# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

The Java variant of the **Gilded Tros refactoring kata** (a rebrand of Gilded Rose by Axxes). The parent directory holds the same kata in other languages (CSharp, Kotlin, Python, TypeScript, Javascript); this `Java/` folder is self-contained. The spec is `GildedTrosRequirements.txt` (symlink to `../GildedTrosRequirements.txt`) — read it before changing behavior.

The task: refactor `GildedTros.updateQuality()` safely, then add the "smelly items" feature.

## Commands

Gradle wrapper (Gradle 9.9.1). Running the build needs **JDK 17+** (tested on 21); the code compiles with `options.release = 8`, so the output stays Java 8 bytecode and Java 8 API (no newer language features or library calls). A `pom.xml` also exists, but it lacks the ApprovalTests dependency, so prefer Gradle.

```bash
./gradlew test                                              # run all tests
./gradlew test --tests com.gildedtros.GildedTrosTest        # one test class
./gradlew test --tests 'com.gildedtros.GildedTrosTest$GoodWine'  # one @Nested group
./gradlew build
```

`GildedTrosTest` holds one `@Nested` class per item category, testing against the requirements. It uses dummy names like `"foo"` for normal items. The smelly-item tests are `@Disabled` until that feature exists; re-enable them when implementing it.

`TexttestFixture` (in `src/test`) is a `main` that prints inventory state per day for a fixed item list; pass a day count as the first argument. It is the natural basis for a golden-master / ApprovalTests characterization test (`com.approvaltests:approvaltests` is already a Gradle test dependency) to lock current behavior before refactoring.

## Code structure

Under `src/main/java/com/gildedtros/`:

- `GildedTros.updateQuality()` calls `ItemUpdaters.update(items)`.
- The `updaters/` subpackage holds all updater classes. Its **only** public API is the final, non-instantiable `ItemUpdaters` class with two static methods: `update(Item)` ages one item, and the convenience `update(Item[])` loops over an array calling `update(Item)` on each. Everything else (`ItemUpdater`, its subclasses, `forName`, helpers, constants) is package-private; keep it that way when adding categories.
- `ItemUpdater` (abstract) is the single place that maps an **exact** item name to a category via a `switch`; unknown names (including other "Backstage passes for …") are normal items. It also holds the name constants, the 0/50 quality limits and the shared `increaseQuality` / `decreaseQuality` / `isExpired` helpers.
- One stateless subclass per category: `NormalItemUpdater`, `GoodWineUpdater`, `LegendaryItemUpdater`, `BackstagePassUpdater`. To add a category (e.g. smelly items), add a subclass and its `case` lines in `forName`.
- The quality helpers leave an out-of-range starting value alone instead of clamping it (e.g. a normal item at 60 drops to 59), which matches the original code's behavior.
- `Item` is a plain data class (`name`, `sellIn`, `quality`).

## Rules from the spec that constrain changes

- **Do not modify `Item` or the `items` field** of `GildedTros` (making `updateQuality`/`items` static is allowed). Add behavior via new classes/wrappers instead.
- Special item names currently hard-coded: `"Good Wine"` (quality increases with age, doubled after sell-by), `"B-DAWG Keychain"` (legendary: sellIn and quality never change; quality is 80), `"Backstage passes for Re:Factor"` and `"Backstage passes for HAXX"` (+2 at ≤10 days, +3 at ≤5 days, 0 after the conference).
- Normal items: −1/day, −2/day once `sellIn` < 0. Quality is never negative and never above 50 (except legendary at 80).
- New feature (not yet implemented): smelly items `"Duplicate Code"`, `"Long Methods"`, `"Ugly Variable Names"` degrade twice as fast as normal items.
