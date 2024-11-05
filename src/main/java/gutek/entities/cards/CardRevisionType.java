package gutek.entities.cards;

/**
 * Enum representing the types of revision that can be applied to a card.
 *
 * This enum defines two types of revisions:
 * <ul>
 *   <li>{@code REGULAR_REVISION} - A standard forward revision process.</li>
 *   <li>{@code REVERSE_REVISION} - A reverse revision process, where the card is revised in reverse order.</li>
 * </ul>
 */
public enum CardRevisionType {

    /** Represents a standard forward revision process. */
    REGULAR_REVISION,

    /** Represents a reverse revision process. */
    REVERSE_REVISION
}
