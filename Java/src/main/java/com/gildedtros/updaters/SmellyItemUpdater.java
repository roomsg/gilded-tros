package com.gildedtros.updaters;

import com.gildedtros.Item;

/** Degrades twice as fast as a normal item: 2 quality per day, 4 once the sell-by date has passed. */
class SmellyItemUpdater extends BaseItemUpdater {

    @Override
    public void update(Item item) {
        item.sellIn--;
        decreaseQuality(item, isExpired(item) ? 4 : 2);
    }
}
