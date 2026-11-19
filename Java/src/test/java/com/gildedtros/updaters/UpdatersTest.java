package com.gildedtros.updaters;

import com.gildedtros.Item;
import com.gildedtros.sanitizers.Sanitizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class UpdatersTest {

    private final Sanitizer sanitizer = mock(Sanitizer.class);
    private final Updaters updaters = new Updaters(sanitizer);

    /** Every test must have verified all of its sanitizer calls. */
    @AfterEach
    void noOtherSanitizerCalls() {
        verifyNoMoreInteractions(sanitizer);
    }

    @Test
    void processWithRunsTheSanitizerBeforeAndAfterTheUpdate() {
        ItemUpdater updater = mock(ItemUpdater.class);
        Item item = new Item("foo", 10, 20);

        updaters.processWith(updater, item);

        InOrder inOrder = inOrder(sanitizer, updater);
        inOrder.verify(sanitizer).verify(Sanitizer.Stage.INPUT, updater, item);
        inOrder.verify(updater).update(item);
        inOrder.verify(sanitizer).verify(Sanitizer.Stage.OUTPUT, updater, item);
        verifyNoMoreInteractions(updater);
    }

    @Test
    void processingAnItemUsesTheUpdaterForItsName() {
        Item item = new Item("Good Wine", 2, 0);
        ItemUpdater expected = updaters.forName("Good Wine");

        updaters.process(item);

        verify(sanitizer).verify(Sanitizer.Stage.INPUT, expected, item);
        verify(sanitizer).verify(Sanitizer.Stage.OUTPUT, expected, item);
    }

    @Test
    void processingAnArrayProcessesEachItemInTurn() {
        Item first = new Item("foo", 10, 20);
        Item second = new Item("Good Wine", 2, 0);

        updaters.process(new Item[] { first, second });

        InOrder inOrder = inOrder(sanitizer);
        inOrder.verify(sanitizer).verify(same(Sanitizer.Stage.INPUT), any(ItemUpdater.class), same(first));
        inOrder.verify(sanitizer).verify(same(Sanitizer.Stage.OUTPUT), any(ItemUpdater.class), same(first));
        inOrder.verify(sanitizer).verify(same(Sanitizer.Stage.INPUT), any(ItemUpdater.class), same(second));
        inOrder.verify(sanitizer).verify(same(Sanitizer.Stage.OUTPUT), any(ItemUpdater.class), same(second));
    }

    @Test
    void processingAnEmptyArrayDoesNothing() {
        updaters.process(new Item[0]);

        verifyNoInteractions(sanitizer);
    }

    @Test
    void skipsANullItem() {
        updaters.process((Item) null);

        verifyNoInteractions(sanitizer);
    }

    @Test
    void agesAnItemWithANullNameAsANormalItem() {
        Item item = new Item(null, 10, 20);

        updaters.process(item);

        assertEquals(9, item.sellIn);
        assertEquals(19, item.quality);
        verify(sanitizer).verify(same(Sanitizer.Stage.INPUT), any(NormalItemUpdater.class), same(item));
        verify(sanitizer).verify(same(Sanitizer.Stage.OUTPUT), any(NormalItemUpdater.class), same(item));
    }

    @Test
    void skipsANullItemInAnArray() {
        Item first = new Item("foo", 10, 20);
        Item last = new Item("foo", 10, 20);

        updaters.process(new Item[] { first, null, last });

        // the items around the null one are still processed
        assertEquals(19, first.quality);
        assertEquals(19, last.quality);
        verify(sanitizer).verify(same(Sanitizer.Stage.INPUT), any(ItemUpdater.class), same(first));
        verify(sanitizer).verify(same(Sanitizer.Stage.OUTPUT), any(ItemUpdater.class), same(first));
        verify(sanitizer).verify(same(Sanitizer.Stage.INPUT), any(ItemUpdater.class), same(last));
        verify(sanitizer).verify(same(Sanitizer.Stage.OUTPUT), any(ItemUpdater.class), same(last));
    }

    @Test
    void rejectsANullArray() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> updaters.process((Item[]) null));
        assertEquals("items must not be null", e.getMessage());
    }

    @Test
    void rejectsANullSanitizer() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> new Updaters(null));
        assertEquals("sanitizer must not be null", e.getMessage());
    }
}
