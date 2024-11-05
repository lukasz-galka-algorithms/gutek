package gutek.repositories;

import gutek.entities.languages.AppLocaleSetting;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for managing {@link AppLocaleSetting} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 */
@Repository
@Transactional
public interface AppLocaleSettingRepository extends JpaRepository<AppLocaleSetting,Long> {
    /**
     * Finds the first {@link AppLocaleSetting} entity ordered by its ID in ascending order.
     *
     * @return An {@link Optional} containing the first {@link AppLocaleSetting} if found, otherwise empty.
     */
    Optional<AppLocaleSetting> findTopByOrderByIdAppLocaleSettingAsc();
}
