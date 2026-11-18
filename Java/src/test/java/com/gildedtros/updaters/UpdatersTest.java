package com.gildedtros.updaters;

import com.gildedtros.Item;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdatersTest {

    /** One call to Sanitizer.handle, with the item's values as they were at that moment. */
    private static final class Call {
        final Sanitizer.Stage stage;
        final ItemUpdater updater;
        final String name;
        final int sellIn;
        final int quality;

        Call(Sanitizer.Stage stage, ItemUpdater updater, Item item) {
            this.stage = stage;
            this.updater = updater;
            this.name = item.name;
            this.sellIn = item.sellIn;
            this.quality = item.quality;
        }
    }

    private final List<Call> calls = new ArrayList<>();
    private final Updaters updaters = new Updaters((stage, updater, item) -> calls.add(new Call(stage, updater, item)));

    @Test
    void callsTheGivenSanitizerBeforeAndAfterTheUpdate() {
        updaters.process(new Item("foo", 10, 20));

        assertEquals(2, calls.size());
        assertEquals(Sanitizer.Stage.INPUT, calls.get(0).stage);
        assertEquals(10, calls.get(0).sellIn);
        assertEquals(20, calls.get(0).quality);
        assertEquals(Sanitizer.Stage.OUTPUT, calls.get(1).stage);
        assertEquals(9, calls.get(1).sellIn);
        assertEquals(19, calls.get(1).quality);
        assertSame(calls.get(0).updater, calls.get(1).updater);
    }

    @Test
    void processingAnArrayCallsTheSanitizerAroundEachItemInTurn() {
        updaters.process(new Item[] { new Item("foo", 10, 20), new Item("Good Wine", 2, 0) });

        assertEquals(4, calls.size());
        assertCall(calls.get(0), Sanitizer.Stage.INPUT, "foo");
        assertCall(calls.get(1), Sanitizer.Stage.OUTPUT, "foo");
        assertCall(calls.get(2), Sanitizer.Stage.INPUT, "Good Wine");
        assertCall(calls.get(3), Sanitizer.Stage.OUTPUT, "Good Wine");
    }

    @Test
    void rejectsANullSanitizer() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> new Updaters(null));
        assertEquals("sanitizer", e.getMessage());
    }

    private static void assertCall(Call call, Sanitizer.Stage expectedStage, String expectedName) {
        assertEquals(expectedStage, call.stage);
        assertEquals(expectedName, call.name);
    }
}
