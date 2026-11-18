package com.gildedtros.updaters;

import com.gildedtros.Item;

/** Legendary items never have to be sold and never change in quality. */
class LegendaryItemUpdater extends ItemUpdater {

    @Override
    void update(Item item) {
        // nothing changes
    }
}
