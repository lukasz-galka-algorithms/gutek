package gutek.repositories;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link CardBase} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations and custom queries for cards.
 */
@Repository
@Transactional
public interface CardBaseRepository extends JpaRepository<CardBase, Long> {
    /**
     * Finds a {@link CardBase} entity by its front text and the associated deck.
     *
     * @param front the front text of the card.
     * @param deck the associated {@link DeckBase}.
     * @return An {@link Optional} containing the {@link CardBase} if found, otherwise empty.
     */
    Optional<CardBase> findByFrontAndDeck(String front, DeckBase deck);
    /**
     * Finds all {@link CardBase} entities associated with a specific deck.
     *
     * @param deck the associated {@link DeckBase}.
     * @return A list of {@link CardBase} entities in the specified deck.
     */
    List<CardBase> findByDeck(DeckBase deck);
    /**
     * Finds all {@link CardBase} entities that contain the given front and back text fragments in the specified deck.
     *
     * @param front the fragment of the front text.
     * @param back the fragment of the back text.
     * @param deck the associated {@link DeckBase}.
     * @return A list of {@link CardBase} entities matching the criteria.
     */
    List<CardBase> findByFrontContainingAndBackContainingAndDeck(String front, String back, DeckBase deck);
    /**
     * Finds all {@link CardBase} entities that contain the given front text fragment in the specified deck.
     *
     * @param front the fragment of the front text.
     * @param deck the associated {@link DeckBase}.
     * @return A list of {@link CardBase} entities matching the front text fragment in the specified deck.
     */
    List<CardBase> findByFrontContainingAndDeck(String front, DeckBase deck);
    /**
     * Finds all {@link CardBase} entities that contain the given back text fragment in the specified deck.
     *
     * @param back the fragment of the back text.
     * @param deck the associated {@link DeckBase}.
     * @return A list of {@link CardBase} entities matching the back text fragment in the specified deck.
     */
    List<CardBase> findByBackContainingAndDeck(String back, DeckBase deck);

    /**
     * Counts all cards in the specified deck.
     *
     * @param deckId the ID of the deck for which to count cards.
     * @return the total number of cards in the deck.
     */
    int countByDeckIdDeck(Long deckId);

    /**
     * Counts all new cards in the specified deck.
     *
     * @param deckId the ID of the deck for which to count new cards.
     * @return the number of new cards in the deck.
     */
    int countByDeckIdDeckAndIsNewCardTrue(Long deckId);

    /**
     * Counts all cards available for regular revision in the specified deck.
     *
     * @param deckId the ID of the deck for which to count cards.
     * @param currentDate the current date used for filtering revision cards.
     * @return the number of cards due for regular revision in the deck.
     */
    int countByDeckIdDeckAndIsNewCardFalseAndNextRegularRevisionDateLessThanEqual(Long deckId, LocalDate currentDate);

    /**
     * Counts all cards available for reverse revision in the specified deck.
     *
     * @param deckId the ID of the deck for which to count cards.
     * @param currentDate the current date used for filtering revision cards.
     * @return the number of cards due for reverse revision in the deck.
     */
    int countByDeckIdDeckAndIsNewCardFalseAndNextReverseRevisionDateLessThanEqual(Long deckId, LocalDate currentDate);

    /**
     * Finds all regular revision cards in the specified deck that are due for revision.
     *
     * @param idDeck the ID of the deck.
     * @param now the current date used for filtering.
     * @return a list of {@link CardBase} entities due for regular revision.
     */
    List<CardBase> findByDeckIdDeckAndIsNewCardFalseAndNextRegularRevisionDateLessThanEqual(Long idDeck, LocalDate now);

    /**
     * Finds all reverse revision cards in the specified deck that are due for revision.
     *
     * @param idDeck the ID of the deck.
     * @param now the current date used for filtering.
     * @return a list of {@link CardBase} entities due for reverse revision.
     */
    List<CardBase> findByDeckIdDeckAndIsNewCardFalseAndNextReverseRevisionDateLessThanEqual(Long idDeck, LocalDate now);

    /**
     * Finds all new cards in the specified deck.
     *
     * @param idDeck the ID of the deck.
     * @return a list of {@link CardBase} entities that are new in the deck.
     */
    List<CardBase> findByDeckIdDeckAndIsNewCardTrue(Long idDeck);
}
