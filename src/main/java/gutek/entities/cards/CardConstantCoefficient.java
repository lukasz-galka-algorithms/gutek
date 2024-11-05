package gutek.entities.cards;

import gutek.entities.decks.DeckBase;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a card that uses constant coefficients for calculating revision times.
 *
 * This class extends {@link CardBase} and adds functionality for tracking revision
 * times and incorrect answer counts for both regular and reverse revision processes.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
public class CardConstantCoefficient extends CardBase {

    /** The base time used for calculating the next regular revision. */
    protected double baseRevisionTime;

    /** The base time used for calculating the next reverse revision. */
    protected double baseReverseRevisionTime;

    /** The number of incorrect answers during regular revision. */
    protected int incorrectCounter;

    /** The number of incorrect answers during reverse revision. */
    protected int reverseIncorrectCounter;

    /**
     * Constructs a new card with the given front and back content, and associated deck.
     *
     * This constructor also initializes the default revision times and resets the
     * incorrect counters for both regular and reverse revisions.
     *
     * @param front the front content of the card
     * @param back the back content of the card
     * @param deck the deck to which the card belongs
     */
    public CardConstantCoefficient(String front, String back, DeckBase deck) {
        super(front, back, deck);
        setRevisionDefault();
        setReverseRevisionDefault();
    }

    /**
     * Resets the revision parameters to their default values for regular revision.
     *
     * This sets the base revision time to 1.0 and resets the incorrect counter to 0.
     */
    public void setRevisionDefault(){
        baseRevisionTime = 1.0;
        incorrectCounter = 0;
    }

    /**
     * Resets the revision parameters to their default values for reverse revision.
     *
     * This sets the base reverse revision time to 1.0 and resets the reverse incorrect counter to 0.
     */
    public void setReverseRevisionDefault(){
        baseReverseRevisionTime = 1.0;
        reverseIncorrectCounter = 0;
    }

    /**
     * Sets the incorrect counter for regular revision, ensuring it is non-negative.
     *
     * @param incorrectCounter the number of incorrect answers
     */
    public void setIncorrectCounter(int incorrectCounter) {
        this.incorrectCounter = Math.max(incorrectCounter, 0);
    }

    /**
     * Sets the incorrect counter for reverse revision, ensuring it is non-negative.
     *
     * @param reverseIncorrectCounter the number of incorrect answers in reverse revision
     */
    public void setReverseIncorrectCounter(int reverseIncorrectCounter) {
        this.reverseIncorrectCounter = Math.max(reverseIncorrectCounter, 0);
    }

    /**
     * Sets the base revision time for regular revision, ensuring it is positive.
     *
     * @param baseRevisionTime the base time for calculating the next regular revision
     */
    public void setBaseRevisionTime(double baseRevisionTime) {
        this.baseRevisionTime = Math.max(baseRevisionTime, 0.01);
    }

    /**
     * Sets the base revision time for reverse revision, ensuring it is positive.
     *
     * @param baseReverseRevisionTime the base time for calculating the next reverse revision
     */
    public void setBaseReverseRevisionTime(double baseReverseRevisionTime) {
        this.baseReverseRevisionTime = Math.max(baseReverseRevisionTime, 0.01);
    }
}
