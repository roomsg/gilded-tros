package com.gildedtros.sanitizers;

import com.gildedtros.Item;
import com.gildedtros.updaters.ItemUpdater;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * The available {@link Sanitizer} implementations: {@link #NOOP}, {@link #LOG}, and {@link #log(Consumer)}
 * for logging to an output of your choice. Not instantiable.
 */
public final class Sanitizers {

    private Sanitizers() {
        // holder for the Sanitizer implementations
    }

    /** Does nothing and reports nothing; the default for {@link com.gildedtros.updaters.Updaters#Updaters()}. */
    public static final Sanitizer NOOP = new Sanitizer() {
        @Override
        public void verify(Stage stage, ItemUpdater itemUpdater, Item item) {
            // ILB
        }
    };

    /**
     * Reports anomalies to {@code System.out} without changing anything: one line per stage at which an
     * item's quality is below its updater's {@linkplain ItemUpdater#getMinQuality() minimum} or above its
     * {@linkplain ItemUpdater#getMaxQuality() maximum}. Same as {@code log(message -> System.out.println(message))}.
     */
    // a lambda rather than System.out::println, so it uses System.out as it is at the time of logging
    // TODO - discuss if slf4j (or any other logging util) would make more sense
    public static final Sanitizer LOG = log(message -> System.out.println(message));

    /**
     * Creates a reporting only sanitizer, like {@link #LOG}, that passes each anomaly message to
     * {@code output} instead of printing it. It never changes the item.
     *
     * @param output receives one message per anomaly, e.g. {@code logger::warn}, or {@code list::add} in tests
     * @return the new sanitizer
     * @throws NullPointerException if {@code output} is null
     */
    public static Sanitizer log(Consumer<String> output) {
        Objects.requireNonNull(output, "output must not be null");
        return new Sanitizer() {
            @Override
            public void verify(Stage stage, ItemUpdater itemUpdater, Item item) {
                if (item.quality < itemUpdater.getMinQuality()) {
                    output.accept("Anomaly detected on " + stage + ": 'item.quality' is lower than minimum:" + item);
                } else if (item.quality > itemUpdater.getMaxQuality()) {
                    output.accept("Anomaly detected on " + stage + ": 'item.quality' is higher than maximum:" + item);
                }
            }
        };
    }

    // TODO - discuss if the following implementation variants would be useful
    // - report metrics (eg: prometheus) which could trigger alerts
    // - actually modify the anomalous values
}
