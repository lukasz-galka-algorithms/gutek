package gutek.services;

import gutek.entities.languages.AppLocaleSetting;
import gutek.repositories.AppLocaleSettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TranslationServiceTest {

    @Mock
    private AppLocaleSettingRepository repository;

    @Mock
    private MessageSource messageSource;

    private TranslationService translationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        translationService = new TranslationService(repository, messageSource);
    }

    @Test
    void testUpdateLocale_WithNull() {
        // Arrange
        AppLocaleSetting defaultSetting = new AppLocaleSetting(1L, "en", "US");
        when(repository.findTopByOrderByIdAppLocaleSettingAsc()).thenReturn(Optional.of(defaultSetting));

        // Act
        translationService.updateLocale(null);

        // Assert
        Locale currentLocale = translationService.getCurrentLocale();
        assertEquals("en", currentLocale.getLanguage());
        assertEquals("US", currentLocale.getCountry());
    }

    @Test
    void testUpdateLocale_WithExistingLocale() {
        // Arrange
        AppLocaleSetting defaultSetting = new AppLocaleSetting(1L, "en", "US");
        when(repository.findTopByOrderByIdAppLocaleSettingAsc()).thenReturn(Optional.of(defaultSetting));

        // Act
        translationService.updateLocale(Locale.FRANCE);

        // Assert
        verify(repository, times(1)).save(any(AppLocaleSetting.class));
        Locale currentLocale = translationService.getCurrentLocale();
        assertEquals("fr", currentLocale.getLanguage());
        assertEquals("FR", currentLocale.getCountry());
    }

    @Test
    void testUpdateLocale_WithUnavailableLocale() {
        // Arrange
        when(repository.findTopByOrderByIdAppLocaleSettingAsc()).thenReturn(Optional.empty());
        when(repository.save(any(AppLocaleSetting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Locale loc = Locale.of("xx", "YY");
        translationService.updateLocale(loc);

        // Assert
        verify(repository, never()).save(any(AppLocaleSetting.class));
        Locale currentLocale = translationService.getCurrentLocale();
        assertNotNull(currentLocale);
        assertEquals("en", currentLocale.getLanguage());
        assertEquals("US", currentLocale.getCountry());
    }


    @Test
    void testGetTranslation() {
        // Arrange
        when(repository.findTopByOrderByIdAppLocaleSettingAsc()).thenReturn(Optional.empty());
        when(repository.save(any(AppLocaleSetting.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Locale testLocale = Locale.US;
        String testKey = "test.key";
        String expectedMessage = "Test Message";
        translationService.updateLocale(testLocale);
        when(messageSource.getMessage(testKey, null, testLocale)).thenReturn(expectedMessage);

        // Act
        String result = translationService.getTranslation(testKey);

        // Assert
        assertEquals(expectedMessage, result);
    }

    @Test
    void testGetAvailableLocales() {
        // Arrange
        AppLocaleSetting defaultSetting = new AppLocaleSetting(1L, "en", "US");
        when(repository.findTopByOrderByIdAppLocaleSettingAsc()).thenReturn(Optional.of(defaultSetting));

        // Act
        List<Locale> availableLocales = translationService.getAvailableLocales();

        // Assert
        assertNotNull(availableLocales);
        assertFalse(availableLocales.isEmpty());
    }

    @Test
    void testGetCurrentLocale() {
        // Arrange
        AppLocaleSetting defaultSetting = new AppLocaleSetting(1L, "en", "US");
        when(repository.findTopByOrderByIdAppLocaleSettingAsc()).thenReturn(Optional.of(defaultSetting));

        // Act
        Locale currentLocale = translationService.getCurrentLocale();

        // Assert
        assertEquals("en", currentLocale.getLanguage());
        assertEquals("US", currentLocale.getCountry());
    }
}
