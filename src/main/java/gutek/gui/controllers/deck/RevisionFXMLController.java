package gutek.gui.controllers.deck;

import gutek.entities.decks.DeckBase;
import gutek.entities.decks.DeckBaseStatistics;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.DeckService;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.gui.controls.NumberTextField;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Controller class for managing the card revision view in the application.
 * <p>
 * This view enables users to initiate regular or reverse revisions of a deck,
 * and allows setting the daily number of new cards to review. The interface displays
 * statistics on new and old cards available for revision in each mode.
 */
@Component
public class RevisionFXMLController extends FXMLController {

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
     * Label for the "new cards per day" input field.
     */
    @FXML
    private Label newCardsPerDayLabel;

    /**
     * Text field to set the number of new cards to review per day.
     */
    @FXML
    private NumberTextField newCardsPerDayTextField;

    /**
     * Button to start a regular revision session.
     */
    @FXML
    private Button regularRevisionButton;

    /**
     * Button to start a reverse revision session.
     */
    @FXML
    private Button reverseRevisionButton;

    /**
     * Label displaying the count of new cards available for regular revision.
     */
    @FXML
    private Label regularNewCardsLabel;

    /**
     * Label displaying the count of old cards available for regular revision.
     */
    @FXML
    private Label regularOldCardsLabel;

    /**
     * Label displaying the count of new cards available for reverse revision.
     */
    @FXML
    private Label reverseNewCardsLabel;

    /**
     * Label displaying the count of old cards available for reverse revision.
     */
    @FXML
    private Label reverseOldCardsLabel;

    /**
     * Listener for changes in the "new cards per day" field to update the value in the deck statistics.
     */
    private final ChangeListener<String> newCardsPerDayListener;

    /**
     * Controller for managing the menu bar.
     */
    private final MenuBarFXMLController menuBarFXMLController;

    /**
     * Controller for managing deck-specific menu actions.
     */
    private final MenuDeckFXMLController menuDeckFXMLController;

    /**
     * Service for managing deck statistics.
     */
    private final DeckStatisticsService deckStatisticsService;

    /**
     * Service for managing deck-related operations.
     */
    private final DeckService deckService;

    /**
     * The deck being revised.
     */
    private DeckBase deck;

    /**
     * Constructs a new `RevisionFXMLController` for managing card revisions in a deck.
     *
     * @param stage               The main application stage.
     * @param fxmlFileLoader      Utility for loading FXML files.
     * @param translationService  Service for handling translations within this view.
     * @param menuBarFXMLController Controller for the main menu bar.
     * @param menuDeckFXMLController Controller for deck-specific menu actions.
     * @param deckService         Service for managing decks.
     * @param deckStatisticsService Service for managing deck statistics.
     */
    protected RevisionFXMLController(MainStage stage,
                                     FXMLFileLoader fxmlFileLoader,
                                     TranslationService translationService,
                                     MenuBarFXMLController menuBarFXMLController,
                                     MenuDeckFXMLController menuDeckFXMLController,
                                     DeckService deckService,
                                     DeckStatisticsService deckStatisticsService) {
        super(stage, fxmlFileLoader, "/fxml/deck/RevisionView.fxml", translationService);
        this.menuBarFXMLController = menuBarFXMLController;
        this.menuDeckFXMLController = menuDeckFXMLController;
        this.deckService = deckService;
        this.deckStatisticsService = deckStatisticsService;

        this.newCardsPerDayListener = (observable, oldValue, newValue) -> updateNewCardsPerDay();
    }

    /**
     * Handles initiating a regular revision session.
     * Switches the scene to the regular revision view.
     */
    private void handleRegularRevision() {
        stage.setScene(MainStageScenes.REVISION_REGULAR_SCENE, deck);
    }

    /**
     * Handles initiating a reverse revision session.
     * Switches the scene to the reverse revision view.
     */
    private void handleReverseRevision() {
        stage.setScene(MainStageScenes.REVISION_REVERSE_SCENE, deck);
    }

    /**
     * Initializes the view with the provided deck parameter.
     * Configures menu components, sets up button actions, and binds the new cards per day listener.
     *
     * @param params Array of parameters, where the first element is expected to be a `DeckBase` instance.
     */
    @Override
    public void initWithParams(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof DeckBase deckBase) {
            this.deck = deckBase;
            if (deck.getDeckBaseStatistics() != null) {
                newCardsPerDayTextField.setText(deck.getDeckBaseStatistics().getNewCardsPerDay().toString());
            }
            this.menuDeckFXMLController.initWithParams(this.deck);
        }
        menuBarFXMLController.initWithParams();

