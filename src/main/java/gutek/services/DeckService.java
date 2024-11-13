package gutek.services;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.entities.decks.DeckBaseStatistics;
import gutek.entities.users.AppUser;
import gutek.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing decks and cards.
 */
@Service
@AllArgsConstructor
public class DeckService {

    /**
     * Repository for accessing deck information.
     */
    private final DeckBaseRepository deckBaseRepository;

    /**
     * Repository for accessing user information.
     */
    private final AppUserRepository appUserRepository;

    /**
     * Repository for accessing card information.
     */
    private final CardBaseRepository cardBaseRepository;

    /**
     * Repository for accessing card revision history.
     */
    private final CardBaseRevisionRepository cardBaseRevisionRepository;

    /**
     * Repository for accessing revision algorithm information.
     */
    private final RevisionAlgorithmRepository revisionAlgorithmRepository;

    /**
     * Repository for accessing deck statistics.
     */
    private final DeckBaseStatisticsRepository deckBaseStatisticsRepository;

    /**
     * Retrieves all cards from the specified deck.
     *
     * @param deck The deck from which to retrieve cards.
     * @return List of all cards in the deck.
     */
    public List<CardBase> getAllCards(DeckBase deck) {
        return cardBaseRepository.findByDeck(deck);
    }

    /**
     * Retrieves cards that are due for regular revision from the specified deck.
     *
     * @param deck The deck from which to retrieve cards.
     * @return List of cards due for regular revision.
     */
    public List<CardBase> getRegularRevisionCards(DeckBase deck) {
        return cardBaseRepository.findByDeckIdDeckAndIsNewCardFalseAndNextRegularRevisionDateLessThanEqual(deck.getIdDeck(), LocalDate.now());
    }

    /**
     * Retrieves cards that are due for reverse revision from the specified deck.
     *
     * @param deck The deck from which to retrieve cards.
     * @return List of cards due for reverse revision.
     */
    public List<CardBase> getReverseRevisionCards(DeckBase deck) {
        return cardBaseRepository.findByDeckIdDeckAndIsNewCardFalseAndNextReverseRevisionDateLessThanEqual(deck.getIdDeck(), LocalDate.now());
    }

    /**
     * Retrieves new cards for today's revision, up to the specified limit.
     *
     * @param deck                        The deck from which to retrieve cards.
     * @param newCardsForTodayRevisionNumber The number of new cards to retrieve.
     * @return List of new cards for today's revision.
     */
    public List<CardBase> getNewCardsForTodayRevision(DeckBase deck, int newCardsForTodayRevisionNumber){
        List<CardBase> newCards = getAllNewCards(deck);

        newCards = newCards.stream()
                .sorted(Comparator.comparing(CardBase::getCreationTime))
                .toList();

        if (newCardsForTodayRevisionNumber >= newCards.size()) {
            return newCards;
        }

        return newCards.subList(0, newCardsForTodayRevisionNumber);
    }

    /**
     * Retrieves all new cards from the specified deck.
     *
     * @param deck The deck from which to retrieve cards.
     * @return List of new cards in the deck.
     */
    public List<CardBase> getAllNewCards(DeckBase deck) {
        return cardBaseRepository.findByDeckIdDeckAndIsNewCardTrue(deck.getIdDeck());
    }

    /**
     * Permanently removes the specified deck and all its associated cards.
     *
     * @param deck The deck to be removed.
     */
    public void removeDeck(DeckBase deck){
        List<CardBase> cards = deck.getCards();
        for (CardBase c : cards){
            cardBaseRevisionRepository.deleteByCardBase(c);
            cardBaseRepository.delete(c);
        }
        deck.getCards().clear();
        AppUser user = deck.getUser();
        user.getDecks().remove(deck);
        appUserRepository.save(user);
        deckBaseRepository.delete(deck);
        RevisionAlgorithm<?> revisionAlgorithm = deck.getRevisionAlgorithm();
        revisionAlgorithmRepository.delete(revisionAlgorithm);
    }

    /**
     * Restores a deleted deck.
     *
     * @param deck The deck to be restored.
     */
    public void restoreDeck(DeckBase deck){
        deck.setIsDeleted(false);
        deckBaseRepository.save(deck);
    }

    /**
     * Marks a deck as deleted.
     *
     * @param deck The deck to be deleted.
     */
    public void deleteDeck(DeckBase deck){
        deck.setIsDeleted(true);
        deckBaseRepository.save(deck);
    }

    /**
     * Saves or updates the specified deck.
     *
     * @param deck The deck to be saved or updated.
     */
    public void saveDeck(DeckBase deck){
        deckBaseRepository.save(deck);
    }

