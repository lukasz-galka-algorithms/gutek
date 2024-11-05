package gutek.services;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.repositories.RevisionAlgorithmRepository;
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
     * Repository used for handling algorithm operations.
     */
    private final RevisionAlgorithmRepository revisionAlgorithmRepository;

    /**
     * A set of classes that extend {@link RevisionAlgorithm}.
     */
    private final Set<Class<? extends RevisionAlgorithm<? extends CardBase>>> algorithmClasses;

    /**
     * Constructor that initializes the service and loads all available revision algorithms.
     *
     * @param translationService Service used for handling translations.
     */
    public RevisionAlgorithmService(TranslationService translationService, RevisionAlgorithmRepository revisionAlgorithmRepository) {
        this.translationService = translationService;
        this.revisionAlgorithmRepository = revisionAlgorithmRepository;

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("gutek"))
                .setScanners(Scanners.SubTypes.filterResultsBy(new FilterBuilder().includePackage("gutek.entities.algorithms")))
        );

        algorithmClasses = reflections.getSubTypesOf(RevisionAlgorithm.class)
                .stream()
                .filter(subType -> !Modifier.isAbstract(subType.getModifiers()))
                .map(clazz -> {
                    @SuppressWarnings("unchecked")
                    Class<? extends RevisionAlgorithm<? extends CardBase>> castedClass = (Class<? extends RevisionAlgorithm<? extends CardBase>>) clazz;
                    return castedClass;
                })
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
                        RevisionAlgorithm<? extends CardBase> algorithmInstance = clazz.getDeclaredConstructor().newInstance();
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
    public <T extends CardBase> RevisionAlgorithm<T> createAlgorithmInstance(String algorithmName) {
        for (Class<? extends RevisionAlgorithm<? extends CardBase>> algorithmClass : algorithmClasses) {
            try {
                @SuppressWarnings("unchecked")
                RevisionAlgorithm<T> algorithmInstance = (RevisionAlgorithm<T>) algorithmClass.getDeclaredConstructor().newInstance();
                algorithmInstance.setTranslationService(translationService);
                if (algorithmInstance.getAlgorithmName().equals(algorithmName)) {
                    return algorithmInstance;
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * Saves a given revision algorithm to the database.
     *
     * @param algorithm The algorithm to save.
     * @return The saved algorithm with an assigned ID.
     */
    public <T extends RevisionAlgorithm<?>> T saveAlgorithm(T algorithm) {
        return revisionAlgorithmRepository.save(algorithm);
    }
}
