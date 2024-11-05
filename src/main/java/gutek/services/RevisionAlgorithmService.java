package gutek.services;

import gutek.entities.algorithms.RevisionAlgorithm;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.stereotype.Service;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service responsible for managing revision algorithms, including loading them dynamically
 * from the classpath and creating instances.
 */
@Service
public class RevisionAlgorithmService {

    /**
     * Service used for handling translations.
     */
    private final TranslationService translationService;

    /**
     * A set of classes that extend {@link RevisionAlgorithm}.
     */
    private final Set<Class<? extends RevisionAlgorithm>> algorithmClasses;

    /**
     * Constructor that initializes the service and loads all available revision algorithms.
     *
     * @param translationService Service used for handling translations.
     */
    public RevisionAlgorithmService(TranslationService translationService) {
        this.translationService = translationService;

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("gutek"))
                .setScanners(Scanners.SubTypes.filterResultsBy(new FilterBuilder().includePackage("gutek.entities.algorithms")))
        );

        algorithmClasses = reflections.getSubTypesOf(RevisionAlgorithm.class)
                .stream()
                .filter(subType -> !Modifier.isAbstract(subType.getModifiers()))
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves the names of all available revision algorithms.
     *
     * @return A set of algorithm names.
     */
    public Set<String> getAlgorithmNames() {
        return algorithmClasses.stream()
                .map(clazz -> {
                    try {
                        RevisionAlgorithm<?> algorithmInstance = clazz.getDeclaredConstructor().newInstance();
                        algorithmInstance.setTranslationService(translationService);
                        return algorithmInstance.getAlgorithmName();
                    } catch (Exception e) {
                        return clazz.getSimpleName();
                    }
                })
                .collect(Collectors.toSet());
    }

    /**
     * Creates an instance of a revision algorithm by its name.
     *
     * @param algorithmName The name of the algorithm to instantiate.
     * @return An instance of the revision algorithm, or {@code null} if not found.
     */
    public RevisionAlgorithm<?> createAlgorithmInstance(String algorithmName) {
        for (Class<? extends RevisionAlgorithm> algorithmClass : algorithmClasses) {
            try {
                RevisionAlgorithm<?> algorithmInstance = algorithmClass.getDeclaredConstructor().newInstance();
                algorithmInstance.setTranslationService(translationService);
                if (algorithmInstance.getAlgorithmName().equals(algorithmName)) {
                    return algorithmInstance;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
