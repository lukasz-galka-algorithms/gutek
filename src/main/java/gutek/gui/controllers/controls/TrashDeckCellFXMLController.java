package gutek.gui.controllers.controls;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.main.TrashFXMLController;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * A controller class for managing the display and interactions of a single deck in the trash view.
 * This controller allows users to restore or permanently delete a deck that has been moved to the trash.
 * <p>
 * The panel displays the deck's name and includes buttons for restoring or permanently deleting the deck.
 * This controller interacts with the trash view to update the list of deleted decks accordingly.
 */
public class TrashDeckCellFXMLController extends FXMLController {

    /**
     * Label for the deck name header.
     */
    @FXML
    private Label deckNameLabel;

    /**
     * Label to display the deck name.
     */
    @FXML
    private Label deckName;

    /**
     * Button to delete the deck.
     */
    @FXML
    private Button buttonDelete;

    /**
     * Icon for the "buttonDelete" menu item.
     */
    private ImageView buttonDeleteIcon;

    /**
     * Button to restore the deck.
     */
    @FXML
    private Button buttonRestore;

    /**
     * Icon for the "buttonRestore" menu item.
     */
    private ImageView buttonRestoreIcon;

    /**
     * The deck associated with this controller, representing the data of a single trashed deck.
     */
    private DeckBase deck;

    /**
     * Service for managing deck-related operations, such as restoration and permanent deletion.
     */
    private final DeckService deckService;

    /**
     * Reference to the parent controller for managing the trash view.
     */
    private final TrashFXMLController parentController;

    /**
     * Constructs a new `TrashDeckCellFXMLController` with the specified dependencies.
     *
     * @param stage               The main application stage for managing scenes.
     * @param fxmlFileLoader      Utility for loading FXML files associated with this controller.
     * @param translationService  Service for handling translations within this controller.
     * @param deckService         Service for managing deck operations.
     * @param parentController    Parent controller for managing the trash view.
     */
    public TrashDeckCellFXMLController(MainStage stage,
                                       FXMLFileLoader fxmlFileLoader,
                                       TranslationService translationService,
                                       DeckService deckService,
                                       TrashFXMLController parentController) {
        super(stage, fxmlFileLoader, "/fxml/controls/TrashDeckCellView.fxml", translationService);
        this.deckService = deckService;
        this.parentController = parentController;
    }

    /**
     * Initializes the controller, setting up listeners for scaling and localization changes,
     * and binding actions to the restore and delete buttons.
     */
    @FXML
    public void initialize() {
        parentController.getScaleFactorProperty().addListener((obs, oldVal, newVal) -> updateSize());
        parentController.getCurrentLocaleProperty().addListener((obs, oldVal, newVal) -> updateTranslation());

        buttonDelete.setOnAction(e -> handleDelete());
        buttonRestore.setOnAction(e -> handleRestore());

        initializeIcons();
    }

    /**
     * Sets the deck to be displayed in this cell and updates the view, size, and translations accordingly.
     *
     * @param deck The deck to display and manage.
     */
    public void setDeck(DeckBase deck) {
        this.deck = deck;
        this.deck.getRevisionAlgorithm().setTranslationService(translationService);
        updateView();
        updateSize();
        updateTranslation();
    }

    /**
     * Restores the deck from the trash back to the main deck view.
     * Updates the trash view after the deck is restored.
     */
    private void handleRestore() {
        deckService.restoreDeck(deck);
        parentController.removeDeckFromListView(deck);
    }

    /**
     * Permanently deletes the deck from the system.
     * Updates the trash view after the deck is deleted.
     */
    private void handleDelete() {
        deckService.removeDeck(deck);
        parentController.removeDeckFromListView(deck);
    }

    /**
     * Updates the text of the labels and buttons based on the current language settings using the translation service.
     */
    @Override
    public void updateTranslation() {
        deckNameLabel.setText(translationService.getTranslation("trash_decks_view.deck_name"));
        buttonRestore.setText(translationService.getTranslation("trash_decks_view.restore_button"));
        buttonDelete.setText(translationService.getTranslation("trash_decks_view.delete_button"));
    }

    /**
     * Adjusts the size of the UI elements according to the current scaling factor, adapting font sizes and component dimensions.
     */
    @Override
    public void updateSize() {
        double scaleFactor = stage.getStageScaleFactor();

        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";
        String buttonRadiusStyle = "-fx-background-radius: " + (15 * scaleFactor) + "; -fx-border-radius: " + (15 * scaleFactor) + ";";

        deckNameLabel.setStyle(fontSizeStyle);
        deckName.setStyle(fontSizeStyle);
        buttonDelete.setStyle(fontSizeStyle + " -fx-background-color: red; -fx-text-fill: white;" + buttonRadiusStyle);
        buttonRestore.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;" + buttonRadiusStyle);

        deckNameLabel.setPrefSize(150 * scaleFactor, 20 * scaleFactor);
        deckName.setPrefSize(150 * scaleFactor, 20 * scaleFactor);
        buttonDelete.setPrefSize(150 * scaleFactor, 20 * scaleFactor);
        buttonRestore.setPrefSize(150 * scaleFactor, 20 * scaleFactor);

        updateIcons(scaleFactor);
    }

    /**
     * Updates the view by setting the deck name label to reflect the name of the deck.
     */
    @Override
    public void updateView() {
        deckName.setText(deck.getName());
    }

    /**
     * Loads the view from the specified FXML file if it has not been loaded already.
     */
    @Override
    public void loadViewFromFXML() {
        if (this.root == null) {
            this.root = fxmlFileLoader.loadFXML(fxmlFilePath, this);
        }
    }

    /**
     * Initializes the icons used in the controller's UI components.
     */
    private void initializeIcons() {
        buttonDeleteIcon = ImageUtil.createImageView("/images/icons/delete.png");
        buttonDelete.setGraphic(buttonDeleteIcon);
        buttonRestoreIcon = ImageUtil.createImageView("/images/icons/restore.png");
        buttonRestore.setGraphic(buttonRestoreIcon);
    }

    /**
     * Updates the size of each icon according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon.
     */
    private void updateIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(buttonDeleteIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(buttonRestoreIcon, 20 * scaleFactor, 20 * scaleFactor);
    }
}
