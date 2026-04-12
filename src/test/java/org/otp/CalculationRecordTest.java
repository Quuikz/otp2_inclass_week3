package org.otp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CalculationRecord Tests")
class CalculationRecordTest {
    
    @Test
    @DisplayName("Getters return constructor values")
    void gettersReturnConstructorValues() {
        CalculationRecord calculationRecord = new CalculationRecord(210.5, 7.2, 1.95, 15.16, 29.56, "fa_IR");

        assertEquals(210.5, calculationRecord.getDistance());
        assertEquals(7.2, calculationRecord.getConsumption());
        assertEquals(1.95, calculationRecord.getPrice());
        assertEquals(15.16, calculationRecord.getTotalFuel());
        assertEquals(29.56, calculationRecord.getTotalCost());
        assertEquals("fa_IR", calculationRecord.getLanguage());
    }

    @Test
    @DisplayName("CalculationRecord can be created with en_US language")
    void testCreationWithEnglish() {
        CalculationRecord calculationRecord = new CalculationRecord(100, 5, 1.5, 5.0, 7.5, "en_US");
        
        assertEquals("en_US", calculationRecord.getLanguage());
        assertEquals(100, calculationRecord.getDistance());
        assertEquals(5, calculationRecord.getConsumption());
        assertEquals(1.5, calculationRecord.getPrice());
        assertEquals(5.0, calculationRecord.getTotalFuel());
        assertEquals(7.5, calculationRecord.getTotalCost());
    }

    @Test
    @DisplayName("CalculationRecord can be created with fr_FR language")
    void testCreationWithFrench() {
        CalculationRecord calculationRecord = new CalculationRecord(150, 6, 2.0, 9.0, 18.0, "fr_FR");
        
        assertEquals("fr_FR", calculationRecord.getLanguage());
    }

    @Test
    @DisplayName("CalculationRecord can be created with ja_JP language")
    void testCreationWithJapanese() {
        CalculationRecord calculationRecord = new CalculationRecord(200, 7, 2.5, 14.0, 35.0, "ja_JP");
        
        assertEquals("ja_JP", calculationRecord.getLanguage());
    }

    @Test
    @DisplayName("CalculationRecord getter for distance")
    void testDistanceGetter() {
        CalculationRecord calculationRecord = new CalculationRecord(123.45, 5, 2, 6.17, 12.34, "en_US");
        assertEquals(123.45, calculationRecord.getDistance());
    }

    @Test
    @DisplayName("CalculationRecord getter for consumption")
    void testConsumptionGetter() {
        CalculationRecord calculationRecord = new CalculationRecord(100, 6.5, 2, 6.5, 13, "en_US");
        assertEquals(6.5, calculationRecord.getConsumption());
    }

    @Test
    @DisplayName("CalculationRecord getter for price")
    void testPriceGetter() {
        CalculationRecord calculationRecord = new CalculationRecord(100, 5, 2.05, 5, 10.25, "en_US");
        assertEquals(2.05, calculationRecord.getPrice());
    }

    @Test
    @DisplayName("CalculationRecord getter for total fuel")
    void testTotalFuelGetter() {
        CalculationRecord calculationRecord = new CalculationRecord(100, 5, 2, 5.5, 11, "en_US");
        assertEquals(5.5, calculationRecord.getTotalFuel());
    }

    @Test
    @DisplayName("CalculationRecord getter for total cost")
    void testTotalCostGetter() {
        CalculationRecord calculationRecord = new CalculationRecord(100, 5, 2, 5, 10.5, "en_US");
        assertEquals(10.5, calculationRecord.getTotalCost());
    }

    @Test
    @DisplayName("CalculationRecord getter for language")
    void testLanguageGetter() {
        CalculationRecord calculationRecord = new CalculationRecord(100, 5, 2, 5, 10, "fr_FR");
        assertEquals("fr_FR", calculationRecord.getLanguage());
    }

