package com.gildedtros;

import com.gildedtros.sanitizers.Sanitizers;
import com.gildedtros.updaters.Updaters;

class GildedTros {
    private final Updaters updaters = new Updaters(Sanitizers.LOG);

    Item[] items;

    public GildedTros(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        updaters.process(items);
    }
}
