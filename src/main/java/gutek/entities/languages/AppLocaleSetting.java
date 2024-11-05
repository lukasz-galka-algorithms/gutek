package gutek.entities.languages;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the locale settings for the application, including language and country information.
 * This class stores the language and country codes used to determine the locale settings for a user.
 * It can be used to configure internationalization preferences.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppLocaleSetting {

    /** Unique identifier for the locale setting. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idAppLocaleSetting;

    /** The language code (e.g., "en" for English, "fr" for French). */
    @Column(nullable = false)
    private String language;

    /** The country code (e.g., "US" for United States, "FR" for France). */
    @Column(nullable = false)
    private String country;
}