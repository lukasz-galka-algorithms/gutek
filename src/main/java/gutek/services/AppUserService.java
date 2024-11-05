package gutek.services;

import gutek.entities.users.AppUser;
import gutek.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing users and authentication operations.
 * Provides methods for user registration, login, and password validation.
 */
@Service
public class AppUserService {

    /**
     * Repository for interacting with the {@link AppUser} entities in the database.
     */
    private final AppUserRepository appUserRepository;

    /**
     * Password encoder for securely storing and verifying user passwords.
     */
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor for injecting dependencies.
     *
     * @param appUserRepository the repository for performing CRUD operations on {@link AppUser} entities.
     */
    @Autowired
    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Registers a new user by encoding the provided password and saving the user to the repository.
     *
     * @param username the username of the new user.
     * @param password the raw password of the new user.
     * @return the saved {@link AppUser} entity.
     */
    public AppUser registerUser(String username, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        return appUserRepository.save(user);
    }

    /**
     * Logs in a user by validating the provided username and password.
     *
     * @param username    the username provided for login.
     * @param rawPassword the raw password provided for login.
     * @return true if the login was successful, false otherwise.
     */
    public boolean loginUser(String username, String rawPassword) {
        Optional<AppUser> userOpt = findUserByUsername(username);
        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            return checkPassword(user, rawPassword);
        }
        return false;
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to be found.
     * @return an {@link Optional} containing the found user, or empty if no user was found.
     */
    public Optional<AppUser> findUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    /**
     * Checks if the provided raw password matches the encoded password of the user.
     *
     * @param user        the user whose password is being checked.
     * @param rawPassword the raw password provided for comparison.
     * @return true if the passwords match, false otherwise.
     */
    private boolean checkPassword(AppUser user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}