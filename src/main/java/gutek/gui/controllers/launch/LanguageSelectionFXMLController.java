package gutek.gui.controllers.launch;

import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.StringUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.List;

/**
 * Controller for selecting the application's language.
 * <p>
 * This view displays a dropdown list of available languages and provides a button
 * to confirm the language selection, updating the application's locale.
 */
@Component
public class LanguageSelectionFXMLController extends FXMLController {

    /** Label prompting the user to select a language. */
    @FXML
    private Label selectLanguageLabel;

    /** ComboBox for displaying and selecting available languages. */
    @FXML
    private ComboBox<String> languageComboBox;

    /** Button to confirm the selected language and proceed. */
    @FXML
    private Button confirmButton;

    /** List of available locales for language selection. */
    private final List<Locale> availableLocales;

    /**
     * Constructs a new `LanguageSelectionFXMLController` for selecting the application's language.
     *
     * @param stage              the main stage of the application
     * @param fxmlFileLoader     utility for loading the FXML file associated with this view
     * @param translationService the service used for managing translations and available locales
     */
    public LanguageSelectionFXMLController(MainStage stage, FXMLFileLoader fxmlFileLoader, TranslationService translationService) {
        super( stage, fxmlFileLoader, "/fxml/launch/LanguageSelectionView.fxml", translationService);
        this.availableLocales = translationService.getAvailableLocales();
    }

    /**
     * Initializes the view with parameters, setting up the available languages in the ComboBox.
     * Configures event handling for language selection and updates translations accordingly.
     *
     * @param params Array of parameters for initialization (not used in this implementation).
     */
    @Override
    public void initWithParams(Object... params) {
        languageComboBox.getItems().clear();
        availableLocales.forEach(locale -> {
            String languageName = StringUtil.capitalizeFirstLetter(locale.getDisplayLanguage(locale));
            languageComboBox.getItems().add(languageName);
        });

        languageComboBox.setOnAction(event -> {
            String selectedLanguage = languageComboBox.getValue();
            Locale selectedLocale = availableLocales.stream()
                    .filter(locale -> StringUtil.capitalizeFirstLetter(locale.getDisplayLanguage(locale)).equals(selectedLanguage))
                    .findFirst()
                    .orElse(translationService.getCurrentLocale());

            translationService.updateLocale(selectedLocale);
            updateTranslation();
            stage.updateTranslation();
        });
    }

    /**
     * Updates the size and layout of the components based on the current window size and scale factor.
     * Adjusts fonts and component dimensions to maintain proportionality across different window sizes.
     */
    @Override
    public void updateSize() {
        double scaleFactor = stage.getStageScaleFactor();

        String fontSizeStyle = "-fx-font-size: " + (15 * scaleFactor) + "px;";
        String fontSizeLargeStyle = "-fx-font-size: " + (20 * scaleFactor) + "px;";

        selectLanguageLabel.setStyle(fontSizeLargeStyle);
        languageComboBox.setStyle(fontSizeStyle);
        confirmButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;");

        selectLanguageLabel.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        languageComboBox.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        confirmButton.setPrefSize(100 * scaleFactor, 40 * scaleFactor);
    }

    /**
     * Updates the translations of the current view components based on the selected language.
     * Refreshes the text of labels and buttons to match the current locale.
     */
    @Override
    public void updateTranslation() {
        selectLanguageLabel.setText(translationService.getTranslation("language_selection.select_language"));
        confirmButton.setText(translationService.getTranslation("window.ok_button"));
    }

    /**
     * Sets the language ComboBox to the currently selected language in the application,
     * ensuring the ComboBox reflects the updated locale.
     */
    @Override
    public void updateView(){
        String currentLocaleName = translationService.getCurrentLocale().getDisplayLanguage(translationService.getCurrentLocale());
        availableLocales.forEach(locale -> {
            String languageName = StringUtil.capitalizeFirstLetter(locale.getDisplayLanguage(locale));
            if (languageName.equalsIgnoreCase(currentLocaleName)) {
                languageComboBox.setValue(languageName);
            }
        });
    }

    /**
     * Action handler for the confirm button.
     * Sets the selected language as the application's locale and navigates to the login scene.
     */
    @FXML
    private void onConfirmButtonClicked() {
        int selectedIndex = languageComboBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Locale selectedLocale = availableLocales.get(selectedIndex);
            translationService.updateLocale(selectedLocale);
        }
        stage.setScene(MainStageScenes.LOGIN_SCENE);
    }
}
