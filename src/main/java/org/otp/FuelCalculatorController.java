package org.otp;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.otp.logging.AppLogger;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FuelCalculatorController {

    private static final Logger LOGGER = AppLogger.getLogger(FuelCalculatorController.class);

    @FXML
    private Label lblDistance;

    @FXML
    private Label lblConsumption;

    @FXML
    private Label lblPrice;

    @FXML
    private Label lblResult;

    @FXML
    private TextField txtDistance;

    @FXML
    private TextField txtConsumption;

    @FXML
    private TextField txtPrice;

    @FXML
    private Button btnCalculate;

    @FXML
    private Button btnEN;

    @FXML
    private Button btnFR;

    @FXML
    private Button btnJP;

    @FXML
    private Button btnIR;

    private final CalculationService calculationService = new CalculationService();
    private final LocalizationService localizationService;

    public FuelCalculatorController(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    @FXML
    private void onCalculate() {
        try {
            ParsedInput parsedInput = parseInput(readInputTexts());
            double distance = parsedInput.distance();
            double consumption = parsedInput.consumption();
            double price = parsedInput.price();

            if (distance < 0 || consumption < 0 || price < 0) {
                lblResult.setText(localizationService.getString("invalid.input"));
                return;
            }

            CalculationRecord calculationRecord = calculationService.calculateFuelCost(distance, consumption, price);
            calculationRecord = new CalculationRecord(calculationRecord.getDistance(), calculationRecord.getConsumption(), 
                    calculationRecord.getPrice(), calculationRecord.getTotalFuel(), calculationRecord.getTotalCost(),
                    localizationService.getCurrentLanguage());

            calculationService.saveCalculation(calculationRecord);

            NumberFormat numberFormat =
                    NumberFormat.getNumberInstance(Locale.of(localizationService.getCurrentLanguage()));
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);

            String resultMessage = MessageFormat.format(
                    localizationService.getString("result.label"),
                    numberFormat.format(calculationRecord.getTotalFuel()),
                    numberFormat.format(calculationRecord.getTotalCost())
            );
            lblResult.setText(resultMessage);
        } catch (NumberFormatException ex) {
            lblResult.setText(localizationService.getString("invalid.input"));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while saving calculation", e);
            lblResult.setText(localizationService.getString("database.error"));
        }
    }

    InputTexts readInputTexts() {
        return new InputTexts(
                getTrimmedText(txtDistance),
                getTrimmedText(txtConsumption),
                getTrimmedText(txtPrice)
        );
    }

    static ParsedInput parseInput(InputTexts inputTexts) {
        return new ParsedInput(
                parseTrimmedDouble(inputTexts.distanceText()),
                parseTrimmedDouble(inputTexts.consumptionText()),
                parseTrimmedDouble(inputTexts.priceText())
        );
    }

    static double parseTrimmedDouble(String value) {
        return Double.parseDouble(value.trim());
    }

    private static String getTrimmedText(TextField textField) {
        if (textField == null || textField.getText() == null) {
            return "";
        }
        return textField.getText().trim();
    }

    record InputTexts(String distanceText, String consumptionText, String priceText) {
    }

    record ParsedInput(double distance, double consumption, double price) {
    }

    @FXML
    private void onEnglishSelected() {
        setLanguage(Locale.of("en", "US"));
    }

    @FXML
    private void onFrenchSelected() {
        setLanguage(Locale.of("fr", "FR"));
    }

    @FXML
    private void onJapaneseSelected() {
        setLanguage(Locale.of("ja", "JP"));
    }

    @FXML
    private void onPersianSelected() {
        setLanguage(Locale.of("fa", "IR"));
    }

    private void setLanguage(Locale locale) {
        try {
            String languageKey = locale.getLanguage() + "_" + locale.getCountry();
            localizationService.loadStrings(languageKey);
            localizationService.setCurrentLanguage(languageKey);
            applyLocalizedTexts();
        } catch (MissingResourceException ex) {
            lblResult.setText("Language resource missing: " + locale);
        }
    }

    private void applyLocalizedTexts() {
        List<String> allKeys = localizationService.getAllKeys();
        if(allKeys == null || allKeys.isEmpty()) {
            LOGGER.warning("No localization keys found for current language: " + localizationService.getCurrentLanguage());
        }

        lblDistance.setText(localizationService.getString("distance.label"));
        lblConsumption.setText(localizationService.getString("consumption.label"));
        lblPrice.setText(localizationService.getString("price.label"));
        btnCalculate.setText(localizationService.getString("calculate.button"));

        // Keep language buttons predictable in all locales.
        btnEN.setText("EN");
        btnFR.setText("FR");
        btnJP.setText("JP");
        btnIR.setText("IR");

        if (lblResult.getText() == null || lblResult.getText().isBlank()) {
            lblResult.setText(localizationService.getString("result.placeholder"));
        }

        Parent root = lblDistance.getScene() == null ? null : lblDistance.getScene().getRoot();
        if (root != null) {
            // Persian uses RTL orientation; other locales remain LTR.
            if (localizationService.getCurrentLanguage() != null && localizationService.getCurrentLanguage().startsWith("fa")) {
                root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            } else {
                root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }
        }
    }
}
