package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * Gains quality faster as the conference approaches: +1 per day, +2 with 10 days or less,
 * +3 with 5 days or less. Worthless once the conference is over.
 */
class BackstagePassUpdater extends BaseItemUpdater {

    @Override
    public void update(Item item) {
        int daysLeft = item.sellIn;
        item.sellIn--;

        if (isExpired(item)) {
            item.quality = 0;
        } else if (daysLeft <= 5) {
            increaseQuality(item, 3);
        } else if (daysLeft <= 10) {
            increaseQuality(item, 2);
        } else {
            increaseQuality(item, 1);
        }
    }
}
