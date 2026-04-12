package org.otp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    LocalizationService localizationService = new LocalizationService();

    @Override
    public void start(Stage stage) throws IOException {
        initializeLanguage();

        FXMLLoader fxmlLoader = createLoader();
        Scene scene = createScene(fxmlLoader);

        configureStage(stage, scene);
    }

    void initializeLanguage() {
        localizationService.setCurrentLanguage("en-US");
    }

    FXMLLoader createLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/otp/fuel-calculator-view.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == FuelCalculatorController.class) {
                return new FuelCalculatorController(localizationService);
            }
            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to create controller: " + controllerClass.getName(), e);
            }
        });
        return fxmlLoader;
    }

    Scene createScene(FXMLLoader fxmlLoader) throws IOException {
        return new Scene(fxmlLoader.load());
    }

    void configureStage(Stage stage, Scene scene) {
        stage.setTitle(localizationService.getString("app.title"));
        stage.setScene(scene);
        stage.setMinWidth(540);
        stage.setMinHeight(360);
        stage.show();
    }
}