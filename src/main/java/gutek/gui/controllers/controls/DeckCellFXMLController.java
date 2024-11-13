package gutek.gui.controllers.controls;

import gutek.domain.revisions.AvailableRevisions;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.gui.controllers.main.DecksFXMLController;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import gutek.utils.CsvUtil;
import gutek.utils.FXMLFileLoader;
import gutek.utils.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static gutek.utils.AlertMessageUtil.showErrorAlert;
import static gutek.utils.AlertMessageUtil.showInfoAlert;

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
     * Container for dynamically generated statistics labels based on revision types.
     * This container holds labels that display statistics for the deck, where labels
     * are added based on the revision types supported by the deck's algorithm.
     */
    @FXML
    private VBox dynamicStatsContainer;

    /** Map holding labels for the names of different revision statistics. */
    private final Map<Class<?>, Label> statNameLabels = new HashMap<>();

    /** Map holding labels for the counts of different revision statistics. */
    private final Map<Class<?>, Label> statCountLabels = new HashMap<>();

    /**
     * Button to delete the deck.
     */
    @FXML
    private Button buttonDelete;

    /**
     * Icon for the "buttonDelete".
     */
    private ImageView buttonDeleteIcon;

    /**
     * Button to open the deck.
     */
    @FXML
    private Button buttonOpen;

    /**
     * Icon for the "buttonOpen".
     */
    private ImageView buttonOpenIcon;

    /**
     * Button to export the deck data to a file.
     */
    @FXML
    private Button buttonExport;

    /**
     * Icon for the "buttonExport".
     */
    private ImageView buttonExportIcon;

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
        parentController.getScaleFactorProperty().addListener((obs, oldVal, newVal) -> updateSize());
        parentController.getCurrentLocaleProperty().addListener((obs, oldVal, newVal) -> updateTranslation());

        buttonDelete.setOnAction(e -> handleDelete());
        buttonOpen.setOnAction(e -> handleOpen());
        buttonExport.setOnAction(e -> handleExport());

        initializeIcons();
    }

    /**
     * Sets the deck to be managed by this controller and updates the view, size, and translations accordingly.
     *
     * @param deck The deck to display and manage.
     */
    public void setDeck(DeckBase deck) {
        this.deck = deck;
        this.deck.getRevisionAlgorithm().setTranslationService(translationService);

        dynamicStatsContainer.getChildren().clear();
        statNameLabels.clear();
        statCountLabels.clear();

        for (Map.Entry<Class<?>, AvailableRevisions.RevisionInfo> entry : AvailableRevisions.getAVAILABLE_REVISIONS().entrySet()) {
            Class<?> revisionInterface = entry.getKey();
            if (revisionInterface.isInstance(deck.getRevisionAlgorithm())) {
                addStatisticHBox(revisionInterface);
            }
        }

        updateView();
        updateSize();
        updateTranslation();
    }

    /**
     * Adds a horizontal box (HBox) containing the statistics labels for a given revision type.
     *
     * @param revisionInterface The revision type for which statistics are added.
     */
    private void addStatisticHBox(Class<?> revisionInterface) {
        Label nameLabel = new Label();
        statNameLabels.put(revisionInterface, nameLabel);
        Label countLabel = new Label();
        statCountLabels.put(revisionInterface, countLabel);

        HBox statBox = new HBox();
        statBox.setSpacing(10);
        statBox.getChildren().addAll(nameLabel, countLabel);

        dynamicStatsContainer.getChildren().add(statBox);
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
     * Handles exporting the deck data to a csv file.
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

            try {
                CsvUtil.writeToCsv(file, deckService.getAllCards(deck));
                showInfoAlert(translationService.getTranslation("decks_view.export_success"), translationService, stage);
            } catch (IOException ex) {
                showErrorAlert(translationService.getTranslation("decks_view.export_fail"), translationService, stage);
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

        for (Map.Entry<Class<?>, Label> entry : statNameLabels.entrySet()) {
            Class<?> revisionInterface = entry.getKey();
            Label nameLabel = entry.getValue();

            String translationKeySuffix = AvailableRevisions.getAVAILABLE_REVISIONS().get(revisionInterface).translationKey();
            if (translationKeySuffix != null) {
                String translationKey = "revision." + translationKeySuffix + ".cards_number";
                nameLabel.setText(translationService.getTranslation(translationKey));
            }
        }

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
        String buttonRadiusStyle = "-fx-background-radius: " + (15 * scaleFactor) + "; -fx-border-radius: " + (15 * scaleFactor) + ";";

        double scaledWidth = 110 * scaleFactor;
        double scaleWidthButtons = 200 * scaleFactor;
        double scaledHeight = 30 * scaleFactor;
        double scaledHeightLabels = 60 * scaleFactor;

        deckNameLabel.setStyle(fontSizeStyle);
        deckName.setStyle(fontSizeStyle);
        newCardsNumberLabel.setStyle(fontSizeStyle);
        newCardsNumber.setStyle(fontSizeStyle);
        allCardsNumberLabel.setStyle(fontSizeStyle);
        allCardsNumber.setStyle(fontSizeStyle);
        revisionAlgorithmLabel.setStyle(fontSizeAlgorithmStyle);
        revisionAlgorithm.setStyle(fontSizeAlgorithmStyle);
        buttonOpen.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;" + buttonRadiusStyle);
        buttonDelete.setStyle(fontSizeStyle + " -fx-background-color: red; -fx-text-fill: white;" + buttonRadiusStyle);
        buttonExport.setStyle(fontSizeStyle + " -fx-background-color: blue; -fx-text-fill: white;" + buttonRadiusStyle);

        deckNameLabel.setPrefSize(scaledWidth,scaledHeightLabels);
        deckName.setPrefSize(scaledWidth,scaledHeight);
        newCardsNumberLabel.setPrefSize(scaledWidth,scaledHeightLabels);
        newCardsNumber.setPrefSize(scaledWidth,scaledHeight);
        allCardsNumberLabel.setPrefSize(scaledWidth,scaledHeightLabels);
        allCardsNumber.setPrefSize(scaledWidth,scaledHeight);
        revisionAlgorithmLabel.setPrefSize(scaledWidth * 2,scaledHeightLabels);
        revisionAlgorithm.setPrefSize(scaledWidth * 3,scaledHeight);
        buttonOpen.setPrefSize(scaleWidthButtons,scaledHeight);
        buttonDelete.setPrefSize(scaleWidthButtons,scaledHeight);
        buttonExport.setPrefSize(scaleWidthButtons,scaledHeight);

        for (Label nameLabel : statNameLabels.values()) {
            nameLabel.setStyle(fontSizeStyle + "-fx-alignment: center-right;");
            nameLabel.setPrefSize(scaledWidth * 2, scaledHeight);
        }

        for (Label countLabel : statCountLabels.values()) {
            countLabel.setStyle(fontSizeStyle + "-fx-alignment: center-left;");
            countLabel.setPrefSize(scaledWidth, scaledHeight);
        }

        updateIcons(scaleFactor);
    }

    /**
     * Updates the deck information displayed in this cell, including the name, counts of regular,
     * reverse, new, and total cards. This method ensures that the latest deck data is visible
     * to the user.
     */
    @Override
    public void updateView() {
        deckName.setText(deck.getName());
        newCardsNumber.setText(String.valueOf(deckService.getNewCardsCount(deck)));
        allCardsNumber.setText(String.valueOf(deckService.getAllCardsCount(deck)));

        for (Map.Entry<Class<?>, Label> entry : statCountLabels.entrySet()) {
            Class<?> revisionInterface = entry.getKey();
            Label countLabel = entry.getValue();

            AvailableRevisions.RevisionInfo revisionInfo = AvailableRevisions.getAVAILABLE_REVISIONS().get(revisionInterface);
            int updatedCount = revisionInfo.countRevisionCardsFunction().apply(deckService, deck);
            countLabel.setText(String.valueOf(updatedCount));
            countLabel.setTextFill(revisionInfo.color());
        }

        for (Map.Entry<Class<?>, Label> entry : statNameLabels.entrySet()) {
            Class<?> revisionInterface = entry.getKey();
            Label nameLabel = entry.getValue();

            AvailableRevisions.RevisionInfo revisionInfo = AvailableRevisions.getAVAILABLE_REVISIONS().get(revisionInterface);
            nameLabel.setTextFill(revisionInfo.color());
        }
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
        buttonDeleteIcon = ImageUtil.createImageView("/images/icons/move.png");
        buttonDelete.setGraphic(buttonDeleteIcon);
        buttonOpenIcon = ImageUtil.createImageView("/images/icons/open.png");
        buttonOpen.setGraphic(buttonOpenIcon);
        buttonExportIcon = ImageUtil.createImageView("/images/icons/export.png");
        buttonExport.setGraphic(buttonExportIcon);
    }

    /**
     * Updates the size of each icon according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon.
     */
    private void updateIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(buttonDeleteIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(buttonOpenIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(buttonExportIcon, 20 * scaleFactor, 20 * scaleFactor);
    }
}
