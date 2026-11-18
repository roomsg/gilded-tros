package com.gildedtros.updaters;

import com.gildedtros.Item;

import java.util.Objects;

/**
 * Entry point to this package: ages items by one day according to their category.
 *
 * <p>Each item is handled by the updater for its category, chosen by an exact match on the item's
 * name; any name that isn't a known special item is aged as a normal item. The {@link Sanitizer}
 * given to the constructor (default {@link Sanitizers#NOOP}) sees each item just before
 * ({@link Sanitizer.Stage#INPUT}) and just after ({@link Sanitizer.Stage#OUTPUT}) its update.
 *
 * <p>Instances are immutable and the category updaters are stateless, so one instance can be reused
 * for any number of days.
 */
public final class Updaters {

    private static final String GOOD_WINE = "Good Wine";
    private static final String B_DAWG_KEYCHAIN = "B-DAWG Keychain";
    private static final String REFACTOR_PASS = "Backstage passes for Re:Factor";
    private static final String HAXX_PASS = "Backstage passes for HAXX";
    private static final String DUPLICATE_CODE = "Duplicate Code";
    private static final String LONG_METHODS = "Long Methods";
    private static final String UGLY_VARIABLE_NAMES = "Ugly Variable Names";

    private static final ItemUpdater NORMAL = new NormalItemUpdater();
    private static final ItemUpdater GOOD_WINE_UPDATER = new GoodWineUpdater();
    private static final ItemUpdater LEGENDARY = new LegendaryItemUpdater();
    private static final ItemUpdater BACKSTAGE_PASS = new BackstagePassUpdater();
    private static final ItemUpdater SMELLY = new SmellyItemUpdater();

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
     * @param items the items to age; they are changed in place
     * @throws NullPointerException if {@code items} is null, or if an item or its name is null;
     *         the items before that one have already been updated by then
     */
    public void process(Item[] items) {
        for (Item item : items) {
            process(item);
        }
    }

    /**
     * Ages a single item by one day, using the updater for its category, with the sanitizer called
     * just before and just after the update.
     *
     * @param item the item to age; its {@code sellIn} and {@code quality} are changed in place
     * @throws NullPointerException if {@code item} or its name is null
     */
    public void process(Item item) {
        Objects.requireNonNull(item, "item must not be null");
        ItemUpdater updater = forName(item.name);
        processWith(updater, item);
    }

    /** Runs the sanitizer before and after {@code updater} ages {@code item}. Package-private for testing. */
    void processWith(ItemUpdater updater, Item item) {
        verifyInput(updater, item);
        updater.update(item);
        verifyOutput(updater, item);
    }

    /** Item names must match exactly; anything unknown is a normal item. Package-private for testing. */
    ItemUpdater forName(String name) {
        Objects.requireNonNull(name, "name must not be null");
        switch (name) {
            case GOOD_WINE:
                return GOOD_WINE_UPDATER;
            case B_DAWG_KEYCHAIN:
                return LEGENDARY;
            case REFACTOR_PASS:
            case HAXX_PASS:
                return BACKSTAGE_PASS;
            case DUPLICATE_CODE:
            case LONG_METHODS:
            case UGLY_VARIABLE_NAMES:
                return SMELLY;
            default:
                return NORMAL;
        }
    }

    private void verifyInput(ItemUpdater updater, Item item) {
        sanitizer.verify(Sanitizer.Stage.INPUT, updater, item);
    }

    private void verifyOutput(ItemUpdater updater, Item item) {
        sanitizer.verify(Sanitizer.Stage.OUTPUT, updater, item);
    }
}
