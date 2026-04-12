package org.otp.logging;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class AppLoggerTest {

    private static void resetInitializationFlag() throws Exception {
        Field field = AppLogger.class.getDeclaredField("initialized");
        field.setAccessible(true);
        field.setBoolean(null, false);
    }

    private static boolean isInitialized() throws Exception {
        Field field = AppLogger.class.getDeclaredField("initialized");
        field.setAccessible(true);
        return field.getBoolean(null);
    }

    @Test
    void initLoadsConfigurationAndIsIdempotent() throws Exception {
        resetInitializationFlag();

        assertDoesNotThrow(AppLogger::init);
        assertTrue(isInitialized(), "init should set the initialized flag");

        assertDoesNotThrow(AppLogger::init);
        assertTrue(isInitialized(), "init should stay initialized after repeated calls");
    }

    @Test
    void getLoggerReturnsNamedLoggerAndTriggersInitialization() throws Exception {
        resetInitializationFlag();

        Logger logger = AppLogger.getLogger(AppLoggerTest.class);

        assertNotNull(logger);
        assertEquals(AppLoggerTest.class.getName(), logger.getName());
        assertTrue(isInitialized(), "getLogger should initialize AppLogger lazily");
    }

    @Test
    void getLoggerReturnsSameLoggerForSameClass() throws Exception {
        resetInitializationFlag();

        Logger first = AppLogger.getLogger(AppLoggerTest.class);
        Logger second = AppLogger.getLogger(AppLoggerTest.class);

        assertSame(first, second, "Logger.getLogger should return the same logger instance for a class name");
    }
}