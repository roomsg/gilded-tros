package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * The only entry point to this package: ages items by one day according to their category.
 * These {@code update} methods are the package's only public API.
 */
public final class ItemUpdaters {

    private ItemUpdaters() {
        // static utility class
    }

    /** Ages a single item by one day. */
    public static void update(Item item) {
        ItemUpdater.forName(item.name).update(item);
    }

    /** Ages every item by one day; each item is updated independently. */
    public static void update(Item[] items) {
        for (Item item : items) {
            update(item);
        }
    }
}
