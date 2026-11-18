package com.gildedtros.updaters;

import com.gildedtros.Item;

import java.util.Objects;
import java.util.function.Consumer;

public final class Sanitizers {

    private Sanitizers() {
        // holder for the Sanitizer implementations
    }

    // NOOP sanitizer, not doing/reporting anything
    public static final Sanitizer NOOP = new Sanitizer() {
        @Override
        public void verify(Stage stage, ItemUpdater itemUpdater, Item item) {
            // ILB
        }
    };

    // reporting only sanitizer, simply logging anomalies to System.out without changing anything
    // (a lambda rather than System.out::println, so it uses System.out as it is at the time of logging)
    // TODO - discuss if slf4j (or any other logging util) would make more sense
    public static final Sanitizer LOG = log(message -> System.out.println(message));

    /**
     * Reporting only sanitizer, like {@link #LOG}, but passing each anomaly message to {@code output}
     * (e.g. a logger, or a list in tests) instead of printing it.
     */
    public static Sanitizer log(Consumer<String> output) {
        Objects.requireNonNull(output, "output");
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
