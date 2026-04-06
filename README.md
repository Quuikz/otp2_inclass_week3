# SEP2: Database Localization Assignment

Fuel Consumption and Trip Cost Calculator with database-driven localization.

This JavaFX application lets the user enter:

- Trip distance
- Fuel consumption rate
- Fuel price

It then calculates:

- Required fuel
- Total trip cost

All UI text is loaded from the database, and every successful calculation is stored in `calculation_records`.

> **Important:** This is a mandatory in-class assignment. The finished app and screenshots must be submitted together with the GitHub repository link.

## Features

- JavaFX UI defined with FXML
- Dynamic language switching
- Database-backed localization strings
- Saved calculation history in MariaDB/MySQL
- Supported language tags:
  - `en-US`
  - `fr-FR`
  - `ja-JP`
  - `fa-IR` (Persian)

## Project Structure

```text
database/
  db_creation.sql
  seed_localization_strings.sql
src/main/java/org/otp/
  CalculationRecord.java
  DatabaseConnection.java
  FuelCalculatorController.java
  FuelModel.java
  Launcher.java
  LocalizationService.java
  Main.java
src/main/resources/org/otp/
  fuel-calculator-view.fxml
deployment.yaml
Dockerfile
Jenkinsfile
pom.xml
```

## Database Schema

The application uses a database named `fuel_calculator_localization` with two tables.

### `calculation_records`

Stores each calculation performed by the user.

| Column | Type | Description |
| --- | --- | --- |
| `id` | INT AUTO_INCREMENT | Primary key |
| `distance` | DOUBLE | Trip distance entered by the user |
| `consumption` | DOUBLE | Fuel consumption rate |
| `price` | DOUBLE | Fuel price |
| `total_fuel` | DOUBLE | Calculated required fuel |
| `total_cost` | DOUBLE | Calculated trip cost |
| `language` | VARCHAR(10) | Language tag used for the calculation |
| `created_at` | TIMESTAMP | Record creation time |

### `localization_strings`

Stores the localized UI text used by the app.

| Column | Type | Description |
| --- | --- | --- |
| `id` | INT AUTO_INCREMENT | Primary key |
| `key` | VARCHAR(100) | UI message key such as `app.title` |
| `value` | VARCHAR(255) | Localized text |
| `language` | VARCHAR(10) | Language tag such as `en-US` |

The `(key, language)` pair is unique.

## Database Setup

### 1) Create the schema

Run `database/db_creation.sql` in MariaDB/MySQL.

```bash
mysql -u root -p < database/db_creation.sql
```

### 2) Seed localization data

After creating the schema, import `database/seed_localization_strings.sql`.

```bash
mysql -u root -p < database/seed_localization_strings.sql
```

### 3) Verify the seed data

The seed file currently contains translations for:

- English: `en-US`
- French: `fr-FR`
- Japanese: `ja-JP`
- Persian: `fa-IR`

## Configuration

The application reads database settings from environment variables via `.env`.

Required variables:

```env
DB_URL=jdbc:mariadb://localhost:3306/
DB_NAME=fuel_calculator_localization
DB_USER=root
DB_PASSWORD=password
```

Notes:

- `DB_URL` should end with `/` because the application appends `DB_NAME` to it.
- If you use Docker, the database host is `db` inside the container network.
- The current container setup in `deployment.yaml` uses MariaDB 11 and maps the database port to `3307` on the host.

## Build and Run

### Local run

1. Make sure MariaDB/MySQL is running.
2. Import the schema and seed scripts.
3. Create a `.env` file in the project root.
4. Start the app with Maven:

```bash
mvn clean javafx:run
```

### Docker run

The repository includes `deployment.yaml` for a Docker-based setup.

```bash
docker compose -f deployment.yaml up --build
```

If you use the container setup, keep in mind that JavaFX may require a working display/X11 configuration on your system.

## Application Flow

### Initialization

- Default language is English.
- The app loads localized strings from `localization_strings`.
- The UI is initialized from database values, not property files.

### Language switching

- Buttons switch between EN, FR, JP, and IR.
- The UI text is refreshed from the database for the selected language.
- Existing input values remain in the text fields.

### Calculation process

1. Enter distance, consumption, and price.
2. Click **Calculate**.
3. Validate that the values are numeric and non-negative.
4. Compute required fuel and total cost.
5. Show a localized result message.
6. Save the calculation in `calculation_records`.
7. Show success or error feedback.

## Repository Submission Checklist

Make sure the GitHub repository includes:

- [ ] Java source files
- [ ] FXML and CSS resources
- [ ] Database schema script (`schema.sql` or equivalent SQL files)
- [ ] `Dockerfile`
- [ ] `Jenkinsfile`
- [ ] `README.md` with setup and run instructions
- [ ] Optional `docker-compose.yml` / compose file if used

## Screenshot Checklist

Submit screenshots showing:

- [ ] `calculation_records` table with at least 3 rows
- [ ] `localization_strings` table with all four languages
- [ ] The application running in English
- [ ] The application running in French
- [ ] The application running in Japanese
- [ ] The application running in Persian
- [ ] Your name tag visible in every screenshot

## Notes

- The seed data uses language tags such as `en-US` and `fa-IR`.
- If you change the database host, user, or password, update your `.env` file accordingly.
- If localization text does not appear, verify that the seed script was imported successfully.
