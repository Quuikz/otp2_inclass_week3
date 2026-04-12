package org.otp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DatabaseConnection Tests")
class DatabaseConnectionTest {
    
    @Test
    @DisplayName("getDBConnection returns open connection")
    void getDBConnectionReturnsOpenConnection() {
        assertDoesNotThrow(() -> {
            try (Connection connection = DatabaseConnection.getDBConnection()) {
                assertNotNull(connection);
                assertFalse(connection.isClosed());
            }
        });
    }

    @Test
    @DisplayName("Multiple calls to getDBConnection return valid connections")
    void testMultipleConnections() {
        assertDoesNotThrow(() -> {
            try (Connection conn1 = DatabaseConnection.getDBConnection();
                 Connection conn2 = DatabaseConnection.getDBConnection()) {
                assertNotNull(conn1);
                assertNotNull(conn2);
                assertFalse(conn1.isClosed());
                assertFalse(conn2.isClosed());
            }
        });
    }

    @Test
    @DisplayName("getDBConnection can be called multiple times successfully")
    void testSequentialConnections() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 3; i++) {
                try (Connection connection = DatabaseConnection.getDBConnection()) {
                    assertNotNull(connection);
                    assertFalse(connection.isClosed());
                }
            }
        });
    }

    @Test
    @DisplayName("Connection can be used for basic operations")
    void testConnectionUsable() {
        assertDoesNotThrow(() -> {
            try (Connection connection = DatabaseConnection.getDBConnection()) {
                assertNotNull(connection);
                var statement = connection.createStatement();
                assertNotNull(statement);
            }
        });
    }

    @Test
    @DisplayName("Connection has autocommit disabled")
    void testConnectionAutoCommitDisabled() {
        assertDoesNotThrow(() -> {
            try (Connection connection = DatabaseConnection.getDBConnection()) {
                assertFalse(connection.getAutoCommit());
            }
        });
    }

    @Test
    @DisplayName("Connection can be committed")
    void testConnectionCommit() {
        assertDoesNotThrow(() -> {
            try (Connection connection = DatabaseConnection.getDBConnection()) {
                connection.commit();
                assertTrue(true);
            }
        });
    }

    @Test
    @DisplayName("Connection can be rolled back")
    void testConnectionRollback() {
        assertDoesNotThrow(() -> {
            try (Connection connection = DatabaseConnection.getDBConnection()) {
                connection.rollback();
                assertTrue(true);
            }
        });
    }
}