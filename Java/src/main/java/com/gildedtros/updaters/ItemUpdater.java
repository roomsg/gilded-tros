package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * Applies one day of ageing to an item. Each item category has its own subclass;
 * {@link #forName(String)} picks the right one.
 */
abstract class ItemUpdater {

    static final int MIN_QUALITY = 0;
    static final int MAX_QUALITY = 50;

    static final String GOOD_WINE = "Good Wine";
    static final String B_DAWG_KEYCHAIN = "B-DAWG Keychain";
    static final String REFACTOR_PASS = "Backstage passes for Re:Factor";
    static final String HAXX_PASS = "Backstage passes for HAXX";

    private static final ItemUpdater NORMAL = new NormalItemUpdater();
    private static final ItemUpdater GOOD_WINE_UPDATER = new GoodWineUpdater();
    private static final ItemUpdater LEGENDARY = new LegendaryItemUpdater();
    private static final ItemUpdater BACKSTAGE_PASS = new BackstagePassUpdater();

    /** Item names must match exactly; anything unknown is a normal item. */
    static ItemUpdater forName(String name) {
        switch (name) {
            case GOOD_WINE:
                return GOOD_WINE_UPDATER;
            case B_DAWG_KEYCHAIN:
                return LEGENDARY;
            case REFACTOR_PASS:
            case HAXX_PASS:
                return BACKSTAGE_PASS;
            default:
                return NORMAL;
        }
    }

    abstract void update(Item item);

    /** Raises quality by {@code amount}, but never above {@link #MAX_QUALITY}. */
    static void increaseQuality(Item item, int amount) {
        if (item.quality < MAX_QUALITY) {
            item.quality = Math.min(MAX_QUALITY, item.quality + amount);
        }
    }

    /** Lowers quality by {@code amount}, but never below {@link #MIN_QUALITY}. */
    static void decreaseQuality(Item item, int amount) {
        if (item.quality > MIN_QUALITY) {
            item.quality = Math.max(MIN_QUALITY, item.quality - amount);
        }
    }

    /** True once the sell-by date has passed (call after decreasing sellIn). */
    static boolean isExpired(Item item) {
        return item.sellIn < 0;
    }
}
