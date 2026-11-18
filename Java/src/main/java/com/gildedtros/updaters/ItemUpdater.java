package com.gildedtros.updaters;

import com.gildedtros.Item;

/**
 * Applies one day of ageing to an item. Each item category has its own implementation;
 * {@link Updaters} picks the right one for an item name. The implementations are package-private
 * (they normally extend {@code BaseItemUpdater}); this interface is public so that a
 * {@link com.gildedtros.sanitizers.Sanitizer} can see which updater handles an item and what
 * quality range it allows.
 */
public interface ItemUpdater {

    /**
     * Returns the lowest quality this updater lets an item drop to.
     *
     * @return the minimum quality, never negative
     */
    int getMinQuality();

    /**
     * Returns the highest quality this updater lets an item rise to.
     *
     * @return the maximum quality, never below {@link #getMinQuality()}
     */
    int getMaxQuality();

    /**
     * Ages {@code item} by one day, changing its {@code sellIn} and {@code quality} in place.
     * An update never moves quality past {@link #getMinQuality()} or {@link #getMaxQuality()};
     * a value that is already outside that range is not corrected.
     *
     * @param item the item to age
     */
    void update(Item item);
}
