package org.otp;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;

public class FuelCalculatorController {

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
    private final LocalizationService localizationService = LocalizationService.getInstance();

    @FXML
    private void onCalculate() {
        try {
            double distance = Double.parseDouble(txtDistance.getText().trim());
            double consumption = Double.parseDouble(txtConsumption.getText().trim());
            double price = Double.parseDouble(txtPrice.getText().trim());

            if (distance < 0 || consumption < 0 || price < 0) {
                lblResult.setText(localizationService.getString("invalid.input"));
                return;
            }

            double totalFuel = (consumption / 100.0) * distance;
            double totalCost = totalFuel * price;

            CalculationRecord record = new CalculationRecord(distance, consumption, price, totalFuel, totalCost,
                    localizationService.getCurrentLanguage());

            try{
                calculationService.saveCalculation(record);
            } catch (Exception ex) {
                System.out.println("Failed to save calculation: " + ex.getMessage());
                return;
            }

            NumberFormat numberFormat =
                    NumberFormat.getNumberInstance(Locale.of(localizationService.getCurrentLanguage()));
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);

            String resultMessage = MessageFormat.format(
                    localizationService.getString("result.label"),
                    numberFormat.format(totalFuel),
                    numberFormat.format(totalCost)
            );
            lblResult.setText(resultMessage);
        } catch (NumberFormatException ex) {
            lblResult.setText(localizationService.getString("invalid.input"));
        }
    }

    @FXML
    private void onEnglishSelected() {
        setLanguage(Locale.of("en", "US"));
        localizationService.loadStrings("en_US");
    }

    @FXML
    private void onFrenchSelected() {
        setLanguage(Locale.of("fr", "FR"));
        localizationService.loadStrings("fr_FR");
    }

    @FXML
    private void onJapaneseSelected() {
        setLanguage(Locale.of("ja", "JP"));
        localizationService.loadStrings("ja_JP");
    }

    @FXML
    private void onPersianSelected() {
        setLanguage(Locale.of("fa", "IR"));
        localizationService.loadStrings("fa_IR");
    }

    private void setLanguage(Locale locale) {
        try {
            localizationService.loadStrings(locale.toLanguageTag());
            localizationService.setCurrentLanguage(locale.toLanguageTag());
            applyLocalizedTexts();
        } catch (MissingResourceException ex) {
            lblResult.setText("Language resource missing: " + locale);
        }
    }

    private void applyLocalizedTexts() {
        localizationService.getAllKeys();
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
            if ("fa".equals(localizationService.getCurrentLanguage().substring(0, 2))) {
                root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            } else {
                root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }
        }
    }
}
