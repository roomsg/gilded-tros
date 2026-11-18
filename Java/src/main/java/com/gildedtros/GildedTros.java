package com.gildedtros;

import com.gildedtros.updaters.ItemUpdaters;

class GildedTros {
    Item[] items;

    public GildedTros(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        ItemUpdaters.update(items);
    }
}
