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

`GildedTrosTest` holds one `@Nested` class per item category, testing against the requirements. It uses dummy names like `"foo"` for normal items. In `src/test/java/com/gildedtros/updaters/`, `ItemCategoryTest` checks which category `ItemCategory.forName` returns for every special name, near-miss and dummy name, and each category's quality range; `UpdatersTest` uses Mockito mocks of `Sanitizer` and `ItemUpdater` to verify the call order (INPUT, update, OUTPUT) via `processWith`, and how `process` picks categories and loops over arrays. In `src/test/java/com/gildedtros/sanitizers/`, `SanitizersTest` checks the log messages through `Sanitizers.log(messages::add)`; only the `LOG`/`NOOP` tests capture `System.out`.

`TexttestFixture` (in `src/test`) is a `main` that prints inventory state per day for a fixed item list; pass a day count as the first argument. It is the natural basis for a golden-master / ApprovalTests characterization test (`com.approvaltests:approvaltests` is already a Gradle test dependency) to lock current behavior before refactoring.

## Code structure

Under `src/main/java/com/gildedtros/`:

- `GildedTros` holds an `Updaters` instance and `updateQuality()` calls `updaters.process(items)`.
- The `updaters/` subpackage holds all updater classes. Its public API: `Updaters` (`process(Item)` ages one item, `process(Item[])` loops over an array) and the `ItemUpdater` interface (which `Sanitizer.verify` receives). `Updaters` calls its sanitizer before (`INPUT`) and after (`OUTPUT`) each update, in the package-private `processWith(updater, item)`, usable in tests with any updater. `ItemCategory` stays package-private; keep it that way.
- The `sanitizers/` subpackage holds the `Sanitizer` interface and the `Sanitizers` constants (`NOOP`, the default; `LOG`, which prints a null name and quality outside the updater's min/max range to `System.out`; the package-private `Sanitizers.log(Consumer<String>)` builds the same sanitizer with any output, for `LOG` itself and for tests). `updaters` and `sanitizers` depend on each other (`Updaters` uses `Sanitizer`/`Sanitizers.NOOP`, `Sanitizer.verify` takes an `ItemUpdater`), so Javadoc links across them use fully qualified names rather than imports.
- `ItemUpdater` (public interface): `getMinQuality()`, `getMaxQuality()` and `update(Item)`.
- `ItemCategory` (package-private enum) implements it, with one constant per category: `NORMAL`, `GOOD_WINE`, `LEGENDARY`, `BACKSTAGE_PASS`, `SMELLY`. Each constant lists its **exact** item names (e.g. `SMELLY("Duplicate Code", "Long Methods", "Ugly Variable Names")`) and has its own `update` body (`public`, as interface methods must be). The static `ItemCategory.forName` looks a name up in a map built from those names; unknown names (including other "Backstage passes for …") and a null name are `NORMAL`. A name listed in two constants fails at class load (`IllegalStateException`).
- Each constant holds a final `minQuality` / `maxQuality`: the default constructor uses `DEFAULT_MIN_QUALITY` 0 and `DEFAULT_MAX_QUALITY` 50, while `ItemCategory(min, max, names...)` sets its own range (rejecting min > max or min < 0); `LEGENDARY` uses 80 and 80. The helpers `increaseQuality` / `decreaseQuality` / `isExpired` are package-private, not private, because the constant bodies are anonymous subclasses that can't call private enum methods. To add a category, add a constant with its names (and range) and its `update`.
- The quality helpers leave an out-of-range starting value alone instead of clamping it (e.g. a normal item at 60 drops to 59), which matches the original code's behavior.
- `Item` is a plain data class (`name`, `sellIn`, `quality`).

## Rules from the spec that constrain changes

- **Do not modify `Item` or the `items` field** of `GildedTros` (making `updateQuality`/`items` static is allowed). Add behavior via new classes/wrappers instead.
- Special item names currently hard-coded: `"Good Wine"` (quality increases with age, doubled after sell-by), `"B-DAWG Keychain"` (legendary: sellIn and quality never change; quality is 80), `"Backstage passes for Re:Factor"` and `"Backstage passes for HAXX"` (+2 at ≤10 days, +3 at ≤5 days, 0 after the conference).
- Normal items: −1/day, −2/day once `sellIn` < 0. Quality is never negative and never above 50 (except legendary at 80).
- Smelly items `"Duplicate Code"`, `"Long Methods"`, `"Ugly Variable Names"` degrade twice as fast as normal items: −2/day, −4/day once `sellIn` < 0.
