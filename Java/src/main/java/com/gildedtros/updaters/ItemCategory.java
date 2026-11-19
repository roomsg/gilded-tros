package com.gildedtros.updaters;

import com.gildedtros.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The item categories, each with its own ageing rule, quality range and the exact item names that
 * belong to it. Any other name, including a null name, is a {@link #NORMAL} item.
 *
 * <p>To add a category, add a constant with its names (and its quality range, if it isn't the
 * default) and implement its {@link #update(Item)}.
 */
enum ItemCategory implements ItemUpdater {

    /** Loses 1 quality per day, 2 once the sell-by date has passed. */
    NORMAL {
        @Override
        public void update(Item item) {
            item.sellIn--;
            decreaseQuality(item, isExpired(item) ? 2 : 1);
        }
    },

    /**
     * Gains 1 quality per day, 2 once the sell-by date has passed.
     * TODO - confirm this (original) behavior is intentional (not spec'ed as such)
     */
    GOOD_WINE("Good Wine") {
        @Override
        public void update(Item item) {
            item.sellIn--;
            increaseQuality(item, isExpired(item) ? 2 : 1);
        }
    },

    /** Legendary items never have to be sold and never change in quality, which is always 80. */
    LEGENDARY(80, 80, "B-DAWG Keychain") {
        @Override
        public void update(Item item) {
            // nothing changes
        }
    },

    /**
     * Gains quality faster as the conference approaches: +1 per day, +2 with 10 days or less,
     * +3 with 5 days or less. Worthless once the conference is over.
     */
    BACKSTAGE_PASS("Backstage passes for Re:Factor", "Backstage passes for HAXX") {
        @Override
        public void update(Item item) {
            int daysLeft = item.sellIn;
            item.sellIn--;
            if (isExpired(item)) {
                item.quality = 0;
            } else if (daysLeft <= 5) {
                increaseQuality(item, 3);
            } else if (daysLeft <= 10) {
                increaseQuality(item, 2);
            } else {
                increaseQuality(item, 1);
            }
        }
    },

    /** Degrades twice as fast as a normal item: 2 quality per day, 4 once the sell-by date has passed. */
    SMELLY("Duplicate Code", "Long Methods", "Ugly Variable Names") {
        @Override
        public void update(Item item) {
            item.sellIn--;
            decreaseQuality(item, isExpired(item) ? 4 : 2);
        }
    };

    static final int DEFAULT_MIN_QUALITY = 0;
    static final int DEFAULT_MAX_QUALITY = 50;

    /** Every special item name, mapped to its category; built once, from the constants' names. */
    private static final Map<String, ItemCategory> BY_NAME;

    static {
        Map<String, ItemCategory> byName = new HashMap<>();
        for (ItemCategory category : values()) {
            for (String name : category.names) {
                ItemCategory previous = byName.put(name, category);
                if (previous != null) {
                    // this denotes a programming mistake
                    throw new IllegalStateException(
                            "'" + name + "' is listed in both " + previous + " and " + category);
                }
            }
        }
        BY_NAME = Collections.unmodifiableMap(byName);
    }

    private final int minQuality;
    private final int maxQuality;
    private final String[] names;

    /** A category with the default quality range, {@link #DEFAULT_MIN_QUALITY} to {@link #DEFAULT_MAX_QUALITY}. */
    ItemCategory(String... names) {
        this(DEFAULT_MIN_QUALITY, DEFAULT_MAX_QUALITY, names);
    }

    /** A category with its own quality range. */
    ItemCategory(int minQuality, int maxQuality, String... names) {
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
        this.names = names;
    }

    /**
     * Returns the category for an item name. Names must match exactly; anything unknown, including
     * a null name, is {@link #NORMAL}.
     *
     * @param name the item name, may be null
     * @return the category, never null
     */
    static ItemCategory forName(String name) {
        return BY_NAME.getOrDefault(name, NORMAL);   // HashMap allows a null key
    }

    @Override
    public int getMinQuality() {
        return minQuality;
    }

    @Override
    public int getMaxQuality() {
        return maxQuality;
    }

    // The helpers below can't be private: each constant's body is an anonymous subclass, which
    // can't call a private method of the enum on itself.

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
