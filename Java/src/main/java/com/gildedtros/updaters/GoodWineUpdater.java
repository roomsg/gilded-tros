package com.gildedtros.updaters;

import com.gildedtros.Item;

/** Gains 1 quality per day, 2 once the sell-by date has passed. */
class GoodWineUpdater extends ItemUpdater {

    @Override
    void update(Item item) {
        item.sellIn--;
        increaseQuality(item, isExpired(item) ? 2 : 1);
    }
}
