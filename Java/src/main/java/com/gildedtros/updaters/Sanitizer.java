package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * Hook that {@link Updaters} calls around every item update, e.g. to report or correct items whose
 * values fall outside what their updater allows. Ready-made implementations are in {@link Sanitizers}.
 *
 * <p>For each item it is called twice with the same updater and the same {@link Item} instance:
 * once at {@link Stage#INPUT}, before the update, and once at {@link Stage#OUTPUT}, after it.
 * Any exception it throws propagates out of {@link Updaters#process(Item)} and stops the processing.
 */
public interface Sanitizer {

    /**
     * Inspects {@code item} at the given stage of its update.
     *
     * @param stage       whether the item is about to be updated or has just been updated
     * @param itemUpdater the updater handling this item, which gives its allowed quality range
     * @param item        the item itself (not a copy), so its values are as they are at this stage
     */
    void verify(Stage stage, ItemUpdater itemUpdater, Item item);

    /** When during an item update a {@link Sanitizer} is called. */
    enum Stage {
        /** Before the update: the item still has the previous day's values. */
        INPUT,
        /** After the update: the item has its new values. */
        OUTPUT
    }
}
