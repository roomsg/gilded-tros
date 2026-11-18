package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * The only entry point to this package: ages items by one day according to their category.
 * These {@code process} methods are the package's only public API.
 */
public final class Updaters {

    private Updaters() {
        // static utility class
    }

    /** Ages a single item by one day. */
    public static void process(Item item) {
        ItemUpdater.forName(item.name).update(item);
    }

    /** Ages every item by one day; each item is updated independently. */
    public static void process(Item[] items) {
        for (Item item : items) {
            process(item);
        }
    }
}
