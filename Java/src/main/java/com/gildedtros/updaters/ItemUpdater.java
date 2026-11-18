package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * Applies one day of ageing to an item. Each item category has its own subclass;
 * {@link Updaters} picks the right one for an item name.
 */
abstract class ItemUpdater {

    static final int DEFAULT_MIN_QUALITY = 0;
    static final int DEFAULT_MAX_QUALITY = 50;

    private final int minQuality;
    private final int maxQuality;

    /** Uses the default quality range, {@link #DEFAULT_MIN_QUALITY} to {@link #DEFAULT_MAX_QUALITY}. */
    ItemUpdater() {
        this(DEFAULT_MIN_QUALITY, DEFAULT_MAX_QUALITY);
    }

    /** Lets a subclass set its own quality range. */
    ItemUpdater(int minQuality, int maxQuality) {
        if (minQuality > maxQuality) {
            // this denotes a programming mistake
            throw new IllegalArgumentException(
                    "minQuality " + minQuality + " is greater than maxQuality " + maxQuality);
        }
        if (minQuality < 0) {
            // this denotes a programming mistake
            throw new IllegalArgumentException(
                    "minQuality " + minQuality + " is negative");
        }
        this.minQuality = minQuality;
        this.maxQuality = maxQuality;
    }

    abstract void update(Item item);

    public int getMinQuality() {
        return minQuality;
    }

    public int getMaxQuality() {
        return maxQuality;
    }

    /** Raises quality by {@code amount}, but never above {@link #maxQuality}. */
    void increaseQuality(Item item, int amount) {
        if (item.quality < maxQuality) {
            item.quality = Math.min(maxQuality, item.quality + amount);
        }
    }

    /** Lowers quality by {@code amount}, but never below {@link #minQuality}. */
    void decreaseQuality(Item item, int amount) {
        if (item.quality > minQuality) {
            item.quality = Math.max(minQuality, item.quality - amount);
        }
    }

    /** True once the sell-by date has passed (call after decreasing sellIn). */
    boolean isExpired(Item item) {
        return item.sellIn < 0;
    }
}
