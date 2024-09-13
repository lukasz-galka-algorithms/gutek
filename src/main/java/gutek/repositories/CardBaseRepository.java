package gutek.repositories;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link CardBase} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations and custom queries for cards.
 */
@Repository
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
}