    /**
     * Adds a new deck for the specified user, using the given revision algorithm and deck name.
     *
     * @param loggedUser The user to whom the new deck will belong.
     * @param algorithm  The revision algorithm used for the deck.
     * @param deckName   The name of the new deck.
     * @return The newly created deck.
     */
    public DeckBase addNewDeck(AppUser loggedUser, RevisionAlgorithm<?> algorithm, String deckName){
        AppUser currentUser = appUserRepository.findById(loggedUser.getIdUser()).orElseThrow(() -> new RuntimeException("User not found"));
        revisionAlgorithmRepository.save(algorithm);
        DeckBaseStatistics deckBaseStatistics = new DeckBaseStatistics();
        DeckBase newDeck = new DeckBase(null, deckName,false, algorithm, null,currentUser);
        deckBaseRepository.save(newDeck);
        deckBaseStatistics.setDeck(newDeck);
        deckBaseStatisticsRepository.save(deckBaseStatistics);
        newDeck.setDeckBaseStatistics(deckBaseStatistics);
        deckBaseRepository.save(newDeck);
        currentUser.getDecks().add(newDeck);
        appUserRepository.save(currentUser);
        return newDeck;
    }

    /**
     * Retrieves a list of decks belonging to the specified user.
     *
     * @param user The user whose decks are to be retrieved.
     * @return List of decks belonging to the user.
     */
    public List<DeckBase> findDecksByUser(AppUser user){
        return deckBaseRepository.findByUser(user);
    }

    /**
     * Retrieves a list of non-deleted decks belonging to the specified user.
     *
     * @param user The user whose decks are to be retrieved.
     * @return List of non-deleted decks belonging to the user.
     */
    public List<DeckBase> findDecksByUserNotDeleted(AppUser user){
        return deckBaseRepository.findByUserAndIsDeletedFalse(user);
    }

    /**
     * Retrieves a list of deleted decks belonging to the specified user.
     *
     * @param user The user whose decks are to be retrieved.
     * @return List of deleted decks belonging to the user.
     */
    public List<DeckBase> findDecksByUserDeleted(AppUser user){
        return deckBaseRepository.findByUserAndIsDeletedTrue(user);
    }

    /**
     * Adds a new card to the specified deck if it doesn't already exist.
     *
     * @param cardBase The card to be added.
     * @param deck     The deck to which the card will be added.
     */
    public void addNewCardToDeck(CardBase cardBase, DeckBase deck){
        Optional<DeckBase> deckBase = deckBaseRepository.findById(deck.getIdDeck());
        if(deckBase.isPresent()){
            Optional<CardBase> cardBaseOptional = cardBaseRepository.findByFrontAndDeck(cardBase.getFront(), deckBase.get());
            if(cardBaseOptional.isEmpty()){
                deckBase.get().getCards().add(cardBase);
                deckBaseRepository.save(deckBase.get());
                cardBase.setDeck(deckBase.get());
                cardBaseRepository.save(cardBase);
            }
        }
    }

    /**
     * Gets the total count of cards in the specified deck.
     *
     * @param deck The deck for which to count cards.
     * @return The total number of cards in the deck.
     */
    public int getAllCardsCount(DeckBase deck) {
        return cardBaseRepository.countByDeckIdDeck(deck.getIdDeck());
    }

    /**
     * Gets the count of new cards in the specified deck.
     *
     * @param deck The deck for which to count new cards.
     * @return The number of new cards in the deck.
     */
    public int getNewCardsCount(DeckBase deck) {
        return cardBaseRepository.countByDeckIdDeckAndIsNewCardTrue(deck.getIdDeck());
    }

    /**
     * Gets the count of cards due for regular revision in the specified deck.
     *
     * @param deck The deck for which to count regular revision cards.
     * @return The number of cards due for regular revision in the deck.
     */
    public int getRegularRevisionCardsCount(DeckBase deck) {
        return cardBaseRepository.countByDeckIdDeckAndIsNewCardFalseAndNextRegularRevisionDateLessThanEqual(deck.getIdDeck(),LocalDate.now());
    }

    /**
     * Gets the count of cards due for reverse revision in the specified deck.
     *
     * @param deck The deck for which to count reverse revision cards.
     * @return The number of cards due for reverse revision in the deck.
     */
    public int getReverseRevisionCardsCount(DeckBase deck) {
        return cardBaseRepository.countByDeckIdDeckAndIsNewCardFalseAndNextReverseRevisionDateLessThanEqual(deck.getIdDeck(),LocalDate.now());
    }

    /**
     * Retrieves a deck by its unique identifier.
     *
     * @param deckId The unique identifier of the deck.
     * @return An {@link Optional} containing the {@link DeckBase} if found, or empty if no deck with the specified ID exists.
     */
    public Optional<DeckBase> findById(Long deckId){
        return deckBaseRepository.findById(deckId);
    }
}
