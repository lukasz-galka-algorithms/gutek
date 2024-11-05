package gutek.entities.cards;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;

/**
 * Represents a record of a card revision event.
 *
 * This class stores information about a single revision of a card, including
 * the date of the revision, the index of the button pressed during the revision,
 * and the type of revision (normal or reverse). Each revision is associated with
 * a specific {@link CardBase}.
 */
@Entity
@Data
@NoArgsConstructor
public class CardBaseRevision {

    /** Unique identifier for the card revision event. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long idCardRevision;

    /** The date when the revision took place. */
    protected LocalDate revisionDate;

    /** The index of the button pressed during the revision. */
    protected Integer pressedButtonIndex;

    /** The type of revision (e.g., normal or reverse). */
    @Enumerated(EnumType.STRING)
    protected CardRevisionType cardRevisionType;

    /** The card that was revised during this revision event. */
    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected CardBase cardBase;
}
