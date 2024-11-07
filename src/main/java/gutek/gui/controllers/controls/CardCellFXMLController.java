package gutek.gui.controllers.controls;

import gutek.entities.cards.CardBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.gui.controllers.deck.RevisionSearchFXMLController;
import gutek.services.CardService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import static gutek.utils.AlertMessageUtil.showInfoAlert;

/**
 * A controller for managing the display and actions of a card within a deck, providing options to edit or delete the card.
 * This controller handles the UI elements associated with a single card, such as displaying its front and back text.
 * It includes buttons for editing and deleting the card, which trigger respective actions.
 * <p>
 * The panel dynamically updates its content based on the current language settings and responds to resizing events.
 */
public class CardCellFXMLController extends FXMLController {

    /**
     * Label for the card front.
     */
    @FXML
    private Label frontLabel;

    /**
     * Label for the card back.
     */
    @FXML
    private Label backLabel;

    /**
     * Button for the card editing.
     */
    @FXML
    private Button editButton;

    /**
     * Icon for the "editButton".
     */
    private ImageView editButtonIcon;

    /**
     * Button for the card deleting.
     */
    @FXML
    private Button deleteButton;

    /**
     * Icon for the "deleteButton".
     */
    private ImageView deleteButtonIcon;

    /**
     * The card associated with this cell.
     */
    private CardBase card;

    /**
     * Service for handling card operations such as deletion.
     */
    private final CardService cardService;

    /**
     * The parent controller of the card cell.
     */
    private final RevisionSearchFXMLController parentController;

    /**
     * Constructs a new `CardCellFXMLController` object with the specified dependencies.
     *
     * @param stage               the main application stage where the scene is displayed
     * @param fxmlFileLoader      utility for loading the FXML file associated with this scene
     * @param translationService  the service responsible for handling translations within this scene
     * @param cardService  the service responsible for handling card operations
     * @param parentController  the parent controller for the card cell
     */
    public CardCellFXMLController(MainStage stage,
                                  FXMLFileLoader fxmlFileLoader,
                                  TranslationService translationService,
                                  CardService cardService,
                                  RevisionSearchFXMLController parentController) {
        super(stage, fxmlFileLoader, "/fxml/controls/CardCellView.fxml", translationService);
        this.cardService = cardService;
        this.parentController = parentController;
    }

    /**
     * Initializes the controller after the FXML file has been loaded.
     * Adds listeners for scaling and localization changes, and sets up actions for the edit and delete buttons.
     */
    @FXML
    public void initialize() {
        parentController.getScaleFactorProperty().addListener((obs, oldVal, newVal) -> updateSize());
        parentController.getCurrentLocaleProperty().addListener((obs, oldVal, newVal) -> updateTranslation());

        editButton.setOnAction(e -> handleEdit());
        deleteButton.setOnAction(e -> handleDelete());

        initializeIcons();
    }

    /**
     * Sets the card to be displayed in this cell and updates the view accordingly.
     *
     * @param card The card to be displayed.
     */
    public void setCard(CardBase card) {
        this.card = card;
        updateView();
        updateSize();
        updateTranslation();
    }

    /**
     * Handles the deletion of the card, asking for confirmation before proceeding.
     */
    private void handleDelete() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle(translationService.getTranslation("deck_view.search_card.delete_title"));
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText(translationService.getTranslation("deck_view.search_card.delete_confirm"));

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cardService.removeCard(card);
                showInfoAlert(translationService.getTranslation("deck_view.search_card.delete_success"), translationService, stage);
                parentController.removeCardFromListView(card);
            }
        });
    }

    /**
     * Opens the edit view for the card.
     */
    private void handleEdit() {
        stage.setScene(MainStageScenes.REVISION_EDIT_CARD_SCENE, card);
    }

    /**
     * Updates the visual scale of the card cell based on the application's current scaling factor.
     * Adjusts font sizes and component sizes dynamically.
     */
    @Override
    public void updateSize() {
        double scaleFactor = stage.getStageScaleFactor();

        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";
        String buttonRadiusStyle = "-fx-background-radius: " + (15 * scaleFactor) + "; -fx-border-radius: " + (15 * scaleFactor) + ";";

        frontLabel.setStyle(fontSizeStyle);
        backLabel.setStyle(fontSizeStyle);
        editButton.setStyle(fontSizeStyle + " -fx-background-color: blue; -fx-text-fill: white;" + buttonRadiusStyle);
        deleteButton.setStyle(fontSizeStyle + " -fx-background-color: red; -fx-text-fill: white;" + buttonRadiusStyle);

        frontLabel.setPrefSize(150 * scaleFactor, 20 * scaleFactor);
        backLabel.setPrefSize(150 * scaleFactor, 20 * scaleFactor);
        editButton.setPrefSize(150 * scaleFactor, 20 * scaleFactor);
        deleteButton.setPrefSize(150 * scaleFactor, 20 * scaleFactor);

        updateIcons(scaleFactor);
    }

    /**
     * Updates the labels and button text based on the current language using the translation service.
     */
    @Override
    public void updateTranslation() {
        frontLabel.setText(translationService.getTranslation("deck_view.search_card.front") + ": " + card.getFront());
        backLabel.setText(translationService.getTranslation("deck_view.search_card.back") + ": " + card.getBack());
        editButton.setText(translationService.getTranslation("deck_view.search_card.edit"));
        deleteButton.setText(translationService.getTranslation("deck_view.search_card.delete"));
    }

    /**
     * Updates the view by refreshing the content displayed on the labels,
     * particularly useful for refreshing the card's front and back text.
     */
    @Override
    public void updateView() {
        frontLabel.setText(translationService.getTranslation("deck_view.search_card.front") + ": " + card.getFront());
        backLabel.setText(translationService.getTranslation("deck_view.search_card.back") + ": " + card.getBack());
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
        editButtonIcon = ImageUtil.createImageView("/images/icons/edit.png");
        editButton.setGraphic(editButtonIcon);
        deleteButtonIcon = ImageUtil.createImageView("/images/icons/delete.png");
        deleteButton.setGraphic(deleteButtonIcon);
    }

    /**
     * Updates the size of each icon according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon.
     */
    private void updateIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(editButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(deleteButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
    }
}