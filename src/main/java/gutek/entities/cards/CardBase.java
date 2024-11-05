package gutek.entities.cards;

import gutek.entities.decks.DeckBase;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents the base class for a card in a revision system.
 * This class defines common fields and methods for different types of cards,
 * including fields for storing the front and back of the card, revision dates,
 * and the deck to which the card belongs.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
public class CardBase{

    /** Unique identifier for the card. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long idCard;

    /** The front content of the card (e.g., question or prompt). */
    protected String front;

    /** The back content of the card (e.g., answer or definition). */
    protected String back;

    /** The next scheduled date for a regular revision of the card. */
    protected LocalDate nextRegularRevisionDate;

    /** The next scheduled date for a reverse revision of the card. */
    protected LocalDate nextReverseRevisionDate;

    /** The timestamp when the card was created. */
    protected LocalDateTime creationTime;

    /** Indicates whether the card is new and has not yet been revised. */
    protected boolean isNewCard;

    /** The deck to which the card belongs. */
    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected DeckBase deck;

    /**
     * Constructs a new card with the given front, back, and associated deck.
     * This constructor initializes the card with the current date as the next
     * regular and reverse revision dates, and sets the creation time to the
     * current time. The card is also marked as new.
     * @param front the front content of the card
     * @param back the back content of the card
     * @param deck the deck to which the card belongs
     */
    public CardBase(String front, String back, DeckBase deck) {
        this.front = front;
        this.back = back;
        this.nextRegularRevisionDate = LocalDate.now();
        this.nextReverseRevisionDate = LocalDate.now();
        this.creationTime = LocalDateTime.now();
        this.deck = deck;
        this.isNewCard = true;
    }
}
