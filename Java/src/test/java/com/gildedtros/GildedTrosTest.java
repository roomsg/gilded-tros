package com.gildedtros;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GildedTrosTest {

    private static final String GOOD_WINE = "Good Wine";
    private static final String KEYCHAIN = "B-DAWG Keychain";
    private static final String REFACTOR_PASS = "Backstage passes for Re:Factor";
    private static final String HAXX_PASS = "Backstage passes for HAXX";

    /** Runs one day of updateQuality() on a single item and returns it. */
    private static Item afterOneDay(String name, int sellIn, int quality) {
        return afterDays(1, name, sellIn, quality);
    }

    private static Item afterDays(int days, String name, int sellIn, int quality) {
        Item item = new Item(name, sellIn, quality);
        GildedTros app = new GildedTros(new Item[] { item });
        for (int i = 0; i < days; i++) {
            app.updateQuality();
        }
        return item;
    }

    private static void assertItem(Item item, int expectedSellIn, int expectedQuality) {
        assertEquals(expectedSellIn, item.sellIn, "sellIn of " + item.name);
        assertEquals(expectedQuality, item.quality, "quality of " + item.name);
    }

    @Nested
    @DisplayName("Normal items")
    class NormalItems {

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = { "foo", "Ring of Cleansening Code", "Elixir of the SOLID" })
        void sellInAndQualityDecreaseByOneEachDay(String name) {
            assertItem(afterOneDay(name, 10, 20), 9, 19);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> sellIn {2}, quality {3}")
        @CsvSource({
                "1, 10, 0, 9",     // last day before the sell-by date: still normal speed
                "0, 10, -1, 8",    // sell-by date passed: twice as fast
                "-5, 10, -6, 8",
        })
        void qualityDegradesTwiceAsFastAfterSellByDate(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertItem(afterOneDay("foo", sellIn, quality), expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> quality {2}")
        @CsvSource({
                "5, 0, 0",
                "0, 0, 0",
                "0, 1, 0",   // would drop by 2, but stops at 0
                "-1, 1, 0",
        })
        void qualityIsNeverNegative(int sellIn, int quality, int expectedQuality) {
            assertEquals(expectedQuality, afterOneDay("foo", sellIn, quality).quality);
        }

        @Test
        void degradesOverSeveralDays() {
            // 3 days at -1, then 2 days at -2
            assertItem(afterDays(5, "Elixir of the SOLID", 3, 20), -2, 13);
        }
    }

    @Nested
    @DisplayName("Good Wine")
    class GoodWine {

        @Test
        void qualityIncreasesWithAge() {
            assertItem(afterOneDay(GOOD_WINE, 2, 0), 1, 1);
        }

        @ParameterizedTest(name = "sellIn {0}")
        @ValueSource(ints = { 0, -1, -10 })
        void qualityIncreasesTwiceAsFastAfterSellByDate(int sellIn) {
            assertItem(afterOneDay(GOOD_WINE, sellIn, 10), sellIn - 1, 12);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> quality {2}")
        @CsvSource({
                "5, 50, 50",
                "5, 49, 50",
                "0, 49, 50",   // would gain 2, but stops at 50
                "0, 50, 50",
        })
        void qualityIsNeverMoreThan50(int sellIn, int quality, int expectedQuality) {
            assertEquals(expectedQuality, afterOneDay(GOOD_WINE, sellIn, quality).quality);
        }
    }

    @Nested
    @DisplayName("B-DAWG Keychain (legendary)")
    class Keychain {

        @ParameterizedTest(name = "sellIn {0}")
        @ValueSource(ints = { 10, 0, -1 })
        void neverHasToBeSoldAndNeverDecreasesInQuality(int sellIn) {
            assertItem(afterOneDay(KEYCHAIN, sellIn, 80), sellIn, 80);
        }

        @Test
        void staysUnchangedOverManyDays() {
            assertItem(afterDays(30, KEYCHAIN, 0, 80), 0, 80);
        }
    }

    @Nested
    @DisplayName("Backstage passes")
    class BackstagePasses {

        @ParameterizedTest(name = "{0}: sellIn {1} -> quality +{2}")
        @CsvSource({
                REFACTOR_PASS + ", 15, 1",
                REFACTOR_PASS + ", 11, 1",
                REFACTOR_PASS + ", 10, 2",   // 10 days or less
                REFACTOR_PASS + ", 6, 2",
                REFACTOR_PASS + ", 5, 3",    // 5 days or less
                REFACTOR_PASS + ", 1, 3",
                HAXX_PASS + ", 15, 1",
                HAXX_PASS + ", 11, 1",
                HAXX_PASS + ", 10, 2",
                HAXX_PASS + ", 6, 2",
                HAXX_PASS + ", 5, 3",
                HAXX_PASS + ", 1, 3",
        })
        void qualityIncreasesFasterAsTheConferenceApproaches(String name, int sellIn, int increase) {
            assertItem(afterOneDay(name, sellIn, 20), sellIn - 1, 20 + increase);
        }

        @ParameterizedTest(name = "{0}: sellIn {1}")
        @CsvSource({
                REFACTOR_PASS + ", 0",
                REFACTOR_PASS + ", -1",
                HAXX_PASS + ", 0",
                HAXX_PASS + ", -1",
        })
        void qualityDropsToZeroAfterTheConference(String name, int sellIn) {
            assertItem(afterOneDay(name, sellIn, 20), sellIn - 1, 0);
        }

        @ParameterizedTest(name = "{0}: sellIn {1}, quality {2} -> quality 50")
        @CsvSource({
                REFACTOR_PASS + ", 15, 50",
                REFACTOR_PASS + ", 10, 49",
                REFACTOR_PASS + ", 5, 48",
                HAXX_PASS + ", 15, 50",
                HAXX_PASS + ", 10, 49",
                HAXX_PASS + ", 5, 49",
        })
        void qualityIsNeverMoreThan50(String name, int sellIn, int quality) {
            assertEquals(50, afterOneDay(name, sellIn, quality).quality);
        }

        @Test
        void fullLifecycle() {
            // 5 days at +1 (15..11), 5 at +2 (10..6), 5 at +3 (5..1), then the conference is over
            assertItem(afterDays(15, REFACTOR_PASS, 15, 0), 0, 30);
            assertItem(afterDays(16, REFACTOR_PASS, 15, 0), -1, 0);
            assertItem(afterDays(15, HAXX_PASS, 15, 0), 0, 30);
            assertItem(afterDays(16, HAXX_PASS, 15, 0), -1, 0);
        }
    }

    @Nested
    @DisplayName("Smelly items")
    class SmellyItems {

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = { "Duplicate Code", "Long Methods", "Ugly Variable Names" })
        @Disabled("Smelly items feature is not implemented yet")
        void degradeTwiceAsFastAsNormalItems(String name) {
            assertItem(afterOneDay(name, 3, 6), 2, 4);
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = { "Duplicate Code", "Long Methods", "Ugly Variable Names" })
        @Disabled("Smelly items feature is not implemented yet")
        void degradeTwiceAsFastAsNormalItemsAfterSellByDate(String name) {
            assertItem(afterOneDay(name, 0, 10), -1, 6);
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = { "Duplicate Code", "Long Methods", "Ugly Variable Names" })
        @Disabled("Smelly items feature is not implemented yet")
        void qualityIsNeverNegative(String name) {
            assertEquals(0, afterOneDay(name, 0, 3).quality);
        }
    }

    @Test
    void updatesEveryItemIndependently() {
        Item[] items = {
                new Item("foo", 10, 20),
                new Item(GOOD_WINE, 2, 0),
                new Item(KEYCHAIN, 0, 80),
                new Item(HAXX_PASS, 5, 20),
        };
        new GildedTros(items).updateQuality();

        assertItem(items[0], 9, 19);
        assertItem(items[1], 1, 1);
        assertItem(items[2], 0, 80);
        assertItem(items[3], 4, 23);
    }

    @Test
    void handlesAnEmptyInventory() {
        new GildedTros(new Item[0]).updateQuality();
    }
}
