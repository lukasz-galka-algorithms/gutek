package gutek.entities.decks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import static gutek.services.ChartService.MAX_RANGE;

/**
 * Represents statistical data related to the usage and performance of a deck in a spaced repetition system.
 *
 * This class stores information about the number of new cards reviewed per day, various types of revisions,
 * and the deck associated with these statistics. The arrays track the number of revisions over a set period of time.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckBaseStatistics {

    /** Unique identifier for the deck statistics. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idDeckStatistics;

    /** The number of new cards reviewed per day. */
    private Integer newCardsPerDay = 0;

    /** Indicator of the current date used for tracking daily progress. */
    private LocalDate todayIndicator = LocalDate.now();

    /** The deck to which these statistics apply. */
    @OneToOne(fetch = FetchType.EAGER)
    private DeckBase deck;

    /** Array tracking the number of cards revised for the first time over time. */
    @Lob
    private int[] revisedForTheFirstTime = new int[MAX_RANGE];

    /** Array tracking the number of regular revisions over time. */
    @Lob
    private int[] regularRevision = new int[MAX_RANGE];

    /** Array tracking the number of reverse revisions over time. */
    @Lob
    private int[] reverseRevision = new int[MAX_RANGE];
}