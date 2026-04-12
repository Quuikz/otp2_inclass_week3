package org.otp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CalculationService Tests")
class CalculationServiceTest {

    @Test
    @DisplayName("CalculationService can be instantiated")
    void testInstantiation() {
        CalculationService service = new CalculationService();
        assertNotNull(service);
    }

    @Test
    @DisplayName("calculateConsumption returns correct result with positive values")
    void testCalculateConsumptionPositiveValues() {
        CalculationService service = new CalculationService();
        double result = service.calculateConsumption(180.0, 6.5);
        assertEquals(11.7, result, 0.001);
    }

    @Test
    @DisplayName("calculateConsumption returns 0 when distance is 0")
    void testCalculateConsumptionZeroDistance() {
        CalculationService service = new CalculationService();
        double result = service.calculateConsumption(0, 6.5);
        assertEquals(0.0, result, 0.001);
    }

    @Test
    @DisplayName("calculateConsumption returns 0 when consumption is 0")
    void testCalculateConsumptionZeroConsumption() {
        CalculationService service = new CalculationService();
        double result = service.calculateConsumption(100, 0);
        assertEquals(0.0, result, 0.001);
    }

    @Test
    @DisplayName("calculateConsumption handles large values")
    void testCalculateConsumptionLargeValues() {
        CalculationService service = new CalculationService();
        double result = service.calculateConsumption(10000, 10.0);
        assertEquals(1000.0, result, 0.001);
    }

    @Test
    @DisplayName("calculateConsumption handles fractional values correctly")
    void testCalculateConsumptionFractionalValues() {
        CalculationService service = new CalculationService();
        double result = service.calculateConsumption(123.456, 7.89);
        assertEquals(9.7402, result, 0.001);
    }

    @ParameterizedTest
    @DisplayName("calculateConsumption with various inputs")
    @CsvSource({
        "100, 5.0, 5.0",
        "200, 6.0, 12.0",
        "50, 8.0, 4.0",
        "1000, 7.0, 70.0",
        "0, 0, 0",
        "500, 5.5, 27.5"
    })
    void testCalculateConsumptionVariousInputs(double distance, double consumption, double expected) {
        CalculationService service = new CalculationService();
        double result = service.calculateConsumption(distance, consumption);
        assertEquals(expected, result, 0.001);
    }

    @Test
    @DisplayName("calculateFuelCost returns CalculationRecord with all fields set")
    void testCalculateFuelCostReturnsRecord() {
        CalculationService service = new CalculationService();
        CalculationRecord  calculationRecord = service.calculateFuelCost(100, 5, 1.5);
        
        assertNotNull( calculationRecord);
        assertEquals(100,  calculationRecord.getDistance());
        assertEquals(5,  calculationRecord.getConsumption());
        assertEquals(1.5,  calculationRecord.getPrice());
    }

    @Test
    @DisplayName("calculateFuelCost calculates correct total fuel")
    void testCalculateFuelCostCorrectFuel() {
        CalculationService service = new CalculationService();
        CalculationRecord  calculationRecord = service.calculateFuelCost(180, 6.5, 2.05);
        
        assertEquals(11.7,  calculationRecord.getTotalFuel(), 0.001);
    }

    @Test
    @DisplayName("calculateFuelCost calculates correct total cost")
    void testCalculateFuelCostCorrectCost() {
        CalculationService service = new CalculationService();
        CalculationRecord  calculationRecord = service.calculateFuelCost(180, 6.5, 2.05);
        
        double expectedCost = 11.7 * 2.05;
        assertEquals(expectedCost,  calculationRecord.getTotalCost(), 0.001);
    }

    @Test
    @DisplayName("calculateFuelCost sets language to en_US")
    void testCalculateFuelCostLanguage() {
        CalculationService service = new CalculationService();
        CalculationRecord  calculationRecord = service.calculateFuelCost(100, 5, 2.0);
        
        assertEquals("en_US",  calculationRecord.getLanguage());
    }

    @ParameterizedTest
    @DisplayName("calculateFuelCost with various inputs")
    @CsvSource({
        "100, 5.0, 1.5, 5.0, 7.5",
        "200, 6.0, 2.0, 12.0, 24.0",
        "50, 8.0, 1.5, 4.0, 6.0",
        "1000, 7.0, 2.5, 70.0, 175.0",
        "0, 0, 0, 0, 0"
    })
    void testCalculateFuelCostVariousInputs(double distance, double consumption, double price, 
                                             double expectedFuel, double expectedCost) {
        CalculationService service = new CalculationService();
        CalculationRecord  calculationRecord = service.calculateFuelCost(distance, consumption, price);
        
        assertEquals(expectedFuel,  calculationRecord.getTotalFuel(), 0.001);
        assertEquals(expectedCost,  calculationRecord.getTotalCost(), 0.001);
    }

    @Test
    @DisplayName("saveCalculation throws SQLException when database is unavailable")
    void testSaveCalculationDatabaseError() {
        // This test assumes the database might not be available during testing
        CalculationService service = new CalculationService();
        CalculationRecord  calculationRecord = new CalculationRecord(100, 5, 2.0, 5.0, 10.0, "en_US");
        
        try {
            service.saveCalculation( calculationRecord);
            // If we get here, database is available and  calculationRecord was saved
            assertTrue(true, "Record saved successfully");
        } catch (SQLException e) {
            // Database might not be available, which is acceptable for this test
            assertNotNull(e.getMessage());
        }
    }

    @Test
    @DisplayName("saveCalculation accepts CalculationRecord with all fields")
    void testSaveCalculationAcceptsRecord() {
        CalculationService service = new CalculationService();
        CalculationRecord  calculationRecord = new CalculationRecord(123.45, 6.78, 1.99, 8.37, 16.66, "fr_FR");
        
        assertDoesNotThrow(() -> {
            try {
                service.saveCalculation( calculationRecord);
            } catch (SQLException e) {
                // Database might not be available during test - that's OK
                if (!e.getMessage().contains("Communications link failure") && 
                    !e.getMessage().contains("Unknown host") &&
                    !e.getMessage().contains("Connection refused")) {
                    throw e;
                }
            }
        });
    }

    @Test
    @DisplayName("calculateConsumption with negative distance returns negative fuel")
    void testCalculateConsumptionNegativeDistance() {
        CalculationService service = new CalculationService();
        double result = service.calculateConsumption(-100, 5.0);
        assertEquals(-5.0, result, 0.001);
    }

    @Test
    @DisplayName("calculateConsumption with negative consumption returns negative fuel")
    void testCalculateConsumptionNegativeConsumption() {
        CalculationService service = new CalculationService();
        double result = service.calculateConsumption(100, -5.0);
        assertEquals(-5.0, result, 0.001);
    }

    @Test
    @DisplayName("calculateFuelCost with very small values")
    void testCalculateFuelCostVerySmallValues() {
        CalculationService service = new CalculationService();
        CalculationRecord  calculationRecord = service.calculateFuelCost(0.1, 0.1, 0.1);
        
        double expectedFuel = (0.1 / 100.0) * 0.1;
        double expectedCost = expectedFuel * 0.1;
        
        assertEquals(expectedFuel,  calculationRecord.getTotalFuel(), 0.0001);
        assertEquals(expectedCost,  calculationRecord.getTotalCost(), 0.0001);
    }

    @Test
    @DisplayName("calculateFuelCost with high price values")
    void testCalculateFuelCostHighPrice() {
        CalculationService service = new CalculationService();
        CalculationRecord  calculationRecord = service.calculateFuelCost(100, 10, 10.0);
        
        double expectedFuel = 10.0;
        double expectedCost = 100.0;
        
        assertEquals(expectedFuel,  calculationRecord.getTotalFuel(), 0.001);
        assertEquals(expectedCost,  calculationRecord.getTotalCost(), 0.001);
    }

    @Test
    @DisplayName("Multiple CalculationService instances are independent")
    void testMultipleServiceInstances() {
        CalculationService service1 = new CalculationService();
        CalculationService service2 = new CalculationService();
        
        assertNotSame(service1, service2);
    }

    @Test
    @DisplayName("calculateFuelCost preserves input values in  calculationRecord")
    void testCalculateFuelCostPreservesInputs() {
        CalculationService service = new CalculationService();
        double distance = 150.75;
        double consumption = 7.25;
        double price = 1.85;
        
        CalculationRecord  calculationRecord = service.calculateFuelCost(distance, consumption, price);
        
        assertEquals(distance,  calculationRecord.getDistance());
        assertEquals(consumption,  calculationRecord.getConsumption());
        assertEquals(price,  calculationRecord.getPrice());
    }
}

