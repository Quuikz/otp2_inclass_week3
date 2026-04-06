package org.otp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static HikariDataSource dataSource;

    private static String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return (fallback != null && !fallback.isBlank()) ? fallback : null;
    }

    public static synchronized void initialize() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        Dotenv dotenv;
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e) {
            dotenv = null;
        }
        String dbUrl = firstNonBlank(System.getenv("DB_URL"), dotenv != null ? dotenv.get("DB_URL") : null);
        String dbName = firstNonBlank(System.getenv("DB_NAME"), dotenv != null ? dotenv.get("DB_NAME") : null);
        String dbUser = firstNonBlank(System.getenv("DB_USER"), dotenv != null ? dotenv.get("DB_USER") : null);
        String dbPassword = firstNonBlank(System.getenv("DB_PASSWORD"), dotenv != null ? dotenv.get("DB_PASSWORD") : null);

        if (dbUrl == null || dbName == null || dbUser == null || dbPassword == null) {
            throw new IllegalStateException("Missing DB_URL, DB_NAME, DB_USER, or DB_PASSWORD in system env or .env file");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl + dbName);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(10);
        config.setAutoCommit(false);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getDBConnection() throws SQLException {
        initialize();
        return dataSource.getConnection();
    }

}
