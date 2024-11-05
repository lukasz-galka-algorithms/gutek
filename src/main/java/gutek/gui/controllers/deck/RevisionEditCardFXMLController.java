package gutek.gui.controllers.deck;

import gutek.entities.cards.CardBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.CardService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;
import java.util.Optional;
import static gutek.utils.AlertMessageUtil.showAlert;

/**
 * Controller class for editing existing cards within a deck.
 * <p>
 * This class provides a user interface allowing users to update the front and back text of a card.
 * It enforces that the front text is unique within the deck. Additionally, it incorporates menu components
 * for navigating other deck-related actions.
 */
@Component
public class RevisionEditCardFXMLController extends FXMLController {

    /**
     * Root pane for this view.
     */
    @FXML
    private BorderPane rootPane;

    /**
     * Container for menu components.
     */
    @FXML
    private VBox menuContainer;

    /**
     * Label for the front text field.
     */
    @FXML
    private Label frontLabel;

    /**
     * Text field for editing the front text of the card.
     */
    @FXML
    private TextField frontTextField;

    /**
     * Label for the back text field.
     */
    @FXML
    private Label backLabel;

    /**
     * Text field for editing the back text of the card.
     */
    @FXML
    private TextField backTextField;

    /**
     * Button for saving the updated card information.
     */
    @FXML
    private Button saveButton;

    /**
     * Service responsible for managing card-related operations.
     */
    private final CardService cardService;

    /**
     * Controller for managing the menu bar.
     */
    private final MenuBarFXMLController menuBarFXMLController;

    /**
     * Controller for managing deck-specific menu actions.
     */
    private final MenuDeckFXMLController menuDeckFXMLController;

    /**
     * The card being edited in this view.
     */
    private CardBase cardToEdit;


    /**
     * Constructs a new `RevisionEditCardFXMLController` for editing an existing card.
     *
     * @param stage               The main frame of the application.
     * @param fxmlFileLoader      Utility for loading FXML files associated with this scene.
     * @param translationService  Service for handling translations within the view.
     * @param cardService         Service for managing cards.
     * @param menuBarFXMLController Controller for the main menu bar.
     * @param menuDeckFXMLController Controller for deck-specific menu actions.
     */
    public RevisionEditCardFXMLController(MainStage stage,
                                          FXMLFileLoader fxmlFileLoader,
                                          TranslationService translationService,
                                          MenuBarFXMLController menuBarFXMLController,
                                          MenuDeckFXMLController menuDeckFXMLController,
                                          CardService cardService) {
        super(stage, fxmlFileLoader, "/fxml/deck/RevisionEditCardView.fxml", translationService);
        this.cardService = cardService;
        this.menuBarFXMLController = menuBarFXMLController;
        this.menuDeckFXMLController = menuDeckFXMLController;
    }

    /**
     * Initializes the view with parameters, setting the card to edit if provided.
     * Configures menu components, sets up button actions, and binds the save button to the save action.
     *
     * @param params Array of parameters, where the first element is expected to be a `CardBase` instance.
     */
    @Override
    public void initWithParams(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof CardBase) {
            this.cardToEdit = (CardBase) params[0];
            setCardToEdit(cardToEdit);
            menuDeckFXMLController.initWithParams(cardToEdit.getDeck());
        }
        menuBarFXMLController.initWithParams();

        menuContainer.getChildren().setAll(menuBarFXMLController.getRoot(), menuDeckFXMLController.getRoot());

        saveButton.setOnAction(e -> handleSaveCard());
    }

    /**
     * Saves the updated card details after validating that the fields are not empty and the front text is unique.
     * If successful, the view returns to the main revision search scene.
     */
    private void handleSaveCard() {
        String frontText = frontTextField.getText().trim();
        String backText = backTextField.getText().trim();

        if (frontText.isEmpty() || backText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, translationService.getTranslation("deck_view.edit_card.empty_text"));
            return;
        }

        Optional<CardBase> existingCard = cardService.findCardByFrontAndDeck(frontText, cardToEdit.getDeck());
        if (existingCard.isPresent() && !existingCard.get().getIdCard().equals(cardToEdit.getIdCard())) {
            showAlert(Alert.AlertType.WARNING, translationService.getTranslation("deck_view.edit_card.front_unique"));
            return;
        }

        cardToEdit.setFront(frontText);
        cardToEdit.setBack(backText);
        cardService.saveCard(cardToEdit);
        showAlert(Alert.AlertType.INFORMATION, translationService.getTranslation("deck_view.edit_card.edit_success"));

        stage.setScene(MainStageScenes.REVISION_SEARCH_SCENE, cardToEdit.getDeck());
    }

    /**
     * Updates the size of the view components based on the current scaling factor.
     * Adjusts font size and component dimensions dynamically.
     */
    @Override
    public void updateSize() {
        menuBarFXMLController.updateSize();
        menuDeckFXMLController.updateSize();

        double scaleFactor = stage.getStageScaleFactor();
        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";

        frontLabel.setStyle(fontSizeStyle);
        backLabel.setStyle(fontSizeStyle);
        frontTextField.setStyle(fontSizeStyle);
        backTextField.setStyle(fontSizeStyle);
        saveButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;");

        frontLabel.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        backLabel.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        frontTextField.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        backTextField.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        saveButton.setPrefSize(100 * scaleFactor, 40 * scaleFactor);
    }

    /**
     * Updates the text labels and button text based on the current language settings.
     */
    @Override
    public void updateTranslation() {
        menuBarFXMLController.updateTranslation();
        menuDeckFXMLController.updateTranslation();

        frontLabel.setText(translationService.getTranslation("deck_view.edit_card.front"));
        backLabel.setText(translationService.getTranslation("deck_view.edit_card.back"));
        saveButton.setText(translationService.getTranslation("deck_view.edit_card.save_button"));
    }

    /**
     * Sets the card to be edited and populates the text fields with the card's existing front and back text.
     *
     * @param card the card to edit
     */
    public void setCardToEdit(CardBase card) {
        this.cardToEdit = card;
        frontTextField.setText(card.getFront());
        backTextField.setText(card.getBack());
    }
}
