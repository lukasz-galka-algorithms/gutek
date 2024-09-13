package gutek.repositories;

import gutek.entities.decks.DeckBase;
import gutek.entities.users.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link DeckBase} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations and custom queries for decks.
 */
@Repository
public interface DeckBaseRepository extends JpaRepository<DeckBase, Long> {
    /**
     * Finds all decks associated with a specific user.
     *
     * @param user the {@link AppUser} whose decks are to be retrieved.
     * @return a list of {@link DeckBase} entities belonging to the specified user.
     */
    List<DeckBase> findByUser(AppUser user);
}
