package gutek.repositories;

import gutek.entities.algorithms.RevisionAlgorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link RevisionAlgorithm} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations for revision algorithms.
 */
@Repository
public interface RevisionAlgorithmRepository extends JpaRepository<RevisionAlgorithm, Long> {
}
