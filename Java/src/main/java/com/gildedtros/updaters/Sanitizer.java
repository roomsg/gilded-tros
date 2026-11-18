package com.gildedtros.updaters;

import com.gildedtros.Item;

public interface Sanitizer {
    void verify(Stage stage, ItemUpdater itemUpdater, Item item);

    enum Stage {
        INPUT,
        OUTPUT
    }
}