    @ParameterizedTest
    @DisplayName("CalculationRecord with various parameter combinations")
    @CsvSource({
        "100, 5, 1.5, 5.0, 7.5, en_US",
        "200, 6, 2.0, 12.0, 24.0, fr_FR",
        "50, 8, 1.5, 4.0, 6.0, ja_JP",
        "1000, 7, 2.5, 70.0, 175.0, fa_IR"
    })
    void testVariousParameterCombinations(double distance, double consumption, double price,
                                           double totalFuel, double totalCost, String language) {
        CalculationRecord calculationRecord = new CalculationRecord(distance, consumption, price, totalFuel, totalCost,
                language);
        
        assertEquals(distance, calculationRecord.getDistance());
        assertEquals(consumption, calculationRecord.getConsumption());
        assertEquals(price, calculationRecord.getPrice());
        assertEquals(totalFuel, calculationRecord.getTotalFuel());
        assertEquals(totalCost, calculationRecord.getTotalCost());
        assertEquals(language, calculationRecord.getLanguage());
    }

    @Test
    @DisplayName("CalculationRecord with zero values")
    void testRecordWithZeroValues() {
        CalculationRecord calculationRecord = new CalculationRecord(0, 0, 0, 0, 0, "en_US");
        
        assertEquals(0, calculationRecord.getDistance());
        assertEquals(0, calculationRecord.getConsumption());
        assertEquals(0, calculationRecord.getPrice());
        assertEquals(0, calculationRecord.getTotalFuel());
        assertEquals(0, calculationRecord.getTotalCost());
    }

    @Test
    @DisplayName("CalculationRecord with negative values")
    void testRecordWithNegativeValues() {
        CalculationRecord calculationRecord = new CalculationRecord(-100, -5, -2.0, -5.0, -10.0, "en_US");
        
        assertEquals(-100, calculationRecord.getDistance());
        assertEquals(-5, calculationRecord.getConsumption());
        assertEquals(-2.0, calculationRecord.getPrice());
        assertEquals(-5.0, calculationRecord.getTotalFuel());
        assertEquals(-10.0, calculationRecord.getTotalCost());
    }

    @Test
    @DisplayName("CalculationRecord with large values")
    void testRecordWithLargeValues() {
        CalculationRecord calculationRecord = new CalculationRecord(100000, 1000, 100.0, 1000000, 100000000, "en_US");
        
        assertEquals(100000, calculationRecord.getDistance());
        assertEquals(1000, calculationRecord.getConsumption());
        assertEquals(100.0, calculationRecord.getPrice());
        assertEquals(1000000, calculationRecord.getTotalFuel());
        assertEquals(100000000, calculationRecord.getTotalCost());
    }

    @Test
    @DisplayName("CalculationRecord with small fractional values")
    void testRecordWithSmallFractionalValues() {
        CalculationRecord calculationRecord = new CalculationRecord(0.1, 0.01, 0.001, 0.000001, 0.000000001, "en_US");
        
        assertEquals(0.1, calculationRecord.getDistance());
        assertEquals(0.01, calculationRecord.getConsumption());
        assertEquals(0.001, calculationRecord.getPrice());
        assertEquals(0.000001, calculationRecord.getTotalFuel());
        assertEquals(0.000000001, calculationRecord.getTotalCost());
    }

    @Test
    @DisplayName("CalculationRecord calculationRecords can be created multiple times independently")
    void testMultipleRecordInstances() {
        CalculationRecord calculationRecord1 = new CalculationRecord(100, 5, 2, 5, 10, "en_US");
        CalculationRecord calculationRecord2 = new CalculationRecord(200, 6, 3, 12, 36, "fr_FR");
        
        assertNotSame(calculationRecord1, calculationRecord2);
        assertEquals(100, calculationRecord1.getDistance());
        assertEquals(200, calculationRecord2.getDistance());
    }

    @Test
    @DisplayName("CalculationRecord instances have different object references")
    void testRecordEquality() {
        CalculationRecord calculationRecord1 = new CalculationRecord(100, 5, 2, 5, 10, "en_US");
        CalculationRecord calculationRecord2 = new CalculationRecord(100, 5, 2, 5, 10, "en_US");
        
        // Even with same values, they are different objects
        assertNotSame(calculationRecord1, calculationRecord2);
        
        // But their values should be the same
        assertEquals(calculationRecord1.getDistance(), calculationRecord2.getDistance());
        assertEquals(calculationRecord1.getConsumption(), calculationRecord2.getConsumption());
        assertEquals(calculationRecord1.getPrice(), calculationRecord2.getPrice());
    }

    @Test
    @DisplayName("CalculationRecord toString does not return null")
    void testRecordToString() {
        CalculationRecord calculationRecord = new CalculationRecord(100, 5, 2, 5, 10, "en_US");
        String str = calculationRecord.toString();
        
        assertNotNull(str);
        assertFalse(str.isEmpty());
    }
}

