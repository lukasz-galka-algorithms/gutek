package gutek.entities.decks;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.users.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a deck of cards used in a spaced repetition system.
 *
 * This class stores information about the deck, including its name, whether it is deleted,
 * the list of associated cards, the revision algorithm applied to the deck, and statistics
 * about the deck's usage.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckBase {
    /** Unique identifier for the deck. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idDeck;
    /** The name of the deck. */
    private String name;
    /** Indicates whether the deck has been marked as deleted. */
    private Boolean isDeleted;
    /** The list of cards associated with the deck. */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "deck")
    private final List<CardBase> cards = new LinkedList<>();
    /** The revision algorithm applied to the deck. */
    @OneToOne(fetch = FetchType.EAGER)
    private RevisionAlgorithm<?> revisionAlgorithm;
    /** Statistics related to the deck's usage and performance. */
    @OneToOne(fetch = FetchType.EAGER)
    private DeckBaseStatistics deckBaseStatistics;
    /** The user who owns the deck. */
    @ManyToOne(fetch = FetchType.EAGER)
    private AppUser user;
}
