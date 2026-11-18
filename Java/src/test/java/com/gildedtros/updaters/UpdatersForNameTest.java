package com.gildedtros.updaters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests for {@link Updaters#forName(String)}: which updater handles which item name. */
class UpdatersForNameTest {

    private final Updaters updaters = new Updaters();

    static Stream<Arguments> specialItems() {
        return Stream.of(
                Arguments.of("Good Wine", GoodWineUpdater.class),
                Arguments.of("B-DAWG Keychain", LegendaryItemUpdater.class),
                Arguments.of("Backstage passes for Re:Factor", BackstagePassUpdater.class),
                Arguments.of("Backstage passes for HAXX", BackstagePassUpdater.class),
                Arguments.of("Duplicate Code", SmellyItemUpdater.class),
                Arguments.of("Long Methods", SmellyItemUpdater.class),
                Arguments.of("Ugly Variable Names", SmellyItemUpdater.class));
    }

    @ParameterizedTest(name = "\"{0}\" -> {1}")
    @MethodSource("specialItems")
    void specialItemsGetTheirOwnUpdater(String name, Class<?> expectedUpdater) {
        assertEquals(expectedUpdater, updaters.forName(name).getClass());
    }

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = {
            // dummy names
            "foo",
            "Ring of Cleansening Code",
            "Elixir of the SOLID",
            // close to a special name, but names must match exactly
            "Backstage passes for devoxx",
            "Backstage passes",
            "good wine",
            "GOOD WINE",
            "Good Wine ",
            " Good Wine",
            "B-DAWG Keychain 2",
            "Duplicate code",
            // edge case
            "",
    })
    void anyOtherNameIsANormalItem(String name) {
        assertEquals(NormalItemUpdater.class, updaters.forName(name).getClass());
    }

    @Test
    void returnsTheSameSharedInstanceEachTime() {
        assertSame(updaters.forName("Good Wine"), updaters.forName("Good Wine"));
        assertSame(updaters.forName("foo"), updaters.forName("bar"));
    }

    @Test
    void namesInTheSameCategoryShareOneUpdater() {
        assertSame(updaters.forName("Backstage passes for Re:Factor"), updaters.forName("Backstage passes for HAXX"));
        assertSame(updaters.forName("Duplicate Code"), updaters.forName("Long Methods"));
        assertSame(updaters.forName("Duplicate Code"), updaters.forName("Ugly Variable Names"));
    }

    @Test
    void updatersAreSharedAcrossUpdatersInstances() {
        assertSame(updaters.forName("Good Wine"), new Updaters(Sanitizers.LOG).forName("Good Wine"));
    }

    @Test
    void rejectsANullName() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> updaters.forName(null));
        assertEquals("name", e.getMessage());
    }
}
