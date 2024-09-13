package gutek.gui.launch;

import gutek.entities.users.AppUser;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.MainFrameViews;
import gutek.services.AppUserService;
import gutek.services.TranslationService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * The `LoginView` class represents the login and registration screen.
 * It allows users to log in with an existing account or register a new account.
 */
@Component
public class LoginView  extends AppView {
    /** Service for user management. */
    private final AppUserService appUserService;
    /** Label for the login field. */
    JLabel loginLabel;
    /** Label for the password field. */
    JLabel passwordLabel;
    /** Text field for the user's login (username). */
    JTextField loginField;
    /** Password field for the user's password. */
    JPasswordField passwordField;
    /** Button to register a new user. */
    JButton registerButton;
    /** Button to log in. */
    JButton loginButton;
    /** Button to return to the language selection view. */
    JButton backButton;
    /**
     * Constructs the `LoginView`, initializing its components.
     *
     * @param frame the main frame of the application
     * @param translationService the service used for managing translations
     * @param appUserService the service responsible for handling user registration and login
     */
    public LoginView(MainFrame frame,TranslationService translationService, AppUserService appUserService) {
        super(frame,translationService);
        this.appUserService = appUserService;

        setLayout(null);

        loginLabel = new JLabel("", SwingConstants.RIGHT);
        add(loginLabel);
        loginField = new JTextField();
        add(loginField);

        passwordLabel = new JLabel("", SwingConstants.RIGHT);
        add(passwordLabel);
        passwordField = new JPasswordField();
        add(passwordField);

        registerButton = new JButton("");
        add(registerButton);
        loginButton = new JButton("");
        add(loginButton);

        backButton = new JButton("");
        add(backButton);

        loginButton.addActionListener(e -> handleLogin());

        registerButton.addActionListener(e -> handleRegister());

        backButton.addActionListener(e -> {
            frame.setView(MainFrameViews.LANGUAGE_SELECTION_VIEW);
        });
    }
    /**
     * Updates the size and layout of the components based on the current window size and scale factor.
     */
    public void updateSize() {
        super.updateSize();

        Dimension dimensionScaled = frame.getScaledSize();
        double scaleFactor = frame.getScaleFactor();

        this.setPreferredSize(dimensionScaled);

        int labelWidth = (int) (200 * scaleFactor);
        int labelHeight = (int) (30 * scaleFactor);
        int fieldWidth = (int) (200 * scaleFactor);
        int fieldHeight = (int) (30 * scaleFactor);

        loginLabel.setBounds((dimensionScaled.width - fieldWidth - labelWidth) / 2, (int) (50 * scaleFactor), labelWidth, labelHeight);
        loginLabel.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));
        loginField.setBounds((dimensionScaled.width - fieldWidth + labelWidth) / 2, (int) (50 * scaleFactor), fieldWidth, fieldHeight);
        loginField.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));

        passwordLabel.setBounds((dimensionScaled.width - fieldWidth - labelWidth) / 2, (int) (100 * scaleFactor), labelWidth, labelHeight);
        passwordLabel.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));
        passwordField.setBounds((dimensionScaled.width - fieldWidth + labelWidth) / 2, (int) (100 * scaleFactor), fieldWidth, fieldHeight);
        passwordField.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));

        int buttonWidth = (int) (120 * scaleFactor);
        int buttonHeight = (int) (40 * scaleFactor);
        registerButton.setBounds((dimensionScaled.width - buttonWidth * 2) / 2, (int) (150 * scaleFactor), buttonWidth, buttonHeight);
        registerButton.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));

        loginButton.setBounds((dimensionScaled.width + buttonWidth) / 2, (int) (150 * scaleFactor), buttonWidth, buttonHeight);
        loginButton.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));

        backButton.setBounds((dimensionScaled.width - buttonWidth / 2) / 2, (int) (200 * scaleFactor), buttonWidth, buttonHeight);
        backButton.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));

        revalidate();
        repaint();
    }
    /**
     * Updates the text in the view components based on the current language settings.
     */
    public void updateTranslation(){
        super.updateTranslation();
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
    private void handleLogin() {
        String username = loginField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, translationService.getTranslation("login_view.empty_username"), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, translationService.getTranslation("login_view.empty_password"), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = appUserService.loginUser(username, password);
        if (success) {
            frame.setLoggedUser(appUserService.findUserByUsername(username).get());
            frame.setView(MainFrameViews.DECKS_VIEW);
        } else {
            JOptionPane.showMessageDialog(this, translationService.getTranslation("login_view.login_failed"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Handles the registration process when the register button is clicked.
     * Validates the input and registers a new user if the username is not already taken.
     */
    private void handleRegister() {
        String username = loginField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, translationService.getTranslation("login_view.empty_username"), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, translationService.getTranslation("login_view.empty_password"), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<AppUser> user = appUserService.findUserByUsername(username);

        if(user.isEmpty()){
            appUserService.registerUser(username,password);
            frame.setLoggedUser(appUserService.findUserByUsername(username).get());
            frame.setView(MainFrameViews.DECKS_VIEW);
        } else {
            JOptionPane.showMessageDialog(this, translationService.getTranslation("login_view.user_exists"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
