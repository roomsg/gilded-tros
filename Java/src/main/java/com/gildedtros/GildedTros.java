package com.gildedtros;

import com.gildedtros.updaters.ItemUpdater;

class GildedTros {
    Item[] items;

    public GildedTros(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (Item item : items) {
            ItemUpdater.forName(item.name).update(item);
        }
    }
}
