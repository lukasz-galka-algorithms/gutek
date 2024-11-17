package gutek.entities.algorithms;

import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.cards.CardBase;
import gutek.services.TranslationService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a revision algorithm for cards.
 * This class defines the common structure for various revision algorithms and
 * includes methods for managing hyperparameters and interacting with the UI.
 * The class is parameterized by a type {@code T} that extends {@link CardBase}.
 * The revision algorithm is defined as a JPA entity with inheritance strategy
 * {@link InheritanceType#TABLE_PER_CLASS}.
 *
 * @param <T> the type of card this algorithm will be applied to
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public abstract class RevisionAlgorithm<T extends CardBase>{

    /** Unique identifier for the revision algorithm. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long idAlgorithm;

    /** A transient field for accessing translation services. */
    @Transient
    protected TranslationService translationService;

    /** List of available revision strategies for this algorithm. */
    @Transient
    protected final List<RevisionStrategy<T>> revisionStrategies = new ArrayList<>();

    /**
     * Default constructor that initializes default hyperparameters and default revision strategies for the algorithm.
     */
    protected RevisionAlgorithm() {
        initializeDefaultHiperparameters();
        initializeDefaultRevisionStrategies();
    }

    /**
     * Returns the name of the algorithm.
     *
     * @return the name of the algorithm
     */
    public abstract String getAlgorithmName();

    /**
     * Creates a new card instance with the given front and back content.
     *
     * @param front the front content of the card
     * @param back the back content of the card
     * @return a new card instance of type {@code T}
     */
    public abstract T createNewCard(String front, String back);

    /**
     * Initializes the default hyperparameters for the algorithm.
     */
    public abstract void initializeDefaultHiperparameters();

    /**
     * Initializes the graphical user interface (GUI) for the revision strategy.
     *
     * @param width       The width of the available area for the GUI components.
     * @param height      The height of the available area for the GUI components.
     * @param scaleFactor A scaling factor used to adjust the size of the components dynamically.
     */
    public abstract void initializeGUI(double width, double height, double scaleFactor);

    /**
     * Updates the size of UI components based on the provided dimensions and scale factor.
     *
     * @param width the new width to set for the components
     * @param height the new height to set for the components
     * @param scaleFactor the scale factor to apply to the components, allowing for dynamic resizing
     */
    public abstract void updateSize(double width, double height, double scaleFactor);

    /**
     * Updates the translations for the UI components.
     */
    public abstract void updateTranslation();

    /**
     * Returns the list of available revision strategies.
     *
     * @return a list of revision strategies
     */
    public List<RevisionStrategy<T>> getAvailableRevisionStrategies() {
        return revisionStrategies;
    }

    /**
     * Initializes the default revision strategies for the algorithm.
     */
    public abstract void initializeDefaultRevisionStrategies();
}
