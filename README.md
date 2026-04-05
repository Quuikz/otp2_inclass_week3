# SEP2: Database Localization Assignment (Fall 2026)

## Week 3 / AD

## Description of the Exercise (Extend Week 2 Assignment with DB)

### Fuel Consumption and Trip Cost Calculator with Multi-Language Support and Database Integration

This exercise demonstrates how to build a JavaFX application that calculates fuel consumption and total trip cost with multi-language support (English, French, Japanese, and Persian).

Users enter:
- Trip distance
- Fuel consumption rate
- Fuel price

The application calculates the required fuel and total cost and displays results in the selected language.

All calculation records must be stored in a database, and UI messages must be loaded from database tables instead of property files.

> **Mandatory in-class assignment:** This assignment is directly related to your project implementation. Students who fail to submit the assignment in Oma will receive **0** for Sprint 6 implementation. You are required to submit screenshots of the running application along with the project's GitHub repository link.

## Summary of Tasks

- Read UI messages from a database table instead of `ResourceBundle` property files
- Save calculation records into the `calculation_records` table using a `CalculationService` class
- Implement database-driven localization for all UI text

## 1. Create the Database

A sample database is shown below. You may adjust it if necessary.

```sql
CREATE DATABASE IF NOT EXISTS fuel_calculator_localization
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fuel_calculator_localization;

CREATE TABLE IF NOT EXISTS calculation_records (
  id INT AUTO_INCREMENT PRIMARY KEY,
  distance DOUBLE NOT NULL,
  consumption DOUBLE NOT NULL,
  price DOUBLE NOT NULL,
  total_fuel DOUBLE NOT NULL,
  total_cost DOUBLE NOT NULL,
  language VARCHAR(10),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS localization_strings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  `key` VARCHAR(100) NOT NULL,
  value VARCHAR(255) NOT NULL,
  language VARCHAR(10) NOT NULL,
  UNIQUE KEY unique_key_lang (`key`, `language`)
);
```

## 2. Add `CalculationService` Class

Create a class that connects to MySQL/MariaDB and stores calculation records.

Requirements:
- Save calculation record (`distance`, `consumption`, `price`, `total_fuel`, `total_cost`, `language`)
- Handle database connection properly
- Use prepared statements to prevent SQL injection
- Implement proper error handling

Required methods:
- `saveCalculation(CalculationRecord record)` - saves a calculation record
- `getConnection()` - establishes database connection

## 3. Add `LocalizationService` Class

Create a class that fetches localized UI strings from the database.

Requirements:
- Query `localization_strings` table based on selected language
- Return a map of key-value pairs
- Handle database connection properly
- Cache loaded strings for performance optimization

Required methods:
- `loadStrings(String language)` - loads all UI strings for a language
- `getString(String key)` - returns a specific localized string
- `getAllKeys()` - returns all available keys for current language

## 4. Database Connection Management

Create a `DatabaseConnection` class including:

```java
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/fuel_calculator_localization";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

Requirements:
- Database URL configuration
- Username and password (configurable via external properties file)
- Proper connection handling (open/close)
- Exception handling for connection failures

## 5. Main Components and Flow (Similar to Week 2)

### FXML Layout

- UI defined using FXML
- Includes labels, text fields, calculate button, and result display
- Language buttons (EN, FR, JP, IR) for dynamic switching

### Controller Class

- Handles user interactions
- Uses `LocalizationService` to load language resources from database
- Updates UI dynamically with strings from database
- Calls `CalculationService` to save records after successful calculation

## 6. Controller Logic (Update to Support DB)

### Language Support

- `setLanguage(locale)` loads strings from database based on locale
- Updates UI labels dynamically using loaded strings from `localization_strings` table
- Displays an error if database connection fails or strings are not found
- No property files should be used

## 7. Application Flow

### Initialization

- Default language: English
- Establish database connection
- Load English strings from `localization_strings` table
- Initialize UI with loaded strings from database

### Language Change

- Buttons switch locale
- Query database for strings in the new language
- Update UI labels with new strings from database
- Maintain current calculation values

### Calculation Process

1. User enters values in text fields
2. User clicks **Calculate** button
3. Validate input values (positive numbers)
4. Compute total fuel and total cost
5. Display localized result message with calculated values
6. Save calculation record to `calculation_records` table
7. Display success confirmation or error message

## Submission Requirements

### 1. GitHub Repository Including

- Source code (Java files, FXML, CSS)
- Database schema file (`schema.sql`)
- `Dockerfile`
- `Jenkinsfile`
- `docker-compose.yml` (optional)
- `README` with setup instructions

### 2. Screenshots Showing

- `calculation_records` table with at least 3 records showing:
  `distance`, `consumption`, `price`, `total_fuel`, `total_cost`, `language`, `timestamp`
- `localization_strings` table with key-value pairs for all four languages (EN, FR, JP, IR)
- Application screenshots in all four languages (EN, FR, JP, IR) showing calculations
- Your name tag visible in all screenshots

### 3. Database Configuration

- Provide clear instructions for setting up the database in `README`
- Document database connection configuration requirements
- Include sample data insertion script (optional but recommended)

