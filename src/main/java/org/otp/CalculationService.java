package org.otp;

import org.otp.logging.AppLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class CalculationService {

    private static final Logger LOGGER = AppLogger.getLogger(CalculationService.class);

    private static final String INSERT_CALCULATION_SQL = """
            INSERT INTO calculation_records
            (distance, consumption, price, total_fuel, total_cost, language)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    public double calculateConsumption(double distance, double consumption) {
        LOGGER.fine(() -> "Calculating consumption for distance: " + distance + " km, consumption rate: " + consumption + " L/100km");
        double totalFuel = (consumption / 100.0) * distance;
        LOGGER.fine(() -> "Total fuel calculated: " + totalFuel + " L");
        return totalFuel;
    }

    public CalculationRecord calculateFuelCost(double distance, double consumption, double price) {
        double totalFuel = calculateConsumption(distance, consumption);
        double totalCost = totalFuel * price;
        LOGGER.fine(() -> "Total cost calculated: " + totalCost);
        return new CalculationRecord(distance, consumption, price, totalFuel, totalCost, "en_US");
    }

    public void saveCalculation(CalculationRecord calculationRecord) throws SQLException {
        Connection connection = DatabaseConnection.getDBConnection();
        try (connection; PreparedStatement statement = connection.prepareStatement(INSERT_CALCULATION_SQL)) {

            statement.setDouble(1, calculationRecord.getDistance());
            statement.setDouble(2, calculationRecord.getConsumption());
            statement.setDouble(3, calculationRecord.getPrice());
            statement.setDouble(4, calculationRecord.getTotalFuel());
            statement.setDouble(5, calculationRecord.getTotalCost());
            statement.setString(6, calculationRecord.getLanguage());

            statement.executeUpdate();
            connection.commit();
            LOGGER.info("Calculation record saved successfully to database.");
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                ex.addSuppressed(rollbackEx);
            }
            throw ex;
        }
    }
}
