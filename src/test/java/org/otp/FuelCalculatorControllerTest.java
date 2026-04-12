package org.otp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;

import java.net.URL;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import static org.junit.jupiter.api.Assertions.*;

class FuelCalculatorControllerTest extends ApplicationTest {

    private FuelCalculatorController controller;
    private TestLocalizationService localizationService;

    private static class TestLocalizationService extends LocalizationService {
        private final Map<String, Map<String, String>> texts = new HashMap<>();
        private String currentLanguage = "en_US";
        private String failingLanguage;

        TestLocalizationService() {
            Map<String, String> en = new HashMap<>();
            en.put("distance.label", "Distance (km)");
            en.put("consumption.label", "Fuel Consumption (L/100 km)");
            en.put("price.label", "Fuel Price (per liter)");
            en.put("calculate.button", "Calculate Trip Cost");
            en.put("result.placeholder", "Result will appear here.");
            en.put("result.label", "Fuel: {0}, Cost: {1}");
            en.put("invalid.input", "Invalid input");
            en.put("database.error", "Database error");
            texts.put("en_US", en);

            Map<String, String> fr = new HashMap<>(en);
            fr.put("distance.label", "Distance");
            fr.put("calculate.button", "Calculer");
            texts.put("fr_FR", fr);

            Map<String, String> fa = new HashMap<>(en);
            fa.put("distance.label", "Distance FA");
            texts.put("fa_IR", fa);

            Map<String, String> ja = new HashMap<>(en);
            ja.put("distance.label", "Distance JP");
            texts.put("ja_JP", ja);
        }

        void failOnLanguage(String language) {
            this.failingLanguage = language;
        }

        @Override
        public void loadStrings(String language) {
            if (language != null && language.equals(failingLanguage)) {
                throw new MissingResourceException("Missing test language", "TestLocalizationService", language);
            }
            // In tests, values are preloaded; nothing to fetch.
        }

        @Override
        public String getString(String key) {
            return texts.getOrDefault(currentLanguage, texts.get("en_US")).getOrDefault(key, key);
        }

        @Override
        public List<String> getAllKeys() {
            return texts.getOrDefault(currentLanguage, texts.get("en_US")).keySet().stream().toList();
        }

        @Override
        public String getCurrentLanguage() {
            return currentLanguage;
        }

        @Override
        public void setCurrentLanguage(String currentLanguage) {
            this.currentLanguage = currentLanguage;
        }
    }

    @BeforeAll
    static void setupHeadlessMode() {
        // This enables "Headless" mode so no windows pop up during tests
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
    }

