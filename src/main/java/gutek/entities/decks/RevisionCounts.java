package gutek.entities.decks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static gutek.services.ChartService.MAX_RANGE;

/**
 * Represents the revision counts for a specific revision strategy associated with a deck's statistics.
 * <p>
 * This entity stores information about the number of revisions performed for a specific strategy
 * and links it to the overall statistics of a deck. Each instance corresponds to a particular revision strategy.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevisionCounts {

    /**
     * Unique identifier for the deck statistics strategy revision counts.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idRevisionCounts;

    /**
     * The index of the revision strategy associated with these counts.
     */
    private Integer strategyIndex;

    /**
     * An array storing the revision counts over a predefined time range.
     */
    @Lob
    private int[] counts = new int[MAX_RANGE];

    /**
     * The deck statistics entity to which these revision counts belong.
     */
    @ManyToOne
    private DeckBaseStatistics deckBaseStatistics;
}