package org.otp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalizationService {

    private static final LocalizationService INSTANCE = new LocalizationService();
    private final Map<String, Map<String, String>> allLocalizedTexts = new HashMap<>();
    private String currentLanguage;

    private LocalizationService() {
        currentLanguage = "en_US"; // default language
    }

    public static LocalizationService getInstance() {
        return INSTANCE;
    }

    private static final String GET_LOCALIZED_TEXTS = """
            SELECT * FROM localization_strings
            WHERE language = ?
            """;

    private void loadLocalizedTexts(String Language) throws SQLException {
        Connection connection = DatabaseConnection.getDBConnection();

        try (connection; PreparedStatement statement = connection.prepareStatement(GET_LOCALIZED_TEXTS)) {
            statement.setString(1, Language);
            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String language = resultSet.getString("language");
                String key = resultSet.getString("key");
                String value = resultSet.getString("value");

                allLocalizedTexts.computeIfAbsent(language, k -> new HashMap<>()).put(key, value);
            }
        }
    }


    public void loadStrings(String language) {
        System.out.println("Loading localized texts for language: " + language);

        if (allLocalizedTexts.isEmpty() || !allLocalizedTexts.containsKey(language)) {
            try {
                loadLocalizedTexts(language);
            } catch (SQLException e) {
                System.out.println("Error loading localized texts: " + e.getMessage());

            }
        }
    }

    public String getString(String key){
        System.out.println("Getting localized text for key: " + key + " in language: " + currentLanguage);

        if(!allLocalizedTexts.containsKey(currentLanguage)){
            loadStrings(currentLanguage);
        }
        System.out.println("Getting localized text for key: " + key + " in language: " + allLocalizedTexts.get(currentLanguage).get(key));
        return allLocalizedTexts.get(currentLanguage).get(key);
    }

    public List<String> getAllKeys() {
        System.out.println("Getting all keys for language: " + currentLanguage);
        if(!allLocalizedTexts.containsKey(currentLanguage)){
            loadStrings(currentLanguage);
        }
        System.out.println("Getting all keys for language: " + allLocalizedTexts.get(currentLanguage).keySet().stream().toList());
        return allLocalizedTexts.get(currentLanguage).keySet().stream().toList();
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(String currentLanguage) {
        this.currentLanguage = currentLanguage;
    }
}