    @Override
    public void start(Stage stage) throws Exception {
        // We manually instantiate the controller to inject the service
        localizationService = new TestLocalizationService();
        localizationService.setCurrentLanguage("en_US");
        localizationService.loadStrings("en_US"); // Start with English
        controller = new FuelCalculatorController(localizationService);
        injectCalculationService(controller, new CalculationService() {
            @Override
            public void saveCalculation(CalculationRecord calculationRecord) {
                // Test stub keeps this test independent from DB availability.
            }
        });

        URL fxmlUrl = getClass().getResource("/org/otp/fuel-calculator-view.fxml");
        assertNotNull(fxmlUrl, "FXML resource '/org/otp/fuel-calculator-view.fxml' was not found on classpath");

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        loader.setControllerFactory(param -> controller);

        VBox root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private static void injectCalculationService(FuelCalculatorController target, CalculationService replacement) {
        try {
            Field field = FuelCalculatorController.class.getDeclaredField("calculationService");
            field.setAccessible(true);
            field.set(target, replacement);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Unable to inject calculationService for tests", e);
        }
    }

    private Label resultLabel() {
        return lookup("#lblResult").queryAs(Label.class);
    }

    @Test
    void testSuccessfulCalculationFlow() {
        // Simulating user input
        clickOn("#txtDistance").write("100");
        clickOn("#txtConsumption").write("5.0");
        clickOn("#txtPrice").write("2.0");

        // Clicking the calculate button
        clickOn("#btnCalculate");

        // Verify the result label contains expected keywords (Fuel: 5.00, Cost: 10.00)
        // Note: The actual string depends on your properties file format
        FxAssert.verifyThat("#lblResult", LabeledMatchers.hasText(org.hamcrest.CoreMatchers.containsString("5.00")));
        FxAssert.verifyThat("#lblResult", LabeledMatchers.hasText(org.hamcrest.CoreMatchers.containsString("10.00")));
    }

    @Test
    void testInvalidInputShowsErrorMessage() {
        clickOn("#txtDistance").write("abc"); // Invalid numeric string
        clickOn("#btnCalculate");

        assertEquals("Invalid input", resultLabel().getText());
    }

    @Test
    void testLanguageSwitchingToFrench() {
        clickOn("#btnFR");

        Label distanceLabel = lookup("#lblDistance").queryAs(Label.class);
        assertEquals("Distance", distanceLabel.getText());
        assertEquals(javafx.geometry.NodeOrientation.LEFT_TO_RIGHT, distanceLabel.getScene().getRoot().getNodeOrientation());
    }
    @Test
    void testLanguageSwitchingToEnglish() {
        clickOn("#btnEN");

        Label distanceLabel = lookup("#lblDistance").queryAs(Label.class);
        assertEquals("Distance (km)", distanceLabel.getText());
    }
    @Test
    void testLanguageSwitchingToJapanese() {
        clickOn("#btnJP");

        Label distanceLabel = lookup("#lblDistance").queryAs(Label.class);
        assertEquals("Distance JP", distanceLabel.getText());
    }

    @Test
    void testPersianLayoutDirection() {
        clickOn("#btnIR");

        // Find the root node and check if orientation is Right-to-Left
        Label lbl = lookup("#lblDistance").queryAs(Label.class);
        assertEquals(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT, lbl.getScene().getRoot().getNodeOrientation());
    }

    @Test
    void testNegativeNumbersHandling() {
        clickOn("#txtDistance").write("-100");
        clickOn("#txtConsumption").write("5");
        clickOn("#txtPrice").write("2");
        clickOn("#btnCalculate");

        assertEquals("Invalid input", resultLabel().getText());
    }

    @Test
    void testDatabaseErrorShowsLocalizedMessage() {
        injectCalculationService(controller, new CalculationService() {
            @Override
            public void saveCalculation(CalculationRecord calculationRecord) throws SQLException {
                throw new SQLException("forced for test");
            }
        });

        clickOn("#txtDistance").write("100");
        clickOn("#txtConsumption").write("5");
        clickOn("#txtPrice").write("2");
        clickOn("#btnCalculate");

        assertEquals("Database error", resultLabel().getText());
    }

    @Test
    void testResultPlaceholderAppliedWhenResultBlank() {
        interact(() -> resultLabel().setText("   "));

        clickOn("#btnEN");

        assertEquals("Result will appear here.", resultLabel().getText());
    }

    @Test
    void testExistingResultIsNotOverwrittenDuringLanguageSwitch() {
        interact(() -> resultLabel().setText("Already calculated"));

        clickOn("#btnFR");

        assertEquals("Already calculated", resultLabel().getText());
    }

    @Test
    void testMissingLanguageShowsFallbackErrorMessage() {
        localizationService.failOnLanguage("fr_FR");

        clickOn("#btnFR");

        assertTrue(resultLabel().getText().contains("Language resource missing"));
    }

    @Test
    void testReadInputTextsReturnsEmptyStringsForMissingTextFields() {
        FuelCalculatorController freshController = new FuelCalculatorController(localizationService);

        FuelCalculatorController.InputTexts inputTexts = freshController.readInputTexts();
        assertEquals("", inputTexts.distanceText());
        assertEquals("", inputTexts.consumptionText());
        assertEquals("", inputTexts.priceText());
    }

    @Test
    void testReadInputTextsTrimsWhitespace() throws Exception {
        FuelCalculatorController freshController = new FuelCalculatorController(localizationService);

        TextField distance = new TextField(" 100 ");
        TextField consumption = new TextField(" 5 ");
        TextField price = new TextField(" 2.0 ");

        Field distanceField = FuelCalculatorController.class.getDeclaredField("txtDistance");
        Field consumptionField = FuelCalculatorController.class.getDeclaredField("txtConsumption");
        Field priceField = FuelCalculatorController.class.getDeclaredField("txtPrice");
        distanceField.setAccessible(true);
        consumptionField.setAccessible(true);
        priceField.setAccessible(true);
        distanceField.set(freshController, distance);
        consumptionField.set(freshController, consumption);
        priceField.set(freshController, price);

        FuelCalculatorController.InputTexts inputTexts = freshController.readInputTexts();
        assertEquals("100", inputTexts.distanceText());
        assertEquals("5", inputTexts.consumptionText());
        assertEquals("2.0", inputTexts.priceText());
    }

    @Test
    void testParseInputParsesTrimmedDoubles() {
        FuelCalculatorController.ParsedInput parsed = FuelCalculatorController.parseInput(
                new FuelCalculatorController.InputTexts(" 100 ", " 5 ", " 2.0 ")
        );

        assertEquals(100.0, parsed.distance());
        assertEquals(5.0, parsed.consumption());
        assertEquals(2.0, parsed.price());
    }

    @Test
    void testParseTrimmedDoubleThrowsForInvalidInput() {
        assertThrows(NumberFormatException.class, () -> FuelCalculatorController.parseTrimmedDouble("not-a-number"));
    }
}