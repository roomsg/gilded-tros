package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * Applies one day of ageing to an item. Each item category has its own implementation;
 * {@link Updaters} picks the right one for an item name. Implementations normally extend
 * {@link BaseItemUpdater}.
 */
interface ItemUpdater {

    /** Lowest quality this updater lets an item reach. */
    int getMinQuality();

    /** Highest quality this updater lets an item reach. */
    int getMaxQuality();

    /** Ages {@code item} by one day. */
    void update(Item item);
}
