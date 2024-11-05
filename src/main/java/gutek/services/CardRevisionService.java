package gutek.services;

import gutek.entities.cards.CardBase;
import gutek.entities.cards.CardBaseRevision;
import gutek.entities.cards.CardRevisionType;
import gutek.repositories.CardBaseRepository;
import gutek.repositories.CardBaseRevisionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Service class responsible for handling the revision of cards, both regular and reverse.
 */
@Service
@AllArgsConstructor
public class CardRevisionService {

    /**
     * Repository for managing {@link CardBaseRevision} entities in the database.
     */
    private final CardBaseRevisionRepository cardBaseRevisionRepository;

    /**
     * Repository for managing {@link CardBase} entities in the database.
     */
    private final CardBaseRepository cardBaseRepository;

    /**
     * Saves a regular revision for the given card and stores the pressed button index.
     *
     * @param card               the card being revised.
     * @param pressedButtonIndex the index of the button pressed during the revision.
     */
    public void reviseRegular(CardBase card, Integer pressedButtonIndex){
        Optional<CardBase> cardBaseOptional = cardBaseRepository.findById(card.getIdCard());
        if(cardBaseOptional.isPresent()){
            CardBaseRevision revision = new CardBaseRevision();
            revision.setRevisionDate(LocalDate.now());
            revision.setCardBase(cardBaseOptional.get());
            revision.setPressedButtonIndex(pressedButtonIndex);
            revision.setCardRevisionType(CardRevisionType.REGULAR_REVISION);
            cardBaseRevisionRepository.save(revision);
        }
    }

    /**
     * Saves a reverse revision for the given card and stores the pressed button index.
     *
     * @param card               the card being revised.
     * @param pressedButtonIndex the index of the button pressed during the revision.
     */
    public void reviseReverse(CardBase card, Integer pressedButtonIndex){
        Optional<CardBase> cardBaseOptional = cardBaseRepository.findById(card.getIdCard());
        if(cardBaseOptional.isPresent()){
            CardBaseRevision revision = new CardBaseRevision();
            revision.setRevisionDate(LocalDate.now());
            revision.setCardBase(cardBaseOptional.get());
            revision.setPressedButtonIndex(pressedButtonIndex);
            revision.setCardRevisionType(CardRevisionType.REVERSE_REVISION);
            cardBaseRevisionRepository.save(revision);
        }
    }
}
