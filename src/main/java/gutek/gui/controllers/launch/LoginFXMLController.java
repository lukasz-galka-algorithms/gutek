package gutek.gui.controllers.launch;

import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.AppUserService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

import static gutek.utils.AlertMessageUtil.showWarningAlert;

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

    /**
     * Icon for the "registerButton".
     */
    private ImageView registerButtonIcon;

    /**
     * Icon for the "loginButton".
     */
    private ImageView loginButtonIcon;

    /**
     * Icon for the "backButton".
     */
    private ImageView backButtonIcon;

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
     * Initializes the view with parameters.
     * @param params Array of parameters for initialization (not used in this implementation).
     */
    @Override
    public void initWithParams(Object... params) {
        initializeIcons();
    }

    /**
     * Updates the size and layout of the components based on the current window size and scale factor.
     */
    @Override
    public void updateSize() {
        double scaleFactor = stage.getStageScaleFactor();
        String fontSizeStyle = "-fx-font-size: " + (15 * scaleFactor) + "px;";
        String fontSizeLargeStyle = "-fx-font-size: " + (20 * scaleFactor) + "px;";
        String radiusStyle = "-fx-background-radius: " + (20 * scaleFactor) + "; -fx-border-radius: " + (20 * scaleFactor) + ";";

        loginLabel.setStyle(fontSizeLargeStyle);
        passwordLabel.setStyle(fontSizeLargeStyle);
        loginField.setStyle(fontSizeStyle + radiusStyle);
        passwordField.setStyle(fontSizeStyle + radiusStyle);
        registerButton.setStyle(fontSizeStyle + " -fx-background-color: blue; -fx-text-fill: white;" + radiusStyle);
        loginButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;" + radiusStyle);
        backButton.setStyle(fontSizeStyle + " -fx-background-color: gray; -fx-text-fill: white;" + radiusStyle);

        loginLabel.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        passwordLabel.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        loginField.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        passwordField.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        registerButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
        loginButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
        backButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);

        updateIcons(scaleFactor);
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
            showWarningAlert(translationService.getTranslation("login_view.empty_username"), translationService, stage);
            return;
        }

        if (password.isEmpty()) {
            showWarningAlert(translationService.getTranslation("login_view.empty_password"), translationService, stage);
            return;
        }

        boolean success = appUserService.loginUser(username, password);
        if (success) {
            stage.setLoggedUser(appUserService.findUserByUsername(username).orElse(null));
            stage.setScene(MainStageScenes.DECKS_SCENE);
        } else {
            showWarningAlert(translationService.getTranslation("login_view.login_failed"), translationService, stage);
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
            showWarningAlert(translationService.getTranslation("login_view.empty_username"), translationService, stage);
            return;
        }

        if (password.isEmpty()) {
            showWarningAlert(translationService.getTranslation("login_view.empty_password"), translationService, stage);
            return;
        }

        if (appUserService.findUserByUsername(username).isEmpty()) {
            appUserService.registerUser(username, password);
            stage.setLoggedUser(appUserService.findUserByUsername(username).orElse(null));
            stage.setScene(MainStageScenes.DECKS_SCENE);
        } else {
            showWarningAlert(translationService.getTranslation("login_view.user_exists"), translationService, stage);
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

    /**
     * Initializes the icons used in the controller's UI components.
     */
    private void initializeIcons() {
        double scaleFactor = stage.getStageScaleFactor();
        loginButtonIcon = ImageUtil.createImageView("/images/icons/success.png");
        loginButton.setGraphic(loginButtonIcon);
        registerButtonIcon = ImageUtil.createImageView("/images/icons/register.png");
        registerButton.setGraphic(registerButtonIcon);
        backButtonIcon = ImageUtil.createImageView("/images/icons/back.png");
        backButton.setGraphic(backButtonIcon);
        updateIcons(scaleFactor);
    }

    /**
     * Updates the size of each icon according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon.
     */
    private void updateIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(loginButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(registerButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(backButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
    }
}
