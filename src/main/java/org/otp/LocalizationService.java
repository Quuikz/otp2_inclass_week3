package org.otp;

import org.otp.logging.AppLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalizationService {

    private static final Logger LOGGER = AppLogger.getLogger(LocalizationService.class);
    private final Map<String, Map<String, String>> allLocalizedTexts = new HashMap<>();
    private String currentLanguage;

    public LocalizationService() {
        this.currentLanguage = "en_US"; // default language
    }

    private static final String SQLQUERY = """
            SELECT localization_strings.language, localization_strings.key, localization_strings.value FROM localization_strings
            WHERE language = ?
            """;

    private void loadLocalizedTexts(String language) throws SQLException {
        Connection connection = DatabaseConnection.getDBConnection();

        try (connection; PreparedStatement statement = connection.prepareStatement(SQLQUERY)) {
            statement.setString(1, language);
            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String rowLanguage = resultSet.getString("language");
                String key = resultSet.getString("key");
                String value = resultSet.getString("value");

                allLocalizedTexts.computeIfAbsent(rowLanguage, k -> new HashMap<>()).put(key, value);
            }
        }
    }


    public void loadStrings(String language) {
        LOGGER.fine(() -> "Loading localized texts for language: " + language);

        if (allLocalizedTexts.isEmpty() || !allLocalizedTexts.containsKey(language)) {
            try {
                loadLocalizedTexts(language);
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error loading localized texts", e);
            }
        }
    }

    public String getString(String key){
        LOGGER.fine(() -> "Getting localized text for key: " + key + " in language: " + currentLanguage);

        if(!allLocalizedTexts.containsKey(currentLanguage)){
            loadStrings(currentLanguage);
        }
        LOGGER.fine(() -> "Resolved key " + key + " for language: " + currentLanguage);
        return allLocalizedTexts.get(currentLanguage).get(key);
    }

    public List<String> getAllKeys() {
        LOGGER.fine(() -> "Getting all keys for language: " + currentLanguage);
        if(!allLocalizedTexts.containsKey(currentLanguage)){
            loadStrings(currentLanguage);
        }
        LOGGER.fine(() -> "Loaded key count for " + currentLanguage + ": " + allLocalizedTexts.get(currentLanguage).size());
        return allLocalizedTexts.get(currentLanguage).keySet().stream().toList();
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(String currentLanguage) {
        this.currentLanguage = currentLanguage;
    }
}
