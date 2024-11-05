package gutek.gui.controllers.launch;

import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.AppUserService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import static gutek.utils.AlertMessageUtil.showAlert;

/**
 * Controller for the login view, handling user authentication and registration.
 * <p>
 * This view allows users to enter their login credentials, log in, register a new account,
 * or navigate back to the language selection view.
 */
@Component
public class LoginFXMLController extends FXMLController {

    /** Label for the login field. */
    @FXML
    private Label loginLabel;

    /** Label for the password field. */
    @FXML
    private Label passwordLabel;

    /** Text field for the user's login (username). */
    @FXML
    private TextField loginField;

    /** Password field for the user's password. */
    @FXML
    private PasswordField passwordField;

    /** Button to register a new user. */
    @FXML
    private Button registerButton;

    /** Button to log in. */
    @FXML
    private Button loginButton;

    /** Button to return to the language selection view. */
    @FXML
    private Button backButton;

    /** Service for user management. */
    private final AppUserService appUserService;

    /**
     * Constructs the `LoginFXMLController`, initializing its components.
     *
     * @param stage the main stage of the application
     * @param fxmlFileLoader     utility for loading the FXML file associated with this view
     * @param translationService the service used for managing translations
     * @param appUserService the service responsible for handling user registration and login
     */
    public LoginFXMLController(MainStage stage, FXMLFileLoader fxmlFileLoader, TranslationService translationService, AppUserService appUserService) {
        super( stage, fxmlFileLoader, "/fxml/launch/LoginView.fxml", translationService);
        this.appUserService = appUserService;
    }

    /**
     * Updates the size and layout of the components based on the current window size and scale factor.
     */
    public void updateSize() {
        double scaleFactor = stage.getStageScaleFactor();
        String fontSizeStyle = "-fx-font-size: " + (15 * scaleFactor) + "px;";
        String fontSizeLargeStyle = "-fx-font-size: " + (20 * scaleFactor) + "px;";

        loginLabel.setStyle(fontSizeLargeStyle);
        passwordLabel.setStyle(fontSizeLargeStyle);
        loginField.setStyle(fontSizeStyle);
        passwordField.setStyle(fontSizeStyle);
        registerButton.setStyle(fontSizeStyle + " -fx-background-color: blue; -fx-text-fill: white;");
        loginButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;");
        backButton.setStyle(fontSizeStyle + " -fx-background-color: gray; -fx-text-fill: white;");

        loginLabel.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        passwordLabel.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        loginField.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        passwordField.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        registerButton.setPrefSize(100 * scaleFactor, 40 * scaleFactor);
        loginButton.setPrefSize(100 * scaleFactor, 40 * scaleFactor);
        backButton.setPrefSize(100 * scaleFactor, 40 * scaleFactor);
    }

    /**
     * Updates the text in the view components based on the current language settings.
     */
    @Override
    public void updateTranslation(){
        loginLabel.setText(translationService.getTranslation("login_view.login_label"));
        passwordLabel.setText(translationService.getTranslation("login_view.password_label"));
        registerButton.setText(translationService.getTranslation("login_view.register_button"));
        loginButton.setText(translationService.getTranslation("login_view.log_button"));
        backButton.setText(translationService.getTranslation("login_view.back_button"));
    }

    /**
     * Handles the login process when the login button is clicked.
     * Validates the user's credentials and logs them in if valid.
     */
    @FXML
    private void handleLogin() {
        String username = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,translationService.getTranslation("login_view.empty_username"));
            return;
        }

        if (password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,translationService.getTranslation("login_view.empty_password"));
            return;
        }

        boolean success = appUserService.loginUser(username, password);
        if (success) {
            stage.setLoggedUser(appUserService.findUserByUsername(username).orElse(null));
            stage.setScene(MainStageScenes.DECKS_SCENE);
        } else {
            showAlert(Alert.AlertType.WARNING,translationService.getTranslation("login_view.login_failed"));
        }
    }

    /**
     * Handles the registration process when the register button is clicked.
     * Validates the input and registers a new user if the username is not already taken.
     */
    @FXML
    private void handleRegister() {
        String username = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,translationService.getTranslation("login_view.empty_username"));
            return;
        }

        if (password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,translationService.getTranslation("login_view.empty_password"));
            return;
        }

        if (appUserService.findUserByUsername(username).isEmpty()) {
            appUserService.registerUser(username, password);
            stage.setLoggedUser(appUserService.findUserByUsername(username).orElse(null));
            stage.setScene(MainStageScenes.DECKS_SCENE);
        } else {
            showAlert(Alert.AlertType.WARNING,translationService.getTranslation("login_view.user_exists"));
        }
    }

    /**
     * Handles the action of going back to the language selection view.
     * Navigates the user to the language selection screen.
     */
    @FXML
    private void handleBack(){
        stage.setScene(MainStageScenes.LANGUAGE_SELECTION_SCENE);
    }
}
