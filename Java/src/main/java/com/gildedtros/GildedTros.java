package com.gildedtros;

import com.gildedtros.updaters.Updaters;

class GildedTros {
    Item[] items;

    public GildedTros(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        Updaters.process(items);
    }
}
