package org.otp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LocalizationService Tests")
class LocalizationServiceTest {

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, String>> localizedTexts(LocalizationService service) {
        try {
            Field field = LocalizationService.class.getDeclaredField("allLocalizedTexts");
            field.setAccessible(true);
            return (Map<String, Map<String, String>>) field.get(service);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError("Unable to read allLocalizedTexts field", e);
        }
    }

    private static void putLanguage(LocalizationService service, String language, Map<String, String> values) {
        localizedTexts(service).put(language, new HashMap<>(values));
    }

    private static void invokeLoadLocalizedTexts(LocalizationService service, String language) {
        try {
            Method method = LocalizationService.class.getDeclaredMethod("loadLocalizedTexts", String.class);
            method.setAccessible(true);
            method.invoke(service, language);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError("Unable to invoke loadLocalizedTexts", e);
        } catch (InvocationTargetException e) {
            throw new AssertionError("loadLocalizedTexts threw an unexpected exception", e.getCause());
        }
    }

    private static final class LazyLoadingLocalizationService extends LocalizationService {
        int loadCalls;

        @Override
        public void loadStrings(String language) {
            loadCalls++;
            putLanguage(this, language, Map.of("distance.label", "Distance", "calculate.button", "Calculate"));
        }
    }

    @Test
    @DisplayName("Service has default language set to en_US")
    void testDefaultLanguage() {
        LocalizationService service = new LocalizationService();
        assertEquals("en_US", service.getCurrentLanguage());
    }

    @Test
    @DisplayName("loadStrings returns without touching DB when language is already cached")
    void loadStringsCachedLanguageDoesNotNeedDatabase() {
        LocalizationService service = new LocalizationService();
        putLanguage(service, "en_US", Map.of("app.title", "Fuel App"));
        service.setCurrentLanguage("en_US");

        assertDoesNotThrow(() -> service.loadStrings("en_US"));
        assertEquals("Fuel App", service.getString("app.title"));
    }

    @Test
    @DisplayName("getCurrentLanguage returns set language")
    void getCurrentLanguage() {
        LocalizationService service = new LocalizationService();
        service.setCurrentLanguage("fr_FR");
        assertEquals("fr_FR", service.getCurrentLanguage());
    }

    @Test
    @DisplayName("setCurrentLanguage stores selected language")
    void setCurrentLanguage() {
        LocalizationService service = new LocalizationService();
        service.setCurrentLanguage("ja_JP");
        assertEquals("ja_JP", service.getCurrentLanguage());
    }

    @Test
    @DisplayName("Service can switch between multiple language codes")
    void testSwitchBetweenLanguages() {
        LocalizationService service = new LocalizationService();

        service.setCurrentLanguage("en_US");
        assertEquals("en_US", service.getCurrentLanguage());

        service.setCurrentLanguage("fr_FR");
        assertEquals("fr_FR", service.getCurrentLanguage());

        service.setCurrentLanguage("ja_JP");
        assertEquals("ja_JP", service.getCurrentLanguage());

        service.setCurrentLanguage("fa_IR");
        assertEquals("fa_IR", service.getCurrentLanguage());
    }

    @ParameterizedTest
    @DisplayName("Service supports expected language codes")
    @ValueSource(strings = {"en_US", "fr_FR", "ja_JP", "fa_IR"})
    void testMultiplLanguageSupport(String language) {
        LocalizationService service = new LocalizationService();
        service.setCurrentLanguage(language);
        assertEquals(language, service.getCurrentLanguage());
    }

    @Test
    @DisplayName("getString returns preloaded value from current language")
    void testGetStringReturnsPreloadedValue() {
        LocalizationService service = new LocalizationService();
        service.setCurrentLanguage("en_US");
        putLanguage(service, "en_US", Map.of("distance.label", "Distance (km)"));

        assertEquals("Distance (km)", service.getString("distance.label"));
    }

    @Test
    @DisplayName("getString triggers lazy load when language is missing")
    void testGetStringTriggersLazyLoad() {
        LazyLoadingLocalizationService service = new LazyLoadingLocalizationService();
        service.setCurrentLanguage("fr_FR");

        assertEquals("Distance", service.getString("distance.label"));
        assertEquals(1, service.loadCalls);
    }

    @Test
    @DisplayName("loadLocalizedTexts loads database rows into the language cache")
    void testLoadLocalizedTextsLoadsDatabaseRows() {
        LocalizationService service = new LocalizationService();

        invokeLoadLocalizedTexts(service, "en-US");
        service.setCurrentLanguage("en-US");

        assertTrue(localizedTexts(service).containsKey("en-US"));
        assertEquals("Fuel Consumption and Trip Cost Calculator", service.getString("app.title"));

        List<String> keys = service.getAllKeys();
        assertTrue(keys.contains("app.title"));
        assertTrue(keys.contains("calculate.button"));
        assertEquals(12, keys.size());
    }

    @Test
    @DisplayName("getAllKeys returns key list for current language")
    void testGetAllKeysReturnsLanguageKeys() {
        LocalizationService service = new LocalizationService();
        service.setCurrentLanguage("en_US");
        putLanguage(service, "en_US", Map.of("distance.label", "Distance", "price.label", "Price"));

        List<String> keys = service.getAllKeys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("distance.label"));
        assertTrue(keys.contains("price.label"));
    }

    @Test
    @DisplayName("getAllKeys triggers lazy load when language keys are missing")
    void testGetAllKeysTriggersLazyLoad() {
        LazyLoadingLocalizationService service = new LazyLoadingLocalizationService();
        service.setCurrentLanguage("ja_JP");

        List<String> keys = service.getAllKeys();
        assertEquals(2, keys.size());
        assertEquals(1, service.loadCalls);
    }

    @Test
    @DisplayName("Service instances keep localization data isolated")
    void testInstanceIndependence() {
        LocalizationService service1 = new LocalizationService();
        LocalizationService service2 = new LocalizationService();

        putLanguage(service1, "en_US", Map.of("distance.label", "Distance A"));
        putLanguage(service2, "en_US", Map.of("distance.label", "Distance B"));
        service1.setCurrentLanguage("en_US");
        service2.setCurrentLanguage("en_US");

        assertEquals("Distance A", service1.getString("distance.label"));
        assertEquals("Distance B", service2.getString("distance.label"));
    }

    @Test
    @DisplayName("Setting same language twice keeps same value")
    void testSetSameLanguageTwice() {
        LocalizationService service = new LocalizationService();

        service.setCurrentLanguage("en_US");
        service.setCurrentLanguage("en_US");

        assertEquals("en_US", service.getCurrentLanguage());
    }
}