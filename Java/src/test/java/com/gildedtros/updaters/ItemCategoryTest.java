package com.gildedtros.updaters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/** Tests for {@link ItemCategory}: which category handles which item name, and the quality ranges. */
class ItemCategoryTest {

    static Stream<Arguments> specialItems() {
        return Stream.of(
                Arguments.of("Good Wine", ItemCategory.GOOD_WINE),
                Arguments.of("B-DAWG Keychain", ItemCategory.LEGENDARY),
                Arguments.of("Backstage passes for Re:Factor", ItemCategory.BACKSTAGE_PASS),
                Arguments.of("Backstage passes for HAXX", ItemCategory.BACKSTAGE_PASS),
                Arguments.of("Duplicate Code", ItemCategory.SMELLY),
                Arguments.of("Long Methods", ItemCategory.SMELLY),
                Arguments.of("Ugly Variable Names", ItemCategory.SMELLY));
    }

    @ParameterizedTest(name = "\"{0}\" -> {1}")
    @MethodSource("specialItems")
    void specialItemsGetTheirOwnCategory(String name, ItemCategory expected) {
        assertSame(expected, ItemCategory.forName(name));
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
            // the enum constant names are not item names
            "NORMAL",
            "GOOD_WINE",
            // edge case
            "",
    })
    void anyOtherNameIsANormalItem(String name) {
        assertSame(ItemCategory.NORMAL, ItemCategory.forName(name));
    }

    @Test
    void aNullNameIsANormalItem() {
        assertSame(ItemCategory.NORMAL, ItemCategory.forName(null));
    }

    @Test
    void legendaryItemsHaveQuality80() {
        assertEquals(80, ItemCategory.LEGENDARY.getMinQuality());
        assertEquals(80, ItemCategory.LEGENDARY.getMaxQuality());
    }

    @ParameterizedTest
    @EnumSource(value = ItemCategory.class, names = "LEGENDARY", mode = EnumSource.Mode.EXCLUDE)
    void otherCategoriesUseTheDefaultRange(ItemCategory category) {
        assertEquals(0, category.getMinQuality());
        assertEquals(50, category.getMaxQuality());
    }
}
