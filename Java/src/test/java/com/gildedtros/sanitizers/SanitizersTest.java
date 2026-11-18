package com.gildedtros.sanitizers;

import com.gildedtros.Item;
import com.gildedtros.updaters.Updaters;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SanitizersTest {

    private final List<String> messages = new ArrayList<>();
    private final Updaters logging = new Updaters(Sanitizers.log(messages::add));

    private static void assertMessage(String message, String stage, String problem, String item) {
        assertTrue(message.contains("on " + stage + ":"), message);
        assertTrue(message.contains(problem), message);
        assertTrue(message.endsWith(item), message);
    }

    @Test
    void logReportsQualityAboveMaximumBeforeAndAfterTheUpdate() {
        logging.process(new Item("foo", 10, 60));

        assertEquals(2, messages.size());
        assertMessage(messages.get(0), "INPUT", "higher than maximum", "foo, 10, 60");
        assertMessage(messages.get(1), "OUTPUT", "higher than maximum", "foo, 9, 59");
    }

    @Test
    void logReportsQualityBelowMinimumBeforeAndAfterTheUpdate() {
        logging.process(new Item("foo", 10, -5));

        assertEquals(2, messages.size());
        assertMessage(messages.get(0), "INPUT", "lower than minimum", "foo, 10, -5");
        assertMessage(messages.get(1), "OUTPUT", "lower than minimum", "foo, 9, -5");
    }

    @Test
    void logUsesTheRangeOfTheItemsOwnUpdater() {
        logging.process(new Item("B-DAWG Keychain", 0, 80));   // 80 is valid for legendary items only
        assertEquals(0, messages.size());

        logging.process(new Item("B-DAWG Keychain", 0, 79));
        assertEquals(2, messages.size());
        assertMessage(messages.get(0), "INPUT", "lower than minimum", "B-DAWG Keychain, 0, 79");
    }

    @Test
    void logIsSilentForValidItems() {
        logging.process(new Item[] {
                new Item("foo", 10, 20),
                new Item("Good Wine", 0, 50),
                new Item("Backstage passes for HAXX", 0, 30),
                new Item("Duplicate Code", 0, 0),
        });

        assertEquals(0, messages.size());
    }

    @Test
    void logRejectsANullOutput() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> Sanitizers.log(null));
        assertEquals("output must not be null", e.getMessage());
    }

    /** LOG must print the same messages to System.out. */
    @Test
    void logConstantPrintsToSystemOut() {
        String printed = systemOutDuring(() -> new Updaters(Sanitizers.LOG).process(new Item("foo", 10, 60)));

        String[] lines = printed.trim().split("\\R");
        assertEquals(2, lines.length);
        assertMessage(lines[0], "INPUT", "higher than maximum", "foo, 10, 60");
        assertMessage(lines[1], "OUTPUT", "higher than maximum", "foo, 9, 59");
    }

    @Test
    void noopIsSilentEvenForInvalidItems() {
        assertDoesNotSanitize(new Updaters(Sanitizers.NOOP));
    }

    @Test
    void defaultUpdatersUseTheNoopSanitizer() {
        assertDoesNotSanitize(new Updaters());
    }

    /** NOOP has no output to inject, so check it leaves System.out alone and doesn't touch the item. */
    private static void assertDoesNotSanitize(Updaters updaters) {
        Item item = new Item("foo", 10, 60);

        assertEquals("", systemOutDuring(() -> updaters.process(item)));
        assertEquals(59, item.quality);   // a normal update, nothing clamped
    }

    /** Runs {@code action} and returns what it printed to System.out, restoring System.out afterwards. */
    private static String systemOutDuring(Runnable action) {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(captured, true));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return captured.toString();
    }
}
