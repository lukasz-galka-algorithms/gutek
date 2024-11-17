package gutek.services;

import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.cards.CardBase;
import gutek.entities.cards.CardBaseRevision;
import gutek.repositories.CardBaseRepository;
import gutek.repositories.CardBaseRevisionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Service class responsible for handling the revision of cards.
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
     * Records a revision for a specified card, including details about the strategy used
     * and the button pressed by the user during the revision.
     *
     * @param card              The card being revised, represented by a {@link CardBase} entity.
     * @param pressedButtonIndex The index of the button pressed during the revision, indicating the user's action or response.
     * @param revisionStrategy  The revision strategy applied to the card, represented by a {@link RevisionStrategy}.
     */
    public void revise(CardBase card, Integer pressedButtonIndex, RevisionStrategy<?> revisionStrategy){
        Optional<CardBase> cardBaseOptional = cardBaseRepository.findById(card.getIdCard());
        if(cardBaseOptional.isPresent()){
            CardBaseRevision revision = new CardBaseRevision();
            revision.setRevisionDate(LocalDate.now());
            revision.setCardBase(cardBaseOptional.get());
            revision.setPressedButtonIndex(pressedButtonIndex);
            revision.setStrategyClassName(revisionStrategy.getClass().getSimpleName());
            cardBaseRevisionRepository.save(revision);
        }
    }
}
