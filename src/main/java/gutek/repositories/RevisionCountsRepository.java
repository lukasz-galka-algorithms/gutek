package gutek.repositories;

import gutek.entities.decks.RevisionCounts;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link RevisionCounts} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations for revision counts.
 */
@Repository
@Transactional
public interface RevisionCountsRepository extends JpaRepository<RevisionCounts, Long> {
}
