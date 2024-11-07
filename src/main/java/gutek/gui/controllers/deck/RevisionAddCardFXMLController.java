package gutek.gui.controllers.deck;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.CardService;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Optional;

import static gutek.utils.AlertMessageUtil.*;

/**
 * A controller class for managing the addition of new cards to a deck, either manually or by importing from a file.
 * <p>
 * This class provides a user interface with text fields to manually add a card's front and back text.
 * Additionally, it allows users to import multiple cards from a file. The class also manages dynamic translations and
 * layout scaling based on the current stage properties.
 */
@Component
public class RevisionAddCardFXMLController extends FXMLController {

    /**
     * Root pane of this view.
     */
    @FXML
    private BorderPane rootPane;

    /**
     * Container for the menu components.
     */
    @FXML
    private VBox menuContainer;

    /**
     * Label for the card front text field.
     */
    @FXML
    private Label frontLabel;

    /**
     * Text field to input the card front text.
     */
    @FXML
    private TextField frontTextField;

    /**
     * Label for the card back text field.
     */
    @FXML
    private Label backLabel;

    /**
     * Text field to input the card back text.
     */
    @FXML
    private TextField backTextField;

    /**
     * Button to add a new card to the deck.
     */
    @FXML
    private Button addButton;

    /**
     * Icon for the "addButton".
     */
    private ImageView addButtonIcon;

    /**
     * Button to import cards from a file.
     */
    @FXML
    private Button importButton;

    /**
     * Icon for the "importButton".
     */
    private ImageView importButtonIcon;

    /**
     * Service for handling card operations, such as adding new cards.
     */
    private final CardService cardService;

    /**
     * Service for handling deck operations, such as adding cards to a deck.
     */
    private final DeckService deckService;

    /**
     * Controller for managing the menu bar.
     */
    private final MenuBarFXMLController menuBarFXMLController;

    /**
     * Controller for managing deck-specific menu actions.
     */
    private final MenuDeckFXMLController menuDeckFXMLController;

    /**
     * The deck to which new cards will be added.
     */
    private DeckBase deck;

    /**
     * Constructs a new `RevisionAddCardFXMLController`, allowing options to add cards manually or import from a file.
     *
     * @param stage               The main frame of the application.
     * @param fxmlFileLoader      Utility for loading FXML files associated with this scene.
     * @param translationService  Service for translating text in the view.
     * @param cardService         Service for managing cards.
     * @param deckService         Service for managing decks.
     * @param menuBarFXMLController Controller for the main menu bar.
     * @param menuDeckFXMLController Controller for deck-specific menu actions.
     */
    public RevisionAddCardFXMLController(MainStage stage,
                                         FXMLFileLoader fxmlFileLoader,
                                         TranslationService translationService,
                                         MenuBarFXMLController menuBarFXMLController,
                                         MenuDeckFXMLController menuDeckFXMLController,
                                         CardService cardService,
                                         DeckService deckService) {
        super(stage, fxmlFileLoader, "/fxml/deck/RevisionAddCardView.fxml", translationService);
        this.cardService = cardService;
        this.deckService = deckService;
        this.menuBarFXMLController = menuBarFXMLController;
        this.menuDeckFXMLController = menuDeckFXMLController;
    }

    /**
     * Initializes the view with parameters. Sets up the deck for adding cards and initializes the menu components.
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

        addButton.setOnAction(e -> handleAddCard());
        importButton.setOnAction(e -> handleImportCards());

        initializeIcons();
    }

    /**
     * Handles adding a new card to the deck after validating the inputs.
     */
    private void handleAddCard() {
        String frontText = frontTextField.getText().trim();
        String backText = backTextField.getText().trim();

        if (frontText.isEmpty() || backText.isEmpty()) {
            showWarningAlert(translationService.getTranslation("deck_view.add_card.empty_text"), translationService,stage);
            return;
        }

        Optional<CardBase> existingCard = cardService.findCardByFrontAndDeck(frontText, deck);
        if (existingCard.isPresent()) {
            showWarningAlert(translationService.getTranslation("deck_view.add_card.front_unique"), translationService, stage);
            return;
        }

        cardService.addNewCard(frontText, backText, deck);
        showInfoAlert(translationService.getTranslation("deck_view.add_card.add_success"), translationService, stage);

        frontTextField.clear();
        backTextField.clear();
    }

    /**
     * Handles importing cards from a file, adding them to the deck.
     */
    private void handleImportCards() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(stage.getStage());
        if (selectedFile != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                long cardsNumber = Long.parseLong(br.readLine());
                for (int i = 0; i < cardsNumber; i++) {
                    CardBase card = deck.getRevisionAlgorithm().createNewCard(br.readLine(), br.readLine());
                    deckService.addNewCardToDeck(card, deck);
                }
                showInfoAlert(translationService.getTranslation("new_deck_view.deck_imported"), translationService, stage);
            } catch (Exception ex) {
                showErrorAlert(translationService.getTranslation("new_deck_view.import_error"), translationService, stage);
            }
        }
    }

    /**
     * Updates the size of the view components based on the window size and scale factor.
     */
    @Override
    public void updateSize() {
        menuBarFXMLController.updateSize();
        menuDeckFXMLController.updateSize();

        double scaleFactor = stage.getStageScaleFactor();
        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";
        String radiusStyle = "-fx-background-radius: " + (20 * scaleFactor) + "; -fx-border-radius: " + (20 * scaleFactor) + ";";

        frontLabel.setStyle(fontSizeStyle);
        backLabel.setStyle(fontSizeStyle);
        frontTextField.setStyle(fontSizeStyle + radiusStyle);
        backTextField.setStyle(fontSizeStyle + radiusStyle);
        addButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;" + radiusStyle);
        importButton.setStyle(fontSizeStyle + " -fx-background-color: blue; -fx-text-fill: white;" + radiusStyle);

        frontLabel.setPrefSize(200 * scaleFactor,40 * scaleFactor);
        backLabel.setPrefSize(200 * scaleFactor,40 * scaleFactor);
        frontTextField.setPrefSize(200 * scaleFactor,40 * scaleFactor);
        backTextField.setPrefSize(200 * scaleFactor,40 * scaleFactor);
        addButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
        importButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);

        updateIcons(scaleFactor);
    }

    /**
     * Updates the text labels and button texts based on the current language settings.
     */
    @Override
    public void updateTranslation() {
        menuBarFXMLController.updateTranslation();
        menuDeckFXMLController.updateTranslation();

        frontLabel.setText(translationService.getTranslation("deck_view.add_card.front"));
        backLabel.setText(translationService.getTranslation("deck_view.add_card.back"));
        addButton.setText(translationService.getTranslation("deck_view.add_card.add"));
        importButton.setText(translationService.getTranslation("deck_view.add_card.import"));
    }

    /**
     * Updates the view components by delegating to the menu controllers to ensure all UI elements are up to date.
     */
    @Override
    public void updateView(){
        menuBarFXMLController.updateView();
        menuDeckFXMLController.updateView();
    }

    /**
     * Initializes the icons used in the controller's UI components.
     */
    private void initializeIcons() {
        addButtonIcon = ImageUtil.createImageView("/images/icons/new.png");
        addButton.setGraphic(addButtonIcon);
        importButtonIcon = ImageUtil.createImageView("/images/icons/import.png");
        importButton.setGraphic(importButtonIcon);
    }

    /**
     * Updates the size of each icon according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon.
     */
    private void updateIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(addButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(importButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
    }
}
