package com.gildedtros.sanitizers;

import com.gildedtros.Item;
import com.gildedtros.updaters.ItemUpdater;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * The available {@link Sanitizer} implementations: {@link #NOOP} and {@link #LOG}. Not instantiable.
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
     * Reports anomalies to {@code System.out} without changing anything: one line per anomaly, at each
     * stage at which an item's name is null, or its quality is below its updater's
     * {@linkplain ItemUpdater#getMinQuality() minimum} or above its
     * {@linkplain ItemUpdater#getMaxQuality() maximum}.
     */
    public static final Sanitizer LOG = log(message -> System.out.println(message));

    /**
     * Creates a reporting only sanitizer, like {@link #LOG}, that passes each anomaly message to
     * {@code output} instead of printing it. It never changes the item. Package-private: {@link #LOG} is
     * built with it, and tests use it to collect the messages.
     *
     * @param output receives one message per anomaly (a null name, or quality out of range),
     *               e.g. {@code list::add} in tests
     * @return the new sanitizer
     * @throws NullPointerException if {@code output} is null
     */
    static Sanitizer log(Consumer<String> output) {
        Objects.requireNonNull(output, "output must not be null");
        return new Sanitizer() {
            @Override
            public void verify(Stage stage, ItemUpdater itemUpdater, Item item) {
                if (item.name == null) {
                    report(output, stage, "'item.name' is null", item);
                }
                if (item.quality < itemUpdater.getMinQuality()) {
                    report(output, stage, "'item.quality' is lower than minimum", item);
                } else if (item.quality > itemUpdater.getMaxQuality()) {
                    report(output, stage, "'item.quality' is higher than maximum", item);
                }
            }
        };
    }

    /** Passes one anomaly message to {@code output}; the single place that defines the message format. */
    private static void report(Consumer<String> output, Sanitizer.Stage stage, String problem, Item item) {
        output.accept("Anomaly detected on " + stage + ": " + problem + " for: {" + item + "}");
    }

    // TODO - discuss if the following implementation variants would be useful
    // - logger using slf4j (iso current System.out)
    // - report metrics (eg: prometheus) which could trigger alerts
    // - an actual sanitizer, actually modifying anomalous values
}
