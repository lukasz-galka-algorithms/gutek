package gutek.gui.controllers.deck;

import gutek.entities.algorithms.AlgorithmHiperparameter;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.validation.FieldValueValidator;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import static gutek.utils.AlertMessageUtil.showAlert;

/**
 * Controller for managing the settings of a deck's revision algorithm.
 * <p>
 * This view provides a graphical interface for displaying, adjusting, and saving the hyperparameters
 * of the algorithm associated with a selected deck. Users can modify settings and persist the changes.
 */
@Component
public class RevisionSettingsFXMLController extends FXMLController {

    /**
     * Root pane for the settings view layout.
     */
    @FXML
    private BorderPane rootPane;

    /**
     * Container for the menu components.
     */
    @FXML
    private VBox menuContainer;

    /**
     * Container holding the settings controls (labels and text fields).
     */
    @FXML
    private VBox settingsContainer;

    /**
     * Scroll pane to allow scrolling through settings if they exceed viewable space.
     */
    @FXML
    private ScrollPane scrollPane;

    /**
     * Button for saving the updated settings.
     */
    @FXML
    private Button saveButton;

    /**
     * Controller for the main menu bar of the application.
     */
    private final MenuBarFXMLController menuBarFXMLController;

    /**
     * Controller for the deck-specific menu actions.
     */
    private final MenuDeckFXMLController menuDeckFXMLController;

    /**
     * Map storing the hyperparameter fields of the revision algorithm.
     * Key: Hyperparameter name, Value: TextField for user input.
     */
    private Map<String, TextField> hiperparameterFields;

    /**
     * The deck for which the revision algorithm settings are managed.
     */
    private DeckBase deck;

    /**
     * Constructs a new `RevisionSettingsFXMLController` for managing and adjusting revision algorithm settings.
     *
     * @param stage                 The main stage of the application.
     * @param fxmlFileLoader        Utility for loading FXML files associated with this scene.
     * @param translationService    Service for retrieving translations for the UI.
     * @param menuBarFXMLController Controller for the main menu bar.
     * @param menuDeckFXMLController Controller for deck-specific menu actions.
     */
    public RevisionSettingsFXMLController(MainStage stage,
                                          FXMLFileLoader fxmlFileLoader,
                                          TranslationService translationService,
                                          MenuBarFXMLController menuBarFXMLController,
                                          MenuDeckFXMLController menuDeckFXMLController) {
        super(stage, fxmlFileLoader, "/fxml/deck/RevisionSettingsView.fxml", translationService);
        this.menuBarFXMLController = menuBarFXMLController;
        this.menuDeckFXMLController = menuDeckFXMLController;
    }

    /**
     * Initializes the view with parameters, setting up the deck and configuring the menu components.
     * Binds the save button action to persist the modified settings.
     *
     * @param params Array of parameters, where the first element is expected to be a `DeckBase` instance.
     */
    @Override
    public void initWithParams(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof DeckBase) {
            this.deck = (DeckBase) params[0];
            menuDeckFXMLController.initWithParams(deck);
        }
        menuBarFXMLController.initWithParams();

        menuContainer.getChildren().setAll(menuBarFXMLController.getRoot(), menuDeckFXMLController.getRoot());

        saveButton.setOnAction(e -> saveSettings());
    }

    /**
     * Loads the current settings of the revision algorithm for display and modification.
     * Each hyperparameter is represented by a label and an editable text field.
     */
    public void loadSettings() {
        settingsContainer.getChildren().clear();
        hiperparameterFields = new HashMap<>();

        RevisionAlgorithm<?> algorithm = deck.getRevisionAlgorithm();
        Field[] fields = algorithm.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(AlgorithmHiperparameter.class)) {
                field.setAccessible(true);

                AlgorithmHiperparameter annotation = field.getAnnotation(AlgorithmHiperparameter.class);
                String translationKey = annotation.descriptionTranslationKey();

                Label label = new Label(translationService.getTranslation(translationKey));
                label.setUserData(translationKey);
                TextField textField = new TextField();

                try {
                    Object value = field.get(algorithm);
                    textField.setText(value != null ? value.toString() : "");
                } catch (IllegalAccessException ignored) {
                }

                HBox hbox = new HBox(10, label, textField);
                hbox.setAlignment(Pos.CENTER);
                settingsContainer.getChildren().add(hbox);

                hiperparameterFields.put(field.getName(), textField);
            }
        }
    }

    /**
     * Saves the modified settings back to the revision algorithm by validating and applying each input.
     * Displays an alert if any field contains invalid data.
     */
    private void saveSettings() {
        RevisionAlgorithm<?> algorithm = deck.getRevisionAlgorithm();

        for (Map.Entry<String, TextField> entry : hiperparameterFields.entrySet()) {
            String hiperparameterName = entry.getKey();
            TextField textField = entry.getValue();
            String valueString = textField.getText();

            try {
                Object convertedValue = FieldValueValidator.validateAndReturnConverted(algorithm, hiperparameterName, valueString, translationService);
                Field field = algorithm.getClass().getDeclaredField(hiperparameterName);
                field.setAccessible(true);
                field.set(algorithm, convertedValue);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, translationService.getTranslation("deck_view.settings.invalid_input") + "\n" + e.getMessage());
                return;
            }
        }

        showAlert(Alert.AlertType.INFORMATION, translationService.getTranslation("deck_view.settings.save_success"));
    }

    /**
     * Updates the size of the view components based on the window size and scale factor.
     * Adjusts font sizes and component dimensions dynamically.
     */
    @Override
    public void updateSize() {
        menuBarFXMLController.updateSize();
        menuDeckFXMLController.updateSize();

        double scaleFactor = stage.getStageScaleFactor();

        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";

        saveButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;");
        saveButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);

        for (HBox hbox : settingsContainer.getChildren().stream()
                .filter(node -> node instanceof HBox)
                .map(node -> (HBox) node)
                .toList()) {
            Label label = (Label) hbox.getChildren().get(0);
            label.setAlignment(Pos.CENTER_RIGHT);
            TextField textField = (TextField) hbox.getChildren().get(1);

            label.setStyle(fontSizeStyle);
            textField.setStyle(fontSizeStyle);

            label.setPrefSize(400 * scaleFactor, 40 * scaleFactor);
            textField.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
        }
    }

    /**
     * Updates the text in the view components based on the current language settings.
     */
    @Override
    public void updateTranslation() {
        menuBarFXMLController.updateTranslation();
        menuDeckFXMLController.updateTranslation();

        saveButton.setText(translationService.getTranslation("deck_view.settings.save_button"));

        for (HBox hbox : settingsContainer.getChildren().stream()
                .filter(node -> node instanceof HBox)
                .map(node -> (HBox) node)
                .toList()) {
            Label label = (Label) hbox.getChildren().getFirst();
            String translationKey = (String) label.getUserData();
            label.setText(translationService.getTranslation(translationKey));
        }
    }

    /**
     * Updates the view by loading the settings and initializing the view components.
     */
    @Override
    public void updateView(){
        menuBarFXMLController.updateView();
        menuDeckFXMLController.updateView();

        loadSettings();
    }
}
