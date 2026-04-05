package org.otp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    LocalizationService localizationService = LocalizationService.getInstance();

    @Override
    public void start(Stage stage) throws IOException {
        localizationService.setCurrentLanguage("en-US");
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/otp/fuel-calculator-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());


        stage.setTitle(localizationService.getString("app.title"));
        stage.setScene(scene);
        stage.setMinWidth(540);
        stage.setMinHeight(360);
        stage.show();
    }
}