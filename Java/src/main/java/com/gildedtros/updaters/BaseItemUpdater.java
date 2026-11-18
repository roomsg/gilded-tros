package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * Base class for the item updaters: holds the quality range and the helpers that respect it.
 * Subclasses only implement {@link #update(Item)}.
 */
abstract class BaseItemUpdater implements ItemUpdater {

    static final int DEFAULT_MIN_QUALITY = 0;
    static final int DEFAULT_MAX_QUALITY = 50;

    private final int minQuality;
    private final int maxQuality;

    /** Uses the default quality range, {@link #DEFAULT_MIN_QUALITY} to {@link #DEFAULT_MAX_QUALITY}. */
    BaseItemUpdater() {
        this(DEFAULT_MIN_QUALITY, DEFAULT_MAX_QUALITY);
    }

    /** Lets a subclass set its own quality range. */
    BaseItemUpdater(int minQuality, int maxQuality) {
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

    @Override
    public int getMinQuality() {
        return minQuality;
    }

    @Override
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
