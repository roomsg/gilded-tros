# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

The Java variant of the **Gilded Tros refactoring kata** (a rebrand of Gilded Rose by Axxes). The parent directory holds the same kata in other languages (CSharp, Kotlin, Python, TypeScript, Javascript); this `Java/` folder is self-contained. The spec is `GildedTrosRequirements.txt` (symlink to `../GildedTrosRequirements.txt`) — read it before changing behavior.

The task: refactor `GildedTros.updateQuality()` safely, then add the "smelly items" feature. Both are done.

## Commands

Gradle wrapper (Gradle 9.9.1). Running the build needs **JDK 17+** (tested on 21); the code compiles with `options.release = 8`, so the output stays Java 8 bytecode and Java 8 API (no newer language features or library calls). A `pom.xml` also exists, but it lacks the ApprovalTests and Mockito dependencies, so prefer Gradle. Tests also compile with `--release 8`, so test libraries must be Java 8 builds: Mockito stays on 4.x (5 needs Java 11), and `mockito-junit-jupiter` is left out because it would pull in a newer JUnit than the pinned 5.6.2.

```bash
./gradlew test                                              # run all tests
./gradlew test --tests com.gildedtros.GildedTrosTest        # one test class
./gradlew test --tests 'com.gildedtros.GildedTrosTest$GoodWine'  # one @Nested group
./gradlew build
```

`GildedTrosTest` holds one `@Nested` class per item category, testing against the requirements. It uses dummy names like `"foo"` for normal items. In `src/test/java/com/gildedtros/updaters/`, `UpdatersForNameTest` checks which updater `forName` returns for every special name, near-miss and dummy name; `UpdatersTest` uses Mockito mocks of `Sanitizer` and `ItemUpdater` to verify the call order (INPUT, update, OUTPUT) via `processWith`, and how `process` picks updaters and loops over arrays, and `SanitizersTest` checks the log messages through `Sanitizers.log(messages::add)`; only the `LOG`/`NOOP` tests capture `System.out`.

`TexttestFixture` (in `src/test`) is a `main` that prints inventory state per day for a fixed item list; pass a day count as the first argument. It is the natural basis for a golden-master / ApprovalTests characterization test (`com.approvaltests:approvaltests` is already a Gradle test dependency) to lock current behavior before refactoring.

## Code structure

Under `src/main/java/com/gildedtros/`:

- `GildedTros` holds an `Updaters` instance and `updateQuality()` calls `updaters.process(items)`.
- The `updaters/` subpackage holds all updater classes. Its public API: `Updaters` (`process(Item)` ages one item, `process(Item[])` loops over an array), the `Sanitizer` interface with the `Sanitizers` constants (`NOOP`, the default; `LOG`, which prints quality outside the updater's min/max range to `System.out`; `Sanitizers.log(Consumer<String>)` builds the same sanitizer with any output), which `Updaters` calls before (`INPUT`) and after (`OUTPUT`) each update (in the package-private `processWith(updater, item)`, usable in tests with any updater), and the `ItemUpdater` interface that `Sanitizer.verify` receives. The implementation classes stay package-private; keep it that way when adding categories.
- `Updaters` is also the single place that maps an **exact** item name to a category: a package-private (for testing) `forName` `switch` over private name constants, returning one shared updater instance per category. Unknown names (including other "Backstage passes for …") are normal items.
- `ItemUpdater` (public interface): `getMinQuality()`, `getMaxQuality()` and `update(Item)`.
- `BaseItemUpdater` (abstract, package-private) implements it and is what the category classes extend. It holds the final `minQuality` / `maxQuality` (no-arg constructor: the static `DEFAULT_MIN_QUALITY` 0 and `DEFAULT_MAX_QUALITY` 50; `BaseItemUpdater(min, max)` lets a subclass set its own range, rejecting min > max or min < 0; `LegendaryItemUpdater` uses 80 and 80) plus the helpers `increaseQuality` / `decreaseQuality` / `isExpired`.
- One immutable `BaseItemUpdater` subclass per category (their `update` is `public`, as interface methods must be): `NormalItemUpdater`, `GoodWineUpdater`, `LegendaryItemUpdater`, `BackstagePassUpdater`, `SmellyItemUpdater`. To add a category, add a subclass, plus a shared instance and its `case` lines in `Updaters.forName`.
- The quality helpers leave an out-of-range starting value alone instead of clamping it (e.g. a normal item at 60 drops to 59), which matches the original code's behavior.
- `Item` is a plain data class (`name`, `sellIn`, `quality`).

## Rules from the spec that constrain changes

- **Do not modify `Item` or the `items` field** of `GildedTros` (making `updateQuality`/`items` static is allowed). Add behavior via new classes/wrappers instead.
- Special item names currently hard-coded: `"Good Wine"` (quality increases with age, doubled after sell-by), `"B-DAWG Keychain"` (legendary: sellIn and quality never change; quality is 80), `"Backstage passes for Re:Factor"` and `"Backstage passes for HAXX"` (+2 at ≤10 days, +3 at ≤5 days, 0 after the conference).
- Normal items: −1/day, −2/day once `sellIn` < 0. Quality is never negative and never above 50 (except legendary at 80).
- Smelly items `"Duplicate Code"`, `"Long Methods"`, `"Ugly Variable Names"` degrade twice as fast as normal items: −2/day, −4/day once `sellIn` < 0.
