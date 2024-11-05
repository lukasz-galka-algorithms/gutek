package gutek.repositories;

import gutek.entities.users.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for managing {@link AppUser} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 */
@Repository
@Transactional
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    /**
     * Finds an {@link AppUser} entity by its username.
     *
     * @param username the username of the {@link AppUser} to be found.
     * @return An {@link Optional} containing the {@link AppUser} if found, otherwise empty.
     */
    Optional<AppUser> findByUsername(String username);
}
