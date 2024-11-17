package gutek.gui.controllers.deck;

import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.decks.DeckBase;
import gutek.entities.decks.DeckBaseStatistics;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.DeckService;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.gui.controls.NumberTextField;
import gutek.utils.ImageUtil;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
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
     * Scroll pane for displaying revision buttons.
     */
    @FXML
    private ScrollPane scrollPane;

    /**
     * Container for holding the revision buttons and statistics.
     */
    @FXML
    private VBox revisionButtonsContainer;

    /**
     * Map storing the buttons associated with each revision type.
     */
    private final Map<RevisionStrategy<?>, Button> revisionButtons = new HashMap<>();

    /**
     * Map storing the icons for each revision button.
     */
    private final Map<RevisionStrategy<?>, ImageView> revisionButtonIcons = new HashMap<>();

    /**
     * Map storing the labels for the names of new cards for each revision type.
     */
    private final Map<RevisionStrategy<?>, Label> newCardsNameLabels = new HashMap<>();

    /**
     * Map storing the labels for the counts of new cards for each revision type.
     */
    private final Map<RevisionStrategy<?>, Label> newCardsCountLabels = new HashMap<>();

    /**
     * Map storing the labels for the names of old cards for each revision type.
     */
    private final Map<RevisionStrategy<?>, Label> cardsNameLabels = new HashMap<>();

    /**
     * Map storing the labels for the counts of old cards for each revision type.
     */
    private final Map<RevisionStrategy<?>, Label> cardsCountLabels = new HashMap<>();

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
     * @param stage                  The main application stage.
     * @param fxmlFileLoader         Utility for loading FXML files.
     * @param translationService     Service for handling translations within this view.
     * @param menuBarFXMLController  Controller for the main menu bar.
     * @param menuDeckFXMLController Controller for deck-specific menu actions.
     * @param deckService            Service for managing decks.
     * @param deckStatisticsService  Service for managing deck statistics.
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
                Optional<DeckBaseStatistics> statistics = deckStatisticsService.loadDeckStatistics(deck.getDeckBaseStatistics().getIdDeckStatistics());
                statistics.ifPresent(deckBaseStatistics -> newCardsPerDayTextField.setText(String.valueOf(deckBaseStatistics.getNewCardsPerDay())));
            }
            this.menuDeckFXMLController.initWithParams(this.deck);
        }
        menuBarFXMLController.initWithParams();

        menuContainer.getChildren().setAll(menuBarFXMLController.getRoot(), menuDeckFXMLController.getRoot());
        newCardsPerDayTextField.textProperty().removeListener(newCardsPerDayListener);
        newCardsPerDayTextField.textProperty().addListener(newCardsPerDayListener);
        loadRevisions();
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
        String borderStyle = "-fx-border-color: gray; -fx-border-width: 2; " + radiusStyle + "-fx-padding: " + (10 * scaleFactor) + ";";
        double labelWidth = 50 * scaleFactor;
        double labelHeight = 30 * scaleFactor;

        newCardsPerDayLabel.setStyle(fontSizeStyle);
        newCardsPerDayTextField.setStyle(fontSizeStyle + radiusStyle);
        newCardsPerDayTextField.setPrefSize(150 * scaleFactor, 30 * scaleFactor);
        newCardsPerDayLabel.setPrefSize(labelWidth * 3, labelHeight);

        newCardsNameLabels.values().forEach(label -> {
            label.setStyle(fontSizeStyle);
            label.setPrefSize(labelWidth * 2, labelHeight);
        });

        newCardsCountLabels.values().forEach(label -> {
            label.setStyle(fontSizeStyle);
            label.setPrefSize(labelWidth, labelHeight);
        });

        cardsNameLabels.values().forEach(label -> {
            label.setStyle(fontSizeStyle);
            label.setPrefSize(labelWidth * 2, labelHeight);
        });

        cardsCountLabels.values().forEach(label -> {
            label.setStyle(fontSizeStyle);
            label.setPrefSize(labelWidth, labelHeight);
        });

        revisionButtons.values().forEach(button -> {
            button.setStyle(fontSizeStyle + radiusStyle);
            button.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
        });

        revisionButtonIcons.values().forEach(icon -> ImageUtil.setImageViewSize(icon, 20 * scaleFactor, 20 * scaleFactor));

        revisionButtonsContainer.getChildren().forEach(node -> {
            if (node instanceof VBox revisionBox) {
                revisionBox.setStyle(borderStyle);
            }
        });
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
        String revisionString = "revision.";

        newCardsNameLabels.forEach((revisionStrategy, nameLabel) -> {
            String translationKey = revisionString + revisionStrategy.getRevisionStrategyTranslationKey() + ".new_cards";
            nameLabel.setText(translationService.getTranslation(translationKey) + ": ");
        });

        cardsNameLabels.forEach((revisionStrategy, nameLabel) -> {
            String translationKey = revisionString + revisionStrategy.getRevisionStrategyTranslationKey() + ".old_cards";
            nameLabel.setText(translationService.getTranslation(translationKey) + ": ");
        });

        revisionButtons.forEach((revisionStrategy, button) -> {
            String translationKey = revisionString + revisionStrategy.getRevisionStrategyTranslationKey() + ".revision_button";
            button.setText(translationService.getTranslation(translationKey));
        });
    }

    /**
     * Updates the number of new cards per day for the current deck.
     * Saves the new value to the deck's statistics.
     */
    private void updateNewCardsPerDay() {
        try {
            int newCardsPerDay = Integer.parseInt(newCardsPerDayTextField.getText());
            Optional<DeckBaseStatistics> statOpt = deckStatisticsService.loadDeckStatistics(deck.getDeckBaseStatistics().getIdDeckStatistics());
            if (statOpt.isPresent()) {
                DeckBaseStatistics stat = statOpt.get();
                stat.setNewCardsPerDay(newCardsPerDay);
                deckStatisticsService.saveDeckStatistics(stat);
            }

            String revisionNewCardsCounts = String.valueOf(deckStatisticsService.getNewCardsForToday(deck.getDeckBaseStatistics().getIdDeckStatistics()));

            newCardsCountLabels.forEach((revisionType, countLabel) -> countLabel.setText(revisionNewCardsCounts));
        } catch (NumberFormatException ignored) {
            // ignore
        }
    }

    /**
     * Updates the revision statistics displayed in the view.
     */
    @Override
    public void updateView() {
        updateNewCardsPerDay();

        for (Map.Entry<RevisionStrategy<?>, Label> entry : cardsCountLabels.entrySet()) {
            int updatedCount = entry.getKey().getRevisionStrategyCardsCount(deckService, deck);
            entry.getValue().setText(String.valueOf(updatedCount));
        }
    }

    /**
     * Loads the revision buttons and statistics display for each revision type supported by the deck.
     */
    private void loadRevisions() {
        revisionButtonsContainer.getChildren().clear();
        revisionButtonsContainer.setAlignment(Pos.CENTER);

        revisionButtons.clear();
        revisionButtonIcons.clear();
        newCardsNameLabels.clear();
        newCardsCountLabels.clear();
        cardsNameLabels.clear();
        cardsCountLabels.clear();

        deck.getRevisionAlgorithm().getAvailableRevisionStrategies().forEach(revisionStrategy -> {
            Button revisionButton = new Button();
            revisionButton.setOnAction(e -> stage.setScene(revisionStrategy.getRevisionStrategyScene(), deck));
            revisionButton.setTextFill(revisionStrategy.getRevisionStrategyColor());
            revisionButtons.put(revisionStrategy, revisionButton);

            ImageView buttonIcon = ImageUtil.createImageView("/images/icons/revision.png");
            revisionButton.setGraphic(buttonIcon);
            revisionButtonIcons.put(revisionStrategy, buttonIcon);

            Label newCardsNameLabel = new Label();
            newCardsNameLabel.setTextFill(Color.BLUE);
            newCardsNameLabel.setAlignment(Pos.CENTER_RIGHT);
            Label newCardsCountLabel = new Label();
            newCardsCountLabel.setTextFill(Color.BLUE);
            newCardsNameLabels.put(revisionStrategy, newCardsNameLabel);
            newCardsCountLabels.put(revisionStrategy, newCardsCountLabel);
            Label cardsNameLabel = new Label();
            cardsNameLabel.setTextFill(revisionStrategy.getRevisionStrategyColor());
            cardsNameLabel.setAlignment(Pos.CENTER_RIGHT);
            Label cardsCountLabel = new Label();
            cardsCountLabel.setTextFill(revisionStrategy.getRevisionStrategyColor());
            cardsNameLabels.put(revisionStrategy, cardsNameLabel);
            cardsCountLabels.put(revisionStrategy, cardsCountLabel);

            HBox revisionStatBox = new HBox(newCardsNameLabel, newCardsCountLabel, cardsNameLabel, cardsCountLabel);
            revisionStatBox.setAlignment(Pos.CENTER);
            VBox revisionBox = new VBox(revisionStatBox, revisionButton);
            revisionBox.setAlignment(Pos.CENTER);
            revisionButtonsContainer.getChildren().add(revisionBox);
        });

        scrollPane.setContent(revisionButtonsContainer);
        scrollPane.setFitToWidth(true);
    }
}