        menuContainer.getChildren().setAll(menuBarFXMLController.getRoot(), menuDeckFXMLController.getRoot());

        regularRevisionButton.setOnAction(e -> handleRegularRevision());
        reverseRevisionButton.setOnAction(e -> handleReverseRevision());

        newCardsPerDayTextField.textProperty().removeListener(newCardsPerDayListener);
        newCardsPerDayTextField.textProperty().addListener(newCardsPerDayListener);
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
        double labelWidth = 150 * scaleFactor;
        double labelHeight = 30 * scaleFactor;

        newCardsPerDayLabel.setStyle(fontSizeStyle);
        regularNewCardsLabel.setStyle(fontSizeStyle);
        regularOldCardsLabel.setStyle(fontSizeStyle);
        reverseNewCardsLabel.setStyle(fontSizeStyle);
        reverseOldCardsLabel.setStyle(fontSizeStyle);
        newCardsPerDayTextField.setStyle(fontSizeStyle);
        regularRevisionButton.setStyle(fontSizeStyle);
        reverseRevisionButton.setStyle(fontSizeStyle);

        newCardsPerDayTextField.setPrefSize(150 * scaleFactor, 30 * scaleFactor);
        regularRevisionButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
        reverseRevisionButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
        newCardsPerDayLabel.setPrefSize(labelWidth, labelHeight);
        regularNewCardsLabel.setPrefSize(labelWidth, labelHeight);
        regularOldCardsLabel.setPrefSize(labelWidth, labelHeight);
        reverseNewCardsLabel.setPrefSize(labelWidth, labelHeight);
        reverseOldCardsLabel.setPrefSize(labelWidth, labelHeight);
    }

    /**
     * Updates the text labels and button texts based on the current language settings.
     * Also updates card revision statistics displayed in the view.
     */
    @Override
    public void updateTranslation() {
        menuBarFXMLController.updateTranslation();
        menuDeckFXMLController.updateTranslation();

        newCardsPerDayLabel.setText(translationService.getTranslation("deck_view.revise.new_cards_per_day"));
        regularNewCardsLabel.setText(translationService.getTranslation("deck_view.revise.new_cards_regular") + ": " +
                deckStatisticsService.getNewCardsForToday(deck.getDeckBaseStatistics().getIdDeckStatistics()));
        regularOldCardsLabel.setText(translationService.getTranslation("deck_view.revise.old_cards_regular") + ": " +
                deckService.getRegularRevisionCards(deck).size());
        regularRevisionButton.setText(translationService.getTranslation("deck_view.revise.regular_button"));

        reverseNewCardsLabel.setText(translationService.getTranslation("deck_view.revise.new_cards_reverse") + ": " +
                deckStatisticsService.getNewCardsForToday(deck.getDeckBaseStatistics().getIdDeckStatistics()));
        reverseOldCardsLabel.setText(translationService.getTranslation("deck_view.revise.old_cards_reverse") + ": " +
                deckService.getReverseRevisionCards(deck).size());
        reverseRevisionButton.setText(translationService.getTranslation("deck_view.revise.reverse_button"));
    }

    /**
     * Updates the number of new cards per day for the current deck.
     * Saves the new value to the deck's statistics.
     */
    private void updateNewCardsPerDay() {
        try {
            int newCardsPerDay = Integer.parseInt(newCardsPerDayTextField.getText());
            Optional<DeckBaseStatistics> statOpt = deckStatisticsService.loadDeckStatistics(deck.getDeckBaseStatistics().getIdDeckStatistics());
            if(statOpt.isPresent()){
                DeckBaseStatistics stat = statOpt.get();
                stat.setNewCardsPerDay(newCardsPerDay);
                deckStatisticsService.saveDeckStatistics(stat);
            }
            regularNewCardsLabel.setText(translationService.getTranslation("deck_view.revise.new_cards_regular") + ": " +
                    deckStatisticsService.getNewCardsForToday(deck.getDeckBaseStatistics().getIdDeckStatistics()));
            reverseNewCardsLabel.setText(translationService.getTranslation("deck_view.revise.new_cards_reverse") + ": " +
                    deckStatisticsService.getNewCardsForToday(deck.getDeckBaseStatistics().getIdDeckStatistics()));
        } catch (NumberFormatException ignored) {
            // ignore
        }
    }
}
