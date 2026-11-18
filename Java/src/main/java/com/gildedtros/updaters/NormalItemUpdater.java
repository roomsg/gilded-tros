package com.gildedtros.updaters;

import com.gildedtros.Item;

/** Loses 1 quality per day, 2 once the sell-by date has passed. */
class NormalItemUpdater extends ItemUpdater {

    @Override
    public void update(Item item) {
        item.sellIn--;
        decreaseQuality(item, isExpired(item) ? 2 : 1);
    }
}
