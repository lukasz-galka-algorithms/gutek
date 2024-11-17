package gutek.services;

import gutek.entities.languages.AppLocaleSetting;
import gutek.repositories.AppLocaleSettingRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
     * Pattern used to match locale files in the format `messages_{language}_{country}.properties`.
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
            this.locale = new Locale.Builder()
                    .setLanguage(appLocaleSetting.getLanguage())
                    .setRegion(appLocaleSetting.getCountry())
                    .build();
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
        Set<String> processedResources = new HashSet<>();

        try {
            Enumeration<URL> resources = loader.getResources("");
            while (resources.hasMoreElements()) {
                URL resourceUrl = resources.nextElement();
                processResource(resourceUrl, availableLocales, processedResources);
            }
        } catch (IOException | URISyntaxException ignored) {
            //ignore
        }

        availableLocales.sort(Comparator.comparing(Locale::getDisplayLanguage, String.CASE_INSENSITIVE_ORDER));
        return availableLocales;
    }

    /**
     * Processes a given resource URL based on its protocol type and adds available locales from it.
     *
     * @param resourceUrl        The URL of the resource.
     * @param availableLocales   The list of available locales to be populated.
     * @param processedResources The set of resources that have already been processed.
     * @throws URISyntaxException If the URL syntax is incorrect.
     * @throws IOException        If an I/O error occurs while accessing the resource.
     */
    private void processResource(URL resourceUrl, List<Locale> availableLocales, Set<String> processedResources) throws URISyntaxException, IOException {
        if (resourceUrl != null) {
            String protocol = resourceUrl.getProtocol();

            switch (protocol) {
                case "file":
                    handleFileProtocol(resourceUrl, availableLocales, processedResources);
                    break;

                case "jar":
                    handleJarProtocol(resourceUrl, availableLocales, processedResources);
                    break;

                default:
                    // No action for unsupported protocols
            }
        }
    }

    /**
     * Handles resources with the "file" protocol, adding any available locales from file-based resources.
     *
     * @param resourceUrl        The URL of the resource.
     * @param availableLocales   The list of available locales to be populated.
     * @param processedResources The set of resources that have already been processed.
     * @throws URISyntaxException If the URL syntax is incorrect.
     */
    private void handleFileProtocol(URL resourceUrl, List<Locale> availableLocales, Set<String> processedResources) throws URISyntaxException {
        String filePath = decodePath(resourceUrl.getPath());
        if (processedResources.add(filePath)) {
            availableLocales.addAll(getLocalesFromFileResource(resourceUrl));
        }
    }

    /**
     * Handles resources with the "jar" protocol, distinguishing between nested and standard JARs.
     *
     * @param resourceUrl        The URL of the resource.
     * @param availableLocales   The list of available locales to be populated.
     * @param processedResources The set of resources that have already been processed.
     * @throws IOException        If an I/O error occurs while accessing the resource.
     */
    private void handleJarProtocol(URL resourceUrl, List<Locale> availableLocales, Set<String> processedResources) throws IOException {
        String fullPath = resourceUrl.getPath();
        if (fullPath.startsWith("nested:")) {
            handleNestedJar(fullPath, availableLocales, processedResources);
        } else if (fullPath.startsWith("file:")) {
            handleStandardJar(fullPath, availableLocales, processedResources);
        }
    }

    /**
     * Handles nested JAR files, extracting and adding any available locales from the nested JAR resource.
     *
     * @param fullPath           The full path of the resource URL.
     * @param availableLocales   The list of available locales to be populated.
     * @param processedResources The set of resources that have already been processed.
     * @throws IOException        If an I/O error occurs while accessing the resource.
     */
    private void handleNestedJar(String fullPath, List<Locale> availableLocales, Set<String> processedResources) throws IOException {
        int lastExclamation = fullPath.lastIndexOf("!");
        String pathUpToFirstExclamation = fullPath.substring(0, lastExclamation);
        int secondLastExclamation = pathUpToFirstExclamation.lastIndexOf("!");
        String nestedPath = decodePath(fullPath.substring(7, secondLastExclamation));
        if (processedResources.add(nestedPath)) {
            availableLocales.addAll(getLocalesFromJarResource(nestedPath));
        }
    }

    /**
     * Handles standard JAR files, extracting and adding any available locales from the JAR resource.
     *
     * @param fullPath           The full path of the resource URL.
     * @param availableLocales   The list of available locales to be populated.
     * @param processedResources The set of resources that have already been processed.
     * @throws IOException        If an I/O error occurs while accessing the resource.
     */
    private void handleStandardJar(String fullPath, List<Locale> availableLocales, Set<String> processedResources) throws IOException {
        String jarPath = decodePath(fullPath.substring(5, fullPath.lastIndexOf("!")));
        if (processedResources.add(jarPath)) {
            availableLocales.addAll(getLocalesFromJarResource(jarPath));
        }
    }

    /**
     * Extracts locales from directory-based resources.
     *
     * @param resourceUrl The URL of the file directory.
     * @return A list of Locales found within the directory.
     * @throws URISyntaxException if the URI syntax is incorrect.
     */
    private List<Locale> getLocalesFromFileResource(URL resourceUrl) throws URISyntaxException {
        List<Locale> locales = new ArrayList<>();
        File resourceDir = new File(resourceUrl.toURI());

        if (resourceDir.exists() && resourceDir.isDirectory()) {
            for (File file : Objects.requireNonNull(resourceDir.listFiles())) {
                Locale extractedlocale = extractLocaleFromFileName(file.getName());
                if (extractedlocale != null) {
                    locales.add(extractedlocale);
                }
            }
        }
        return locales;
    }

    /**
     * Extracts locales from JAR-based resources.
     *
     * @param jarPath The path to the JAR file.
     * @return A list of Locales found within the JAR.
     * @throws IOException if an I/O error occurs.
     */
    private List<Locale> getLocalesFromJarResource(String jarPath) throws IOException {
        List<Locale> locales = new ArrayList<>();
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String fileName = entry.getName().substring(entry.getName().lastIndexOf('/') + 1);
                Locale extractedlocale = extractLocaleFromFileName(fileName);
                if (extractedlocale != null) {
                    locales.add(extractedlocale);
                }
            }
        }
        return locales;
    }

    /**
     * Extracts a Locale from a given file name if it matches the locale pattern.
     *
     * @param fileName The file name to extract the locale from.
     * @return A Locale object if the file name matches the pattern, otherwise null.
     */
    private Locale extractLocaleFromFileName(String fileName) {
        Matcher matcher = LOCALE_PATTERN.matcher(fileName);
        if (matcher.matches()) {
            String language = matcher.group(1);
            String country = matcher.group(2);
            return new Locale.Builder()
                    .setLanguage(language)
                    .setRegion(country)
                    .build();
        }
        return null;
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
     * Decodes the provided URL-encoded path using UTF-8 encoding.
     * This method is typically used to handle URL-encoded strings, such as paths that contain
     * special characters (e.g., spaces encoded as "%20") and converts them back to their original form.
     *
     * @param path the URL-encoded path to decode
     * @return the decoded path as a String
     */
    private String decodePath(String path) {
        return URLDecoder.decode(path, StandardCharsets.UTF_8);
    }
}
