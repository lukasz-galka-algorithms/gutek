package gutek.repositories;

import gutek.entities.decks.DeckBase;
import gutek.entities.users.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for managing {@link DeckBase} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations and custom queries for decks.
 */
@Repository
@Transactional
public interface DeckBaseRepository extends JpaRepository<DeckBase, Long> {
    /**
     * Finds all decks associated with a specific user.
     *
     * @param user the {@link AppUser} whose decks are to be retrieved.
     * @return a list of {@link DeckBase} entities belonging to the specified user.
     */
    List<DeckBase> findByUser(AppUser user);

    /**
     * Finds all deleted decks associated with a specific user.
     *
     * @param user the {@link AppUser} whose deleted decks are to be retrieved.
     * @return a list of deleted {@link DeckBase} entities belonging to the specified user.
     */
    List<DeckBase> findByUserAndIsDeletedTrue(AppUser user);

    /**
     * Finds all non-deleted decks associated with a specific user.
     *
     * @param user the {@link AppUser} whose non-deleted decks are to be retrieved.
     * @return a list of non-deleted {@link DeckBase} entities belonging to the specified user.
     */
    List<DeckBase> findByUserAndIsDeletedFalse(AppUser user);
}
