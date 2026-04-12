package org.otp;

import javafx.application.Application;
import org.otp.logging.AppLogger;

import java.util.function.BiConsumer;

public final class Launcher {
    public static void main(String[] args) {
        launch(args, AppLogger::init, Launcher::launchApplication);
    }

    static void launch(String[] args,
                       Runnable loggerInitializer,
                       BiConsumer<Class<? extends Application>, String[]> appLauncher) {
        loggerInitializer.run();
        appLauncher.accept(Main.class, args);
    }

    static void launchApplication(Class<? extends Application> appClass, String[] args) {
        Application.launch(appClass, args);
    }
}