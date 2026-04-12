package org.otp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    static class TestLocalizationService extends LocalizationService {
        @Override
        public String getString(String key) {
            return "Fuel Calculator";
        }
    }

    @Test
    void initializeLanguageSetsExpectedDefaultTag() {
        Main main = new Main();
        main.localizationService = new LocalizationService();

        main.initializeLanguage();

        assertEquals("en-US", main.localizationService.getCurrentLanguage());
    }

    @Test
    void createLoaderConfiguresFactoryForFuelCalculatorController() {
        Main main = new Main();
        main.localizationService = new LocalizationService();

        FXMLLoader loader = main.createLoader();

        Object createdController = loader.getControllerFactory().call(FuelCalculatorController.class);

        assertNotNull(createdController);
        assertInstanceOf(FuelCalculatorController.class, createdController);
    }

    @Test
    void startOrchestratesInitializationLoadingAndStageConfiguration() throws Exception {
        class MainSpy extends Main {
            boolean initializeLanguageCalled;
            boolean createLoaderCalled;
            boolean createSceneCalled;
            boolean configureStageCalled;
            final FXMLLoader loader = new FXMLLoader();

            @Override
            void initializeLanguage() {
                initializeLanguageCalled = true;
                super.initializeLanguage();
            }

            @Override
            FXMLLoader createLoader() {
                createLoaderCalled = true;
                return loader;
            }

            @Override
            Scene createScene(FXMLLoader fxmlLoader) {
                createSceneCalled = true;
                assertSame(loader, fxmlLoader);
                return null;
            }

            @Override
            void configureStage(Stage stage, Scene scene) {
                configureStageCalled = true;
                assertNull(stage);
                assertNull(scene);
            }
        }

        MainSpy main = new MainSpy();
        main.localizationService = new TestLocalizationService();

        main.start(null);

        assertTrue(main.initializeLanguageCalled);
        assertTrue(main.createLoaderCalled);
        assertTrue(main.createSceneCalled);
        assertTrue(main.configureStageCalled);
    }
}