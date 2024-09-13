package gutek.services;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.repositories.CardBaseRepository;
import gutek.repositories.CardBaseRevisionRepository;
import gutek.repositories.DeckBaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing {@link CardBase} entities and related operations.
 */
@Service
@AllArgsConstructor
public class CardService {
    /**
     * Repository for managing {@link CardBase} entities.
     */
    private final CardBaseRepository cardBaseRepository;
    /**
     * Repository for managing {@link DeckBase} entities.
     */
    private final DeckBaseRepository deckBaseRepository;
    /**
     * Repository for managing {@link gutek.entities.cards.CardBaseRevision} entities.
     */
    private final CardBaseRevisionRepository cardBaseRevisionRepository;
    /**
     * Removes a card from its deck and deletes its related revisions.
     *
     * @param card the card to be removed and deleted.
     */
    public void removeCard(CardBase card){
        Optional<DeckBase> deck = deckBaseRepository.findById(card.getDeck().getIdDeck());
        if (deck.isPresent()) {
            deck.get().getCards().remove(card);
            deckBaseRepository.save(deck.get());

            Optional<CardBase> currentCard = cardBaseRepository.findById(card.getIdCard());
            if (currentCard.isPresent()) {
                cardBaseRepository.delete(currentCard.get());
                cardBaseRevisionRepository.deleteByCardBase(card);
            }
        }
    }
    /**
     * Adds a new card to the specified deck with the provided front and back text.
     *
     * @param frontText the front text of the new card.
     * @param backText  the back text of the new card.
     * @param deck      the deck to which the new card will be added.
     */
    public void addNewCard(String frontText, String backText, DeckBase deck){
        Optional<DeckBase> currentDeck = deckBaseRepository.findById(deck.getIdDeck());
        if(currentDeck.isPresent()){
            CardBase newCard = currentDeck.get().getRevisionAlgorithm().createNewCard(frontText, backText);
            newCard.setDeck(currentDeck.get());
            cardBaseRepository.save(newCard);
            currentDeck.get().getCards().add(newCard);
            deckBaseRepository.save(currentDeck.get());
        }
    }
    /**
     * Saves the provided card to the database.
     *
     * @param cardToEdit the card to be saved.
     */
    public void saveCard(CardBase cardToEdit){
        cardBaseRepository.save(cardToEdit);
    }
    /**
     * Finds cards belonging to a specific deck that match the given search criteria.
     *
     * @param phraseInFront the front text search phrase.
     * @param phraseInBack  the back text search phrase.
     * @param deck          the deck containing the cards.
     * @return a list of cards that match the search criteria.
     */
    public List<CardBase> findCardsByUser(String phraseInFront, String phraseInBack, DeckBase deck){
        if (phraseInFront.isEmpty() && phraseInBack.isEmpty()) {
            return cardBaseRepository.findByDeck(deck);
        } else if (phraseInBack.isEmpty()) {
            return cardBaseRepository.findByFrontContainingAndDeck(phraseInFront, deck);
        } else if (phraseInFront.isEmpty()) {
            return cardBaseRepository.findByBackContainingAndDeck(phraseInBack, deck);
        } else {
            return cardBaseRepository.findByFrontContainingAndBackContainingAndDeck(phraseInFront, phraseInBack, deck);
        }
    }
    /**
     * Finds a card by its front text and the deck it belongs to.
     *
     * @param frontText the front text of the card.
     * @param deck      the deck to search in.
     * @return an {@link Optional} containing the card if found, or empty if not found.
     */
    public Optional<CardBase> findCardByFrontAndDeck(String frontText, DeckBase deck){
        return cardBaseRepository.findByFrontAndDeck(frontText, deck);
    }
}
