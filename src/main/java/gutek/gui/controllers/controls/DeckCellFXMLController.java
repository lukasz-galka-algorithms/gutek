package gutek.gui.controllers.controls;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.gui.controllers.main.DecksFXMLController;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import static gutek.utils.AlertMessageUtil.showAlert;

/**
 * A controller class for managing the display and interactions of a single deck within the user interface.
 * This controller provides functionalities for viewing deck details and performing actions on the deck,
 * such as opening it for revision, deleting it, and exporting its contents to a file.
 * <p>
 * The panel displays deck statistics including the number of regular, reverse, new, and total cards,
 * as well as the name of the revision algorithm.
 * Users can interact with the deck through buttons to open, delete, or export it.
 */
public class DeckCellFXMLController extends FXMLController {
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
     * Label for the number of regular revision cards header.
     */
    @FXML
    private Label regularRevisionCardsNumberLabel;

    /**
     * Label to display the number of regular revision cards.
     */
    @FXML
    private Label regularRevisionCardsNumber;

    /**
     * Label for the number of reverse revision cards header.
     */
    @FXML
    private Label reverseRevisionCardsNumberLabel;

    /**
     * Label to display the number of reverse revision cards.
     */
    @FXML
    private Label reverseRevisionCardsNumber;

    /**
     * Label for the number of new cards header.
     */
    @FXML
    private Label newCardsNumberLabel;

    /**
     * Label to display the number of new cards.
     */
    @FXML
    private Label newCardsNumber;

    /**
     * Label for the total number of cards header.
     */
    @FXML
    private Label allCardsNumberLabel;

    /**
     * Label to display the total number of cards.
     */
    @FXML
    private Label allCardsNumber;

    /**
     * Label for the revision algorithm header.
     */
    @FXML
    private Label revisionAlgorithmLabel;

    /**
     * Label to display the name of the revision algorithm used for the deck.
     */
    @FXML
    private Label revisionAlgorithm;

    /**
     * Button to delete the deck.
     */
    @FXML
    private Button buttonDelete;

    /**
     * Button to open the deck.
     */
    @FXML
    private Button buttonOpen;

    /**
     * Button to export the deck data to a file.
     */
    @FXML
    private Button buttonExport;

    /**
     * The deck associated with this controller.
     */
    private DeckBase deck;

    /**
     * Service for managing deck-related operations.
     */
    private final DeckService deckService;

    /**
     * Reference to the parent controller for managing the list of decks.
     */
    private final DecksFXMLController parentController;

    /**
     * Constructs a `DeckCellFXMLController` with the specified dependencies.
     *
     * @param stage               The main application stage for managing scenes.
     * @param fxmlFileLoader      Utility for loading FXML files.
     * @param translationService  Service for handling translations within this controller.
     * @param deckService         Service for managing deck operations.
     * @param parentController    Parent controller for managing the list of decks.
     */
    public DeckCellFXMLController(MainStage stage,
                                  FXMLFileLoader fxmlFileLoader,
                                  TranslationService translationService,
                                  DeckService deckService,
                                  DecksFXMLController parentController) {
        super(stage, fxmlFileLoader, "/fxml/controls/DeckCellView.fxml", translationService);
        this.deckService = deckService;
        this.parentController = parentController;
    }

    /**
     * Initializes the controller, setting up listeners for scaling and localization changes,
     * and binding actions to the open, delete, and export buttons.
     */
    @FXML
    public void initialize() {
        parentController.getScaleFactorProperty().addListener((obs, oldVal, newVal) -> {
            updateSize();
        });
        parentController.getCurrentLocaleProperty().addListener((obs, oldVal, newVal) -> {
            updateTranslation();
        });

        buttonDelete.setOnAction(e -> handleDelete());
        buttonOpen.setOnAction(e -> handleOpen());
        buttonExport.setOnAction(e -> handleExport());
    }

    /**
     * Sets the deck to be managed by this controller and updates the view, size, and translations accordingly.
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
     * Handles deleting the current deck and refreshing the view.
     */
    private void handleDelete() {
        deckService.deleteDeck(deck);
        parentController.removeDeckFromListView(deck);
    }

    /**
     * Handles opening the deck for revision and sets the current view in the main stage.
     */
    private void handleOpen() {
        stage.setScene(MainStageScenes.REVISION_REVISE_SCENE, deck);
    }

