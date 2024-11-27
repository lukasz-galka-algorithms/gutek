package gutek.gui.controllers.launch;

import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.AppUserService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoginFXMLControllerTest extends ApplicationTest {

    private LoginFXMLController controller;
    private AppUserService mockAppUserService;
    private MainStage mockStage;

    @BeforeAll
    static void initToolkit() {
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.startup(() -> {
                });
            }
        } catch (IllegalStateException e) {
            // ignore
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);
        TranslationService mockTranslationService = mock(TranslationService.class);
        mockStage = mock(MainStage.class);
        mockAppUserService = mock(AppUserService.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");

        controller = new LoginFXMLController(
                mockStage,
                mockFxmlFileLoader,
                mockTranslationService,
                mockAppUserService
        );

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/launch/LoginView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/launch/LoginView.fxml"), any()))
                .thenReturn(mockRoot);

        controller.loadViewFromFXML();

        Platform.runLater(() -> {
            Stage stage = new Stage();
            when(mockStage.getStage()).thenReturn(stage);
            stage.setScene(new Scene(controller.getRoot()));
            stage.show();

            WaitForAsyncUtils.waitForFxEvents();
            controller.initWithParams();
        });
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        Label loginLabel = lookup("#loginLabel").queryAs(Label.class);
        Label passwordLabel = lookup("#passwordLabel").queryAs(Label.class);
        Button registerButton = lookup("#registerButton").queryAs(Button.class);
        Button loginButton = lookup("#loginButton").queryAs(Button.class);
        Button backButton = lookup("#backButton").queryAs(Button.class);

        // Assert
        assertNotNull(loginLabel, "loginLabel should not be null");
        assertEquals("Translated", loginLabel.getText(), "loginLabel text should be translated");

        assertNotNull(passwordLabel, "passwordLabel should not be null");
        assertEquals("Translated", passwordLabel.getText(), "passwordLabel text should be translated");

        assertNotNull(registerButton, "registerButton should not be null");
        assertEquals("Translated", registerButton.getText(), "registerButton text should be translated");

        assertNotNull(loginButton, "loginButton should not be null");
        assertEquals("Translated", loginButton.getText(), "loginButton text should be translated");

        assertNotNull(backButton, "backButton should not be null");
        assertEquals("Translated", backButton.getText(), "backButton text should be translated");
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Label loginLabel = lookup("#loginLabel").queryAs(Label.class);
        Label passwordLabel = lookup("#passwordLabel").queryAs(Label.class);
        TextField loginField = lookup("#loginField").queryAs(TextField.class);
        PasswordField passwordField = lookup("#passwordField").queryAs(PasswordField.class);
        Button registerButton = lookup("#registerButton").queryAs(Button.class);
        Button loginButton = lookup("#loginButton").queryAs(Button.class);
        Button backButton = lookup("#backButton").queryAs(Button.class);

        // Assert
        assertNotNull(loginLabel, "loginLabel should not be null");
        assertNotNull(loginLabel.getStyle(), "loginLabel should have updated style");
        assertTrue(loginLabel.getStyle().contains("-fx-font-size:"), "loginLabel should have font size updated");

        assertNotNull(passwordLabel, "passwordLabel should not be null");
        assertNotNull(passwordLabel.getStyle(), "passwordLabel should have updated style");
        assertTrue(passwordLabel.getStyle().contains("-fx-font-size:"), "passwordLabel should have font size updated");

        assertNotNull(loginField, "loginField should not be null");
        assertNotNull(loginField.getStyle(), "loginField should have updated style");
        assertTrue(loginField.getStyle().contains("-fx-font-size:"), "loginField should have font size updated");

        assertNotNull(passwordField, "passwordField should not be null");
        assertNotNull(passwordField.getStyle(), "passwordField should have updated style");
        assertTrue(passwordField.getStyle().contains("-fx-font-size:"), "passwordField should have font size updated");

        assertNotNull(registerButton, "registerButton should not be null");
        assertNotNull(registerButton.getStyle(), "registerButton should have updated style");
        assertTrue(registerButton.getStyle().contains("-fx-font-size:"), "registerButton should have font size updated");

        assertNotNull(loginButton, "loginButton should not be null");
        assertNotNull(loginButton.getStyle(), "loginButton should have updated style");
        assertTrue(loginButton.getStyle().contains("-fx-font-size:"), "loginButton should have font size updated");

        assertNotNull(backButton, "backButton should not be null");
        assertNotNull(backButton.getStyle(), "backButton should have updated style");
        assertTrue(backButton.getStyle().contains("-fx-font-size:"), "backButton should have font size updated");
    }

    @Test
    void testHandleLoginWithValidCredentials() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        when(mockAppUserService.loginUser("testUser", "testPassword")).thenReturn(true);
        when(mockAppUserService.findUserByUsername("testUser")).thenReturn(Optional.of(mock()));

        // Act
        Platform.runLater(() -> {
            TextField loginField = lookup("#loginField").queryAs(TextField.class);
            PasswordField passwordField = lookup("#passwordField").queryAs(PasswordField.class);
            loginField.setText("testUser");
            passwordField.setText("testPassword");

            Button loginButton = lookup("#loginButton").queryAs(Button.class);
            loginButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockAppUserService, times(1)).loginUser("testUser", "testPassword");
        verify(mockStage, times(1)).setLoggedUser(any());
        verify(mockStage, times(1)).setScene(MainStageScenes.DECKS_SCENE);
    }

    @Test
    void testHandleRegisterWithValidData() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        when(mockAppUserService.findUserByUsername("newUser")).thenReturn(Optional.empty());

        // Act
        Platform.runLater(() -> {
            TextField loginField = lookup("#loginField").queryAs(TextField.class);
            PasswordField passwordField = lookup("#passwordField").queryAs(PasswordField.class);
            loginField.setText("newUser");
            passwordField.setText("newPassword");

            Button registerButton = lookup("#registerButton").queryAs(Button.class);
            registerButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockAppUserService, times(1)).registerUser("newUser", "newPassword");
        verify(mockStage, times(1)).setLoggedUser(any());
        verify(mockStage, times(1)).setScene(MainStageScenes.DECKS_SCENE);
    }

    @Test
    void testHandleBack() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Platform.runLater(() -> {
            Button backButton = lookup("#backButton").queryAs(Button.class);
            backButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.LANGUAGE_SELECTION_SCENE);
    }
}
