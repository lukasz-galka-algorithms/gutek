package gutek.repositories;

import gutek.entities.decks.DeckBaseStatistics;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link DeckBaseStatistics} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations for deck statistics.
 */
@Repository
@Transactional
public interface DeckBaseStatisticsRepository  extends JpaRepository<DeckBaseStatistics, Long> {
}
