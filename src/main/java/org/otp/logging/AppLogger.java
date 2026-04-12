package org.otp.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class AppLogger {

    private static final String CONFIG_RESOURCE = "/logging.properties";
    private static volatile boolean initialized;

    private AppLogger() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }

        try (InputStream input = AppLogger.class.getResourceAsStream(CONFIG_RESOURCE)) {
            if (input != null) {
                LogManager.getLogManager().readConfiguration(input);
            }
        } catch (IOException ignored) {
            // Fall back to the JVM's default logging configuration.
        }

        initialized = true;
    }

    public static Logger getLogger(Class<?> clazz) {
        init();
        return Logger.getLogger(clazz.getName());
    }
}
