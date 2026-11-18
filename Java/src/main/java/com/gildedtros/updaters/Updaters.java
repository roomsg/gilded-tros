package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * The only entry point to this package: ages items by one day according to their category.
 * These {@code process} methods are the package's only public API.
 */
public final class Updaters {

    private static final String GOOD_WINE = "Good Wine";
    private static final String B_DAWG_KEYCHAIN = "B-DAWG Keychain";
    private static final String REFACTOR_PASS = "Backstage passes for Re:Factor";
    private static final String HAXX_PASS = "Backstage passes for HAXX";
    private static final String DUPLICATE_CODE = "Duplicate Code";
    private static final String LONG_METHODS = "Long Methods";
    private static final String UGLY_VARIABLE_NAMES = "Ugly Variable Names";

    private static final ItemUpdater NORMAL = new NormalItemUpdater();
    private static final ItemUpdater GOOD_WINE_UPDATER = new GoodWineUpdater();
    private static final ItemUpdater LEGENDARY = new LegendaryItemUpdater();
    private static final ItemUpdater BACKSTAGE_PASS = new BackstagePassUpdater();
    private static final ItemUpdater SMELLY = new SmellyItemUpdater();

    private Updaters() {
        // static utility class
    }

    /** Ages a single item by one day. */
    public static void process(Item item) {
        forName(item.name).update(item);
    }

    /** Ages every item by one day; each item is updated independently. */
    public static void process(Item[] items) {
        for (Item item : items) {
            process(item);
        }
    }

    /** Item names must match exactly; anything unknown is a normal item. */
    private static ItemUpdater forName(String name) {
        switch (name) {
            case GOOD_WINE:
                return GOOD_WINE_UPDATER;
            case B_DAWG_KEYCHAIN:
                return LEGENDARY;
            case REFACTOR_PASS:
            case HAXX_PASS:
                return BACKSTAGE_PASS;
            case DUPLICATE_CODE:
            case LONG_METHODS:
            case UGLY_VARIABLE_NAMES:
                return SMELLY;
            default:
                return NORMAL;
        }
    }
}
