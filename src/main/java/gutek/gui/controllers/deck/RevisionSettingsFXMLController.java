package gutek.gui.controllers.deck;

import gutek.entities.algorithms.AlgorithmHiperparameter;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.RevisionAlgorithmService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.ImageUtil;
import gutek.utils.validation.FieldValueValidator;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static gutek.utils.AlertMessageUtil.*;

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
     * Icon for the "saveButton".
     */
    private ImageView saveButtonIcon;

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
     * Service responsible for managing algorithm-related operations.
     */
    private final RevisionAlgorithmService revisionAlgorithmService;

    /**
     * Constructs a new `RevisionSettingsFXMLController` for managing and adjusting revision algorithm settings.
     *
     * @param stage                    The main stage of the application.
     * @param fxmlFileLoader           Utility for loading FXML files associated with this scene.
     * @param translationService       Service for retrieving translations for the UI.
     * @param menuBarFXMLController    Controller for the main menu bar.
     * @param menuDeckFXMLController   Controller for deck-specific menu actions.
     * @param revisionAlgorithmService Service for managing algorithm-related operations.
     */
    public RevisionSettingsFXMLController(MainStage stage,
                                          FXMLFileLoader fxmlFileLoader,
                                          TranslationService translationService,
                                          MenuBarFXMLController menuBarFXMLController,
                                          MenuDeckFXMLController menuDeckFXMLController,
                                          RevisionAlgorithmService revisionAlgorithmService) {
        super(stage, fxmlFileLoader, "/fxml/deck/RevisionSettingsView.fxml", translationService);
        this.menuBarFXMLController = menuBarFXMLController;
        this.menuDeckFXMLController = menuDeckFXMLController;
        this.revisionAlgorithmService = revisionAlgorithmService;
    }

    /**
     * Initializes the view with parameters, setting up the deck and configuring the menu components.
     * Binds the save button action to persist the modified settings.
     *
     * @param params Array of parameters, where the first element is expected to be a `DeckBase` instance.
     */
    @Override
    public void initWithParams(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof DeckBase deckBase) {
            this.deck = deckBase;
            menuDeckFXMLController.initWithParams(deck);
        }
        menuBarFXMLController.initWithParams();

        menuContainer.getChildren().setAll(menuBarFXMLController.getRoot(), menuDeckFXMLController.getRoot());

        saveButton.setOnAction(e -> saveSettings());

        initializeIcons();
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
                AlgorithmHiperparameter annotation = field.getAnnotation(AlgorithmHiperparameter.class);
                String translationKey = annotation.descriptionTranslationKey();

                Label label = new Label(translationService.getTranslation(translationKey));
                label.setUserData(translationKey);
                TextField textField = new TextField();

                try {
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), algorithm.getClass());
                    Method getter = propertyDescriptor.getReadMethod();
                    if (getter != null) {
                        Object value = getter.invoke(algorithm);
                        textField.setText(value != null ? value.toString() : "");
                    }
                } catch (Exception ignored) {
                    //ignore
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

                PropertyDescriptor pd = null;
                for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(algorithm.getClass(), Object.class).getPropertyDescriptors()) {
                    if (propertyDescriptor.getName().equals(hiperparameterName)) {
                        pd = propertyDescriptor;
                        break;
                    }
                }

                if (pd != null && pd.getWriteMethod() != null) {
                    pd.getWriteMethod().invoke(algorithm, convertedValue);
                }

                revisionAlgorithmService.saveAlgorithm(algorithm);
            } catch (Exception e) {
                showErrorAlert(translationService.getTranslation("deck_view.settings.invalid_input") + "\n" + e.getMessage(), translationService, stage);
                return;
            }
        }
        showInfoAlert(translationService.getTranslation("deck_view.settings.save_success"), translationService, stage);
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
        String radiusStyle = "-fx-background-radius: " + (20 * scaleFactor) + "; -fx-border-radius: " + (20 * scaleFactor) + ";";

        saveButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;" + radiusStyle);
        saveButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);

        for (HBox hbox : settingsContainer.getChildren().stream()
                .filter(HBox.class::isInstance)
                .map(HBox.class::cast)
                .toList()) {
            Label label = (Label) hbox.getChildren().get(0);
            label.setAlignment(Pos.CENTER_RIGHT);
            TextField textField = (TextField) hbox.getChildren().get(1);

            label.setStyle(fontSizeStyle);
            textField.setStyle(fontSizeStyle + radiusStyle);
            DropShadow dropShadow = new DropShadow();
            dropShadow.setOffsetX(3.0);
            dropShadow.setOffsetY(3.0);
            dropShadow.setColor(Color.BLACK);
            textField.setEffect(dropShadow);

            label.setPrefSize(400 * scaleFactor, 40 * scaleFactor);
            textField.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
        }

        updateIcons(scaleFactor);
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
                .filter(HBox.class::isInstance)
                .map(HBox.class::cast)
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
    public void updateView() {
        menuBarFXMLController.updateView();
        menuDeckFXMLController.updateView();

        loadSettings();
    }

    /**
     * Initializes the icons used in the controller's UI components.
     */
    private void initializeIcons() {
        saveButtonIcon = ImageUtil.createImageView("/images/icons/save.png");
        saveButton.setGraphic(saveButtonIcon);
    }

    /**
     * Updates the size of each icon according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon.
     */
    private void updateIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(saveButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
    }
}
