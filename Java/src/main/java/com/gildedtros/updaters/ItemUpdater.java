package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * Applies one day of ageing to an item. Each item category has its own subclass;
 * {@link Updaters} picks the right one for an item name.
 */
abstract class ItemUpdater {

    static final int MIN_QUALITY = 0;
    static final int MAX_QUALITY = 50;

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
