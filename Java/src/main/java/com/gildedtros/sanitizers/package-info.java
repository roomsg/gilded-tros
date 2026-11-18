/**
 * Checks on items around their daily update, e.g. to report quality outside the allowed range.
 *
 * <p>A {@link com.gildedtros.sanitizers.Sanitizer} is given to
 * {@link com.gildedtros.updaters.Updaters#Updaters(Sanitizer)} and called for every item at the
 * {@link com.gildedtros.sanitizers.Sanitizer.Stage#INPUT INPUT} and
 * {@link com.gildedtros.sanitizers.Sanitizer.Stage#OUTPUT OUTPUT} stage of its update.
 * {@link com.gildedtros.sanitizers.Sanitizers} holds the ready-made ones: {@code NOOP} (the default)
 * and {@code LOG}.
 *
 * <p>This package and {@link com.gildedtros.updaters} depend on each other.
 */
package com.gildedtros.sanitizers;
