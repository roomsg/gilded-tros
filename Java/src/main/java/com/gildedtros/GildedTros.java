package com.gildedtros;

import com.gildedtros.sanitizers.Sanitizer;
import com.gildedtros.sanitizers.Sanitizers;
import com.gildedtros.updaters.Updaters;

class GildedTros {
    private final Sanitizer sanitizer = Sanitizers.LOG;
    private final Updaters updaters = new Updaters(sanitizer);

    Item[] items;

    public GildedTros(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        updaters.process(items);
    }
}
