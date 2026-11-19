package com.gildedtros.updaters;

import com.gildedtros.Item;
import com.gildedtros.sanitizers.Sanitizer;
import com.gildedtros.sanitizers.Sanitizers;

import java.util.Objects;

/**
 * Entry point to this package: ages items by one day according to their category.
 *
 * <p>Each item is handled by the updater for its {@link ItemCategory}, chosen by an exact match on the
 * item's name; any name that isn't a known special item is aged as a normal item. The {@link Sanitizer}
 * given to the constructor (default {@link Sanitizers#NOOP}) sees each item just before
 * ({@link Sanitizer.Stage#INPUT}) and just after ({@link Sanitizer.Stage#OUTPUT}) its update.
 *
 * <p>Instances are immutable and the categories are stateless, so one instance can be reused
 * for any number of days.
 */
public final class Updaters {

    private final Sanitizer sanitizer;

    /** Creates an instance that does no sanitizing ({@link Sanitizers#NOOP}). */
    public Updaters() {
        this(Sanitizers.NOOP);
    }

    /**
     * Creates an instance that calls {@code sanitizer} before and after every item update.
     *
     * @param sanitizer the sanitizer to call, e.g. {@link Sanitizers#LOG}; use {@link Sanitizers#NOOP}
     *                  (or the no-argument constructor) for no sanitizing
     * @throws NullPointerException if {@code sanitizer} is null
     */
    public Updaters(Sanitizer sanitizer) {
        this.sanitizer = Objects.requireNonNull(sanitizer, "sanitizer must not be null");
    }

    /**
     * Ages every item by one day; each item is updated independently, in array order, exactly as
     * {@link #process(Item)} would.
     *
     * @param items the items to age; they are changed in place, and null elements are skipped
     * @throws NullPointerException if {@code items} is null
     */
    public void process(Item[] items) {
        Objects.requireNonNull(items, "items must not be null");
        for (Item item : items) {
            process(item);
        }
    }

    /**
     * Ages a single item by one day, using the updater for its category, with the sanitizer called
     * just before and just after the update.
     *
     * @param item the item to age; its {@code sellIn} and {@code quality} are changed in place.
     *             A null item is skipped: nothing happens and the sanitizer is not called.
     *             An item with a null name is aged as a normal item
     */
    public void process(Item item) {
        if (item == null) {
            return;
        }
        processWith(ItemCategory.forName(item.name), item);
    }

    /** Runs the sanitizer before and after {@code updater} ages {@code item}. Package-private for testing. */
    void processWith(ItemUpdater updater, Item item) {
        sanitizer.verify(Sanitizer.Stage.INPUT, updater, item);
        updater.update(item);
        sanitizer.verify(Sanitizer.Stage.OUTPUT, updater, item);
    }
}
