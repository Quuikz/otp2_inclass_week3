package org.otp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FuelModel {

    private static final String INSERT_CALCULATION_SQL = """
            INSERT INTO calculation_records
            (distance, consumption, price, total_fuel, total_cost, language)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    public void saveCalculation(CalculationRecord record) throws SQLException {
        Connection connection = DatabaseConnection.getDBConnection();
        try (connection; PreparedStatement statement = connection.prepareStatement(INSERT_CALCULATION_SQL)) {

            statement.setDouble(1, record.getDistance());
            statement.setDouble(2, record.getConsumption());
            statement.setDouble(3, record.getPrice());
            statement.setDouble(4, record.getTotalFuel());
            statement.setDouble(5, record.getTotalCost());
            statement.setString(6, record.getLanguage());

            statement.executeUpdate();
            connection.commit();
            System.out.println("Calculation record saved successfully to database.");
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
