package org.otp;

import org.junit.jupiter.api.Test;

import javafx.application.Application;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

class LauncherTest {

    @Test
    void launcherHasStaticMainMethod() throws Exception {
        Method mainMethod = Launcher.class.getDeclaredMethod("main", String[].class);

        assertTrue(Modifier.isFinal(Launcher.class.getModifiers()));
        assertTrue(Modifier.isStatic(mainMethod.getModifiers()));
        assertEquals(void.class, mainMethod.getReturnType());
    }

    @Test
    void launchInitializesLoggerBeforeLaunchingApplication() {
        List<String> callOrder = new ArrayList<>();
        Runnable loggerInitializer = () -> callOrder.add("logger");
        BiConsumer<Class<? extends Application>, String[]> launcher = (appClass, args) -> callOrder.add("launch");

        Launcher.launch(new String[]{"--demo"}, loggerInitializer, launcher);

        assertEquals(List.of("logger", "launch"), callOrder);
    }

    @Test
    void launchPassesMainClassAndArgumentsToApplicationLauncher() {
        class LaunchCall {
            Class<? extends Application> appClass;
            String[] args;
        }

        LaunchCall launchCall = new LaunchCall();
        String[] args = {"arg1", "arg2"};

        Launcher.launch(args, () -> {
        }, (appClass, launchArgs) -> {
            launchCall.appClass = appClass;
            launchCall.args = launchArgs;
        });

        assertEquals(Main.class, launchCall.appClass);
        assertArrayEquals(args, launchCall.args);
    }

}