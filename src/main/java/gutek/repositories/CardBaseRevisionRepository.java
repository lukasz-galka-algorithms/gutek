package gutek.repositories;

import gutek.entities.cards.CardBase;
import gutek.entities.cards.CardBaseRevision;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link CardBaseRevision} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations and custom queries for card revisions.
 */
@Repository
@Transactional
public interface CardBaseRevisionRepository extends JpaRepository<CardBaseRevision,Long> {
    /**
     * Deletes all revisions associated with a specific {@link CardBase}.
     *
     * @param cardBase the {@link CardBase} whose revisions are to be deleted.
     */
    void deleteByCardBase(CardBase cardBase);
}
