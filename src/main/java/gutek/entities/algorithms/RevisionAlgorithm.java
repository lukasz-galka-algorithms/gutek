package gutek.entities.algorithms;

import gutek.entities.cards.CardBase;
import gutek.services.TranslationService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    /**
     * Default constructor that initializes default hyperparameters for the algorithm.
     */
    protected RevisionAlgorithm() {
        initializeDefaultHiperparameters();
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
     * Initializes the GUI components.
     */
    public abstract void initializeGUI();

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
}
