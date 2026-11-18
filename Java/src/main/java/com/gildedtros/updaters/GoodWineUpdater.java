package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * Gains 1 quality per day, 2 once the sell-by date has passed.
 * TODO - confirm this (original) behavior is intentional (not spec'ed as such)
 */
class GoodWineUpdater extends BaseItemUpdater {

    @Override
    public void update(Item item) {
        item.sellIn--;
        increaseQuality(item, isExpired(item) ? 2 : 1);
    }
}
