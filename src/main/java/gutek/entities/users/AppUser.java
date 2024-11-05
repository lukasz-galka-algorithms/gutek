package gutek.entities.users;

import gutek.entities.decks.DeckBase;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user of the application.
 *
 * This class stores information about the user, including their username, password, and the list of decks they own.
 * The relationship between users and decks is one-to-many, meaning each user can own multiple decks.
 */
@Entity
@Data
public class AppUser {

    /** Unique identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idUser;

    /** The unique username of the user. */
    @Column(unique = true, nullable = false)
    private String username;

    /** The password of the user. */
    @Column(nullable = false)
    private String password;

    /** The list of decks owned by the user. */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<DeckBase> decks = new ArrayList<>();
}
