package gutek.gui.controllers.launch;

import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.ImageUtil;
import gutek.utils.StringUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
    private ComboBox<Locale> languageComboBox;

    /** Button to confirm the selected language and proceed. */
    @FXML
    private Button confirmButton;

    /**
     * Icon for the "confirmButton".
     */
    private ImageView confirmButtonIcon;

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
        availableLocales.forEach(locale -> languageComboBox.getItems().add(locale));

        setupLanguageComboBoxCellFactory();
        setupLanguageComboBoxButtonCell();
        setupLanguageComboBoxAction();

        initializeIcons();
    }

    /**
     * Sets the cell factory for the language ComboBox, configuring each cell
     * to display a language name along with a flag icon.
     * This method utilizes `createLocaleListCell` to define how each language
     * option is rendered in the dropdown list.
     */
    private void setupLanguageComboBoxCellFactory() {
        languageComboBox.setCellFactory(param -> createLocaleListCell());
    }

    /**
     * Sets the button cell for the language ComboBox, defining how the selected
     * language is displayed when the dropdown list is closed.
     * This method configures the button cell to display the language name and flag
     * icon of the currently selected language.
     */
    private void setupLanguageComboBoxButtonCell() {
        languageComboBox.setButtonCell(createLocaleListCell());
    }

    /**
     * Configures the action for the language ComboBox. When a user selects a language
     * from the dropdown list, this method updates the application's locale.
     * It calls `translationService.updateLocale` to apply the selected language,
     * then updates translations in the current stage and controller.
     */
    private void setupLanguageComboBoxAction() {
        languageComboBox.setOnAction(event -> {
            Locale selectedLocale = languageComboBox.getValue();
            if (selectedLocale != null) {
                translationService.updateLocale(selectedLocale);
                updateTranslation();
                stage.updateTranslation();
            }
        });
    }

    /**
     * Creates a ListCell that displays the language name and associated flag icon for a given Locale.
     * This method generates a ListCell used in the ComboBox to display each language option with its
     * flag icon and formatted language name. It adjusts the flag icon size based on the current scale factor.
     *
     * @return a ListCell configured to display the language name and flag icon.
     */
    private ListCell<Locale> createLocaleListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Locale locale, boolean empty) {
                super.updateItem(locale, empty);
                if (locale == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    ImageView imageView = createFlagIcon(locale);
                    double scaleFactor = stage.getStageScaleFactor();
                    ImageUtil.setImageViewSize(imageView, 20 * scaleFactor, 15 * scaleFactor);
                    setText(StringUtil.capitalizeFirstLetter(locale.getDisplayLanguage(locale)));
                    setGraphic(imageView);
                }
            }
        };
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
        String radiusStyle = "-fx-background-radius: " + (20 * scaleFactor) + "; -fx-border-radius: " + (20 * scaleFactor) + ";";

        selectLanguageLabel.setStyle(fontSizeLargeStyle);
        languageComboBox.setStyle(fontSizeStyle + radiusStyle);
        confirmButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;" + radiusStyle);

        selectLanguageLabel.setPrefSize(400 * scaleFactor, 30 * scaleFactor);
        languageComboBox.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        confirmButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);

        setupLanguageComboBoxCellFactory();
        setupLanguageComboBoxButtonCell();
        updateIcons(scaleFactor);
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
        Locale currentLocale = translationService.getCurrentLocale();

        availableLocales.forEach(locale -> {
            if (locale.equals(currentLocale)) {
                languageComboBox.setValue(locale);
            }
        });
    }

    /**
     * Action handler for the confirm button.
     * Sets the selected language as the application's locale and navigates to the login scene.
     */
    @FXML
    private void onConfirmButtonClicked() {
        Locale selectedLocale = languageComboBox.getValue();
        if (selectedLocale != null) {
            translationService.updateLocale(selectedLocale);
        }
        stage.setScene(MainStageScenes.LOGIN_SCENE);
    }

    /**
     * Creates an ImageView containing the flag icon for the specified Locale.
     * This method uses the IconUtil class to generate an icon based on the country code
     * from the given Locale. The flag icon is loaded from the resources folder.
     *
     * @param locale the Locale object representing the language and country.
     * @return an ImageView containing the flag icon, or an empty ImageView if the icon is not found.
     */
    private ImageView createFlagIcon(Locale locale) {
        String countryCode = locale.getCountry();
        return ImageUtil.createImageView("/images/flags/" + countryCode + ".png");
    }

    /**
     * Initializes the icons used in the controller's UI components.
     */
    private void initializeIcons() {
        confirmButtonIcon = ImageUtil.createImageView("/images/icons/success.png");
        confirmButton.setGraphic(confirmButtonIcon);
    }

    /**
     * Updates the size of each icon according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon.
     */
    private void updateIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(confirmButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
    }
}
