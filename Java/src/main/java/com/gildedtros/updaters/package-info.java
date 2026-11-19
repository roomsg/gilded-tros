/**
 * Ages the inventory of the Gilded Tros by one day, item by item.
 *
 * <p>{@link com.gildedtros.updaters.Updaters} is the entry point: it picks the updater for each
 * item's category by an exact match on the item's name (any other name is a normal item) and runs a
 * {@link com.gildedtros.sanitizers.Sanitizer} just before and just after each update.
 *
 * <p>Every category is a constant of the package-private {@code ItemCategory} enum, which implements
 * {@link com.gildedtros.updaters.ItemUpdater} and lists the item names it handles: normal items, Good
 * Wine, the legendary B-DAWG Keychain, backstage passes and smelly items. Only {@code Updaters} and
 * the {@code ItemUpdater} interface are public, the latter
 * so that sanitizers can see which updater handles an item and what quality range it allows.
 *
 * <p>This package and {@link com.gildedtros.sanitizers} depend on each other.
 */
package com.gildedtros.updaters;
