package gutek.entities.cards;

import gutek.entities.decks.DeckBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a card that uses the SuperMemo2 algorithm for spaced repetition.
 * This class extends {@link CardBase} and adds fields for managing repetition count,
 * intervals, easiness factors, and incorrect answer counts for both regular and reverse revision processes.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Getter
public class CardSuperMemo2 extends CardBase {

    /** Number of repetitions in the regular revision process. */
    protected int repetition;

    /** Number of repetitions in the reverse revision process. */
    protected int reverseRepetition;

    /** Interval (in days) for the next regular revision. */
    protected int interval;

    /** Interval (in days) for the next reverse revision. */
    protected int reverseInterval;

    /** Easiness factor for the regular revision process. */
    protected double easinessFactor;

    /** Easiness factor for the reverse revision process. */
    protected double reverseEasinessFactor;

    /** Number of incorrect answers in the regular revision process. */
    protected int incorrectCounter;

    /** Number of incorrect answers in the reverse revision process. */
    protected int reverseIncorrectCounter;

    /**
     * Constructs a new card with the given front, back, easiness factors, and associated deck.
     *
     * This constructor initializes the card's easiness factors and sets the default values
     * for both regular and reverse revision processes.
     *
     * @param front the front content of the card
     * @param back the back content of the card
     * @param easinessFactor the initial easiness factor for the regular revision process
     * @param reverseEasinessFactor the initial easiness factor for the reverse revision process
     * @param deck the deck to which the card belongs
     */
    public CardSuperMemo2(String front, String back, double easinessFactor, double reverseEasinessFactor, DeckBase deck) {
        super(front, back, deck);
        this.easinessFactor = easinessFactor;
        this.reverseEasinessFactor = reverseEasinessFactor;
        setRevisionDefault(easinessFactor);
        setReverseRevisionDefault(reverseEasinessFactor);
    }

    /**
     * Resets the revision parameters to their default values for the regular revision process.
     *
     * @param easinessFactor the initial easiness factor for the regular revision process
     */
    public void setRevisionDefault(double easinessFactor){
        this.repetition = 0;
        this.interval = 1;
        this.incorrectCounter = 0;
        this.easinessFactor = easinessFactor;
    }

    /**
     * Resets the revision parameters to their default values for the reverse revision process.
     *
     * @param reverseEasinessFactor the initial easiness factor for the reverse revision process
     */
    public void setReverseRevisionDefault(double reverseEasinessFactor){
        this.reverseRepetition = 0;
        this.reverseInterval = 1;
        this.reverseIncorrectCounter = 0;
        this.reverseEasinessFactor = reverseEasinessFactor;
    }

    /**
     * Sets the repetition count for the regular revision process, ensuring it is non-negative.
     *
     * @param repetition the number of repetitions
     */
    public void setRepetition(int repetition) {
        this.repetition = Math.max(repetition, 0);
    }

    /**
     * Sets the repetition count for the reverse revision process, ensuring it is non-negative.
     *
     * @param reverseRepetition the number of repetitions in reverse revision
     */
    public void setReverseRepetition(int reverseRepetition) {
        this.reverseRepetition = Math.max(reverseRepetition, 0);
    }

    /**
     * Sets the incorrect answer count for the reverse revision process, ensuring it is non-negative.
     *
     * @param reverseIncorrectCounter the number of incorrect answers in reverse revision
     */
    public void setReverseIncorrectCounter(int reverseIncorrectCounter) {
        this.reverseIncorrectCounter = Math.max(reverseIncorrectCounter, 0);
    }

    /**
     * Sets the incorrect answer count for the regular revision process, ensuring it is non-negative.
     *
     * @param incorrectCounter the number of incorrect answers
     */
    public void setIncorrectCounter(int incorrectCounter) {
        this.incorrectCounter = Math.max(incorrectCounter, 0);
    }

    /**
     * Sets the interval (in days) for the next regular revision, ensuring it is at least 1 day.
     *
     * @param interval the interval in days
     */
    public void setInterval(int interval) {
        this.interval = Math.max(interval, 1);
    }

    /**
     * Sets the interval (in days) for the next reverse revision, ensuring it is at least 1 day.
     *
     * @param reverseInterval the interval in days for reverse revision
     */
    public void setReverseInterval(int reverseInterval) {
        this.reverseInterval = Math.max(reverseInterval, 1);
    }

    /**
     * Sets the easiness factor for the regular revision process, ensuring it is at least 1.3.
     *
     * @param easinessFactor the easiness factor for regular revision
     */
    public void setEasinessFactor(double easinessFactor) {
        this.easinessFactor = Math.max(easinessFactor, 1.3);
    }

    /**
     * Sets the easiness factor for the reverse revision process, ensuring it is at least 1.3.
     *
     * @param reverseEasinessFactor the easiness factor for reverse revision
     */
    public void setReverseEasinessFactor(double reverseEasinessFactor) {
        this.reverseEasinessFactor = Math.max(reverseEasinessFactor, 1.3);
    }
}