    /**
     * Handles exporting the deck data to a file.
     */
    public void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(translationService.getTranslation("decks_view.export_title"));

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(stage.getStage());

        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                List<CardBase> allCards = deckService.getAllCards(deck);
                writer.write(String.valueOf(allCards.size()));
                for (CardBase card : allCards) {
                    writer.newLine();
                    writer.write(card.getFront());
                    writer.newLine();
                    writer.write(card.getBack());
                }
                showAlert(Alert.AlertType.INFORMATION, translationService.getTranslation("decks_view.export_success"));
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, translationService.getTranslation("decks_view.export_fail"));
            }
        }
    }

    /**
     * Updates the text labels and buttons according to the current language settings.
     */
    @Override
    public void updateTranslation() {
        revisionAlgorithm.setText(deck.getRevisionAlgorithm().getAlgorithmName());
        deckNameLabel.setText(translationService.getTranslation("decks_view.deck_name"));
        regularRevisionCardsNumberLabel.setText(translationService.getTranslation("decks_view.regular_cards_number"));
        reverseRevisionCardsNumberLabel.setText(translationService.getTranslation("decks_view.reverse_cards_number"));
        newCardsNumberLabel.setText(translationService.getTranslation("decks_view.new_cards_number"));
        allCardsNumberLabel.setText(translationService.getTranslation("decks_view.all_cards_number"));
        revisionAlgorithmLabel.setText(translationService.getTranslation("decks_view.revision_algorithm"));
        buttonDelete.setText(translationService.getTranslation("decks_view.delete_button"));
        buttonOpen.setText(translationService.getTranslation("decks_view.open_button"));
        buttonExport.setText(translationService.getTranslation("decks_view.export_button"));
    }

    /**
     * Adjusts the size of the UI elements according to the current scaling factor, adapting font sizes and component dimensions.
     */
    @Override
    public void updateSize() {
        double scaleFactor = stage.getStageScaleFactor();

        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";
        String fontSizeAlgorithmStyle = "-fx-font-size: " + (14 * scaleFactor) + "px;";

        double scaledWidth = 110 * scaleFactor;
        double scaleWidthButtons = 100 * scaleFactor;
        double scaledHeight = 30 * scaleFactor;
        double scaledHeightLabels = 60 * scaleFactor;

        deckNameLabel.setStyle(fontSizeStyle);
        deckName.setStyle(fontSizeStyle);
        regularRevisionCardsNumberLabel.setStyle(fontSizeStyle);
        regularRevisionCardsNumber.setStyle(fontSizeStyle);
        reverseRevisionCardsNumberLabel.setStyle(fontSizeStyle);
        reverseRevisionCardsNumber.setStyle(fontSizeStyle);
        newCardsNumberLabel.setStyle(fontSizeStyle);
        newCardsNumber.setStyle(fontSizeStyle);
        allCardsNumberLabel.setStyle(fontSizeStyle);
        allCardsNumber.setStyle(fontSizeStyle);
        revisionAlgorithmLabel.setStyle(fontSizeAlgorithmStyle);
        revisionAlgorithm.setStyle(fontSizeAlgorithmStyle);
        buttonOpen.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;");
        buttonDelete.setStyle(fontSizeStyle + " -fx-background-color: red; -fx-text-fill: white;");
        buttonExport.setStyle(fontSizeStyle + " -fx-background-color: blue; -fx-text-fill: white;");

        deckNameLabel.setPrefSize(scaledWidth,scaledHeightLabels);
        deckName.setPrefSize(scaledWidth,scaledHeight);
        regularRevisionCardsNumberLabel.setPrefSize(scaledWidth,scaledHeightLabels);
        regularRevisionCardsNumber.setPrefSize(scaledWidth,scaledHeight);
        reverseRevisionCardsNumberLabel.setPrefSize(scaledWidth,scaledHeightLabels);
        reverseRevisionCardsNumber.setPrefSize(scaledWidth,scaledHeight);
        newCardsNumberLabel.setPrefSize(scaledWidth,scaledHeightLabels);
        newCardsNumber.setPrefSize(scaledWidth,scaledHeight);
        allCardsNumberLabel.setPrefSize(scaledWidth,scaledHeightLabels);
        allCardsNumber.setPrefSize(scaledWidth,scaledHeight);
        revisionAlgorithmLabel.setPrefSize(scaledWidth * 2,scaledHeightLabels);
        revisionAlgorithm.setPrefSize(scaledWidth * 3,scaledHeight);
        buttonOpen.setPrefSize(scaleWidthButtons,scaledHeight);
        buttonDelete.setPrefSize(scaleWidthButtons,scaledHeight);
        buttonExport.setPrefSize(scaleWidthButtons,scaledHeight);
    }

    /**
     * Updates the deck information displayed in this cell, including the name, counts of regular,
     * reverse, new, and total cards. This method ensures that the latest deck data is visible
     * to the user.
     */
    @Override
    public void updateView() {
        deckName.setText(deck.getName());
        regularRevisionCardsNumber.setText(String.valueOf(deckService.getRegularRevisionCardsCount(deck)));
        reverseRevisionCardsNumber.setText(String.valueOf(deckService.getReverseRevisionCardsCount(deck)));
        newCardsNumber.setText(String.valueOf(deckService.getNewCardsCount(deck)));
        allCardsNumber.setText(String.valueOf(deckService.getAllCardsCount(deck)));
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
}
