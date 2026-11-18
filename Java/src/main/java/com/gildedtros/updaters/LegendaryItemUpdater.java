package com.gildedtros.updaters;

import com.gildedtros.Item;

/** Legendary items never have to be sold and never change in quality, which is always 80. */
class LegendaryItemUpdater extends ItemUpdater {

    LegendaryItemUpdater() {
        super(80, 80);
    }

    @Override
    void update(Item item) {
        // nothing changes
    }
}
