package gutek.services;

import gutek.entities.users.AppUser;
import gutek.repositories.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AppUserService appUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appUserService = new AppUserService(appUserRepository);
    }

    @Test
    void testRegisterUser() {
        // Arrange
        String username = "testuser";
        String rawPassword = "password";

        // Act
        appUserService.registerUser(username, rawPassword);

        // Assert
        verify(appUserRepository, times(1)).save(argThat(user ->
                user.getUsername().equals(username) && passwordEncoder.matches(rawPassword, user.getPassword())
        ));
    }

    @Test
    void testLoginUser_Success() {
        // Arrange
        String username = "testuser";
        String rawPassword = "password";
        AppUser mockUser = new AppUser();
        mockUser.setUsername(username);
        mockUser.setPassword(passwordEncoder.encode(rawPassword));
        when(appUserRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        boolean loginResult = appUserService.loginUser(username, rawPassword);

        // Assert
        assertTrue(loginResult);
        verify(appUserRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoginUser_Failure_InvalidPassword() {
        // Arrange
        String username = "testuser";
        String rawPassword = "password";
        String invalidPassword = "invalidpassword";
        AppUser mockUser = new AppUser();
        mockUser.setUsername(username);
        mockUser.setPassword(passwordEncoder.encode(rawPassword));
        when(appUserRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        boolean loginResult = appUserService.loginUser(username, invalidPassword);

        // Assert
        assertFalse(loginResult);
        verify(appUserRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoginUser_Failure_UserNotFound() {
        // Arrange
        String username = "unknownuser";
        String rawPassword = "password";
        when(appUserRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        boolean loginResult = appUserService.loginUser(username, rawPassword);

        // Assert
        assertFalse(loginResult);
        verify(appUserRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindUserByUsername_UserExists() {
        // Arrange
        String username = "testuser";
        AppUser mockUser = new AppUser();
        mockUser.setUsername(username);
        when(appUserRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<AppUser> result = appUserService.findUserByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(appUserRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindUserByUsername_UserNotFound() {
        // Arrange
        String username = "unknownuser";
        when(appUserRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<AppUser> result = appUserService.findUserByUsername(username);

        // Assert
        assertFalse(result.isPresent());
        verify(appUserRepository, times(1)).findByUsername(username);
    }
}