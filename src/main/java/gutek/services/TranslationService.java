package gutek.services;

import gutek.entities.languages.AppLocaleSetting;
import gutek.repositories.AppLocaleSettingRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service responsible for handling translations and managing locale settings.
 */
@Service
public class TranslationService {

    /**
     * Base name for message resource files.
     */
    private static final String BASE_NAME = "messages";

    /**
     * Pattern used to match locale files in the format 'messages_<language>_<country>.properties'.
     */
    private static final Pattern LOCALE_PATTERN = Pattern.compile(BASE_NAME + "_([a-z]{2})_([A-Z]{2})\\.properties");

    /**
     * Repository for handling persistence of locale settings.
     */
    private final AppLocaleSettingRepository repository;

    /**
     * Message source for retrieving localized messages.
     */
    private final MessageSource messageSource;

    /**
     * The current locale used for translations.
     */
    private Locale locale;

    /**
     * Constructor initializing the repository and message source.
     *
     * @param repository    Repository for locale settings.
     * @param messageSource Message source for retrieving translations.
     */
    public TranslationService(AppLocaleSettingRepository repository, MessageSource messageSource) {
        this.repository = repository;
        this.messageSource = messageSource;
    }

    /**
     * Updates the locale setting. If the locale is null, it retrieves the default locale from the database.
     * If the locale is provided, it updates the locale in the database and applies it.
     *
     * @param locale The locale to be set, or null to use the default locale from the database.
     */
    public void updateLocale(Locale locale) {
        if (locale == null) {
            AppLocaleSetting appLocaleSetting = getOrCreateDefaultAppLocaleSetting();
            this.locale = new Locale(appLocaleSetting.getLanguage(), appLocaleSetting.getCountry());
        } else {
            List<Locale> availableLocales = getAvailableLocales();
            if (availableLocales.contains(locale)) {
                AppLocaleSetting appLocaleSetting = getOrCreateDefaultAppLocaleSetting();
                appLocaleSetting.setLanguage(locale.getLanguage());
                appLocaleSetting.setCountry(locale.getCountry());
                repository.save(appLocaleSetting);
                this.locale = locale;
            }
        }
    }

    /**
     * Retrieves the translation for a given key based on the current locale.
     *
     * @param key The translation key.
     * @return The translated string for the current locale.
     */
    public String getTranslation(String key) {
        return messageSource.getMessage(key, null, locale);
    }

    /**
     * Retrieves the current locale. If the locale is not set, it updates the locale from the database.
     *
     * @return The current locale.
     */
    public Locale getCurrentLocale() {
        updateLocale(locale);
        return locale;
    }

    /**
     * Retrieves a list of all available locales based on message property files found in the classpath.
     *
     * @return A list of available locales.
     */
    public List<Locale> getAvailableLocales() {
        List<Locale> availableLocales = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Set<String> processedJars = new HashSet<>();

        try {
            Enumeration<URL> resources = loader.getResources("");
            while (resources.hasMoreElements()) {
                URL resourceUrl = resources.nextElement();
                if (resourceUrl != null) {
                    String protocol = resourceUrl.getProtocol();
                    if ("file".equals(protocol)) {
                        File resourceDir = new File(resourceUrl.toURI());
                        if (resourceDir.exists() && resourceDir.isDirectory()) {
                            for (File file : resourceDir.listFiles()) {
                                String fileName = file.getName();
                                Matcher matcher = LOCALE_PATTERN.matcher(fileName);
                                if (matcher.matches()) {
                                    String language = matcher.group(1);
                                    String country = matcher.group(2);
                                    availableLocales.add(new Locale(language, country));
                                }
                            }
                        }
                    } else if ("jar".equals(protocol)) {
                        String jarPath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!"));
                        if (processedJars.add(jarPath)) {
                            JarFile jarFile = new JarFile(jarPath);
                            Enumeration<JarEntry> entries = jarFile.entries();

                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                String entryName = entry.getName();
                                String fileName = entryName.substring(entryName.lastIndexOf('/') + 1);
                                Matcher matcher = LOCALE_PATTERN.matcher(fileName);
                                if (matcher.matches()) {
                                    String language = matcher.group(1);
                                    String country = matcher.group(2);
                                    availableLocales.add(new Locale(language, country));
                                }
                            }
                            jarFile.close();
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException ignored) {
        }

        availableLocales.sort((a, b) -> a.getDisplayLanguage().compareToIgnoreCase(b.getDisplayLanguage()));
        return availableLocales;
    }

    /**
     * Retrieves or creates the default locale setting in the database.
     *
     * @return The current locale setting from the database, or a new default setting if none exists.
     */
    private AppLocaleSetting getOrCreateDefaultAppLocaleSetting() {
        return repository.findTopByOrderByIdAppLocaleSettingAsc().orElseGet(() -> {
            AppLocaleSetting newSetting = new AppLocaleSetting(null, "en", "US");
            return repository.save(newSetting);
        });
    }

    /**
     * Updates the locale setting in the database to the provided locale.
     *
     * @param locale The new locale to be saved.
     */
    private void updateLocaleSetting(Locale locale) {
        AppLocaleSetting setting = getOrCreateDefaultAppLocaleSetting();
        setting.setLanguage(locale.getLanguage());
        setting.setCountry(locale.getCountry());
        repository.save(setting);
    }
}
