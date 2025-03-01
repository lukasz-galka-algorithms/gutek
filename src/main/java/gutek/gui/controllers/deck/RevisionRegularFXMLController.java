package gutek.gui.controllers.deck;

import gutek.domain.revisions.RegularTextModeRevisionStrategy;
import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.*;
import gutek.utils.FXMLFileLoader;
import gutek.utils.ImageUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controller class for managing the regular revision of cards in a deck.
 * <p>
 * This class provides a user interface for conducting a revision session where the front
 * of a card is displayed, and users can reveal the translation (back) and interact
 * with revision options based on the deck's revision algorithm.
 */
@Component
public class RevisionRegularFXMLController extends FXMLController {

    /**
     * Root pane for this view.
     */
    @FXML
    private BorderPane rootPane;

    /**
     * Container for the menu components.
     */
    @FXML
    private VBox menuContainer;

    /**
     * Label displaying the word (front) of the card.
     */
    @FXML
    private Label wordLabel;

    /**
     * Label displaying the translation (back) of the card.
     */
    @FXML
    private Label translationLabel;

    /**
     * Button to reveal the translation of the card.
     */
    private Button showButton;

    /**
     * Icon for the "showButton".
     */
    private ImageView showButtonIcon;

    /**
     * Button to end the revision session.
     */
    private Button endRevisionButton;

    /**
     * Icon for the "endRevisionButton".
     */
    private ImageView endRevisionButtonIcon;

    /**
     * Container for revision action buttons.
     */
    @FXML
    private HBox buttonContainer;

    /**
     * Container for algorithm-specific buttons, set based on the revision algorithm.
     */
    private Pane algorithmButtonContainer;

    /**
     * Service for managing deck-related operations.
     */
    private final DeckService deckService;

    /**
     * Service for managing card-related operations.
     */
    private final CardService cardService;

    /**
     * Service for managing deck statistics.
     */
    private final DeckStatisticsService deckStatisticsService;

    /**
     * Service for handling card revisions.
     */
    private final CardRevisionService cardRevisionService;

    /**
     * Random number generator for selecting cards to revise.
     */
    private final Random random = new Random();

    /**
     * List of old cards available for regular revision.
     */
    private List<CardBase> oldCardsList;

    /**
     * List of new cards available for revision today.
     */
    private List<CardBase> newCardsList;

    /**
     * Controller for managing the main menu bar.
     */
    private final MenuBarFXMLController menuBarFXMLController;

    /**
     * Controller for managing deck-specific menu actions.
     */
    private final MenuDeckFXMLController menuDeckFXMLController;

    /**
     * The current card being revised in the session.
     */
    private CardBase currentCard;

    /**
     * Constructs a new `RevisionRegularFXMLController` for facilitating the regular revision of cards.
     *
     * @param stage                  The main stage of the application.
     * @param fxmlFileLoader         Utility for loading FXML files associated with this scene.
     * @param translationService     Service for retrieving translations for the UI.
     * @param menuBarFXMLController  Controller for the main menu bar.
     * @param menuDeckFXMLController Controller for deck-specific menu actions.
     * @param cardService            Service for managing cards.
     * @param deckStatisticsService  Service for managing deck statistics.
     * @param cardRevisionService    Service for handling card revisions.
     * @param deckService            Service for managing deck-related operations.
     */
    public RevisionRegularFXMLController(MainStage stage,
                                         FXMLFileLoader fxmlFileLoader,
                                         TranslationService translationService,
                                         MenuBarFXMLController menuBarFXMLController,
                                         MenuDeckFXMLController menuDeckFXMLController,
                                         CardService cardService,
                                         DeckStatisticsService deckStatisticsService,
                                         CardRevisionService cardRevisionService,
                                         DeckService deckService) {
        super(stage, fxmlFileLoader, "/fxml/deck/RevisionRegularView.fxml", translationService);
        this.deckService = deckService;
        this.cardService = cardService;
        this.deckStatisticsService = deckStatisticsService;
        this.cardRevisionService = cardRevisionService;
        this.menuBarFXMLController = menuBarFXMLController;
        this.menuDeckFXMLController = menuDeckFXMLController;
    }

    /**
     * Initializes the view with parameters, setting up the deck and loading cards for revision.
     * Configures the menu components and binds actions for the revision buttons.
     *
     * @param params Array of parameters, where the first element is expected to be a `DeckBase` instance.
     */
    @Override
    public void initWithParams(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof DeckBase deckBase) {
            this.oldCardsList = new ArrayList<>(deckService.getRegularRevisionCards(deckBase));
            this.newCardsList = new ArrayList<>(deckService.getNewCardsForTodayRevision(deckBase, deckStatisticsService.getNewCardsForToday(deckBase.getDeckBaseStatistics().getIdDeckStatistics())));
            menuDeckFXMLController.initWithParams(deckBase);
        }
        menuBarFXMLController.initWithParams();

        menuContainer.getChildren().setAll(menuBarFXMLController.getRoot(), menuDeckFXMLController.getRoot());

        showButton = new Button();
        endRevisionButton = new Button();
        showButton.setOnAction(e -> showTranslation());
        endRevisionButton.setOnAction(e -> endRevisionSession());
        initializeIcons();
        handleNextCard();
    }

    /**
     * Updates the size of the view components based on the window size and scale factor.
     * Adjusts font sizes and component dimensions dynamically.
     */
    @Override
    public void updateSize() {
        this.menuBarFXMLController.updateSize();
        this.menuDeckFXMLController.updateSize();

        double scaleFactor = stage.getStageScaleFactor();
        String fontSizeStyleText = "-fx-border-color: black; -fx-border-width: 5 5 5 5; " +
                "-fx-font-size: " + (18 * scaleFactor) + "px;";
        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";
        String buttonRadiusStyle = "-fx-background-radius: " + (20 * scaleFactor) + "; -fx-border-radius: " + (20 * scaleFactor) + ";";

        wordLabel.setStyle(fontSizeStyleText);
        translationLabel.setStyle(fontSizeStyleText);
        showButton.setStyle(fontSizeStyle + buttonRadiusStyle);
        endRevisionButton.setStyle(fontSizeStyle + buttonRadiusStyle);

        if (currentCard != null) {
            new Timeline(new KeyFrame(Duration.millis(20), e -> {
                double sectionHeight = rootPane.getCenter().getBoundsInLocal().getHeight() / 3;
                wordLabel.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                translationLabel.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                showButton.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                endRevisionButton.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                if (currentCard != null) {
                    currentCard.getDeck().getRevisionAlgorithm().updateSize(stage.getStage().getWidth(), sectionHeight, scaleFactor);
                }
            })).play();
        } else {
            new Timeline(new KeyFrame(Duration.millis(20), e -> {
                double sectionHeight = rootPane.getCenter().getBoundsInLocal().getHeight() / 3;
                wordLabel.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                translationLabel.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                endRevisionButton.setPrefSize(stage.getStage().getWidth(), sectionHeight);
            })).play();
        }

        updateIcons(scaleFactor);
    }

    /**
     * Updates the text in the view components based on the current language settings.
     */
    @Override
    public void updateTranslation() {
        this.menuBarFXMLController.updateTranslation();
        this.menuDeckFXMLController.updateTranslation();

        showButton.setText(translationService.getTranslation("deck_view.regular_revision.show_button"));
        endRevisionButton.setText(translationService.getTranslation("deck_view.regular_revision.end_button"));

        if (currentCard == null) {
            wordLabel.setText(translationService.getTranslation("deck_view.regular_revision.end_title"));
            translationLabel.setText(translationService.getTranslation("deck_view.regular_revision.end_message"));
        } else {
            currentCard.getDeck().getRevisionAlgorithm().updateTranslation();
        }
    }

    /**
     * Updates the view by loading the next card and setting up the revision buttons.
     */
    @Override
    public void updateView() {
        this.menuBarFXMLController.updateView();
        this.menuDeckFXMLController.updateView();

        if (currentCard != null) {
            currentCard.getDeck().getRevisionAlgorithm().setTranslationService(translationService);
        }
    }

    /**
     * Displays the translation (back) of the current card.
     */
    private void showTranslation() {
        wordLabel.setText(currentCard.getFront());
        translationLabel.setText(currentCard.getBack());

        buttonContainer.getChildren().setAll(algorithmButtonContainer);
        updateSize();
        updateTranslation();
    }

    /**
     * Displays the message indicating the end of the revision session.
     */
    private void showRevisionEnd() {
        wordLabel.setText(translationService.getTranslation("deck_view.regular_revision.end_title"));
        translationLabel.setText(translationService.getTranslation("deck_view.regular_revision.end_message"));

        buttonContainer.getChildren().setAll(endRevisionButton);
        updateSize();
        updateTranslation();
    }

    /**
     * Displays the word (front) of the card and prepares the button to reveal the translation.
     */
    private void showWord() {
        wordLabel.setText(currentCard.getFront());
        translationLabel.setText("");

        buttonContainer.getChildren().setAll(showButton);
        updateSize();
        updateTranslation();
    }

    /**
     * Loads the next card to be revised from the list of old or new cards.
     */
    public void loadNextCard() {
        if (oldCardsList.isEmpty() && newCardsList.isEmpty()) {
            currentCard = null;
            return;
        }

        int oldCardsSize = oldCardsList.size();
        int newCardsSize = newCardsList.size();
        int totalSize = oldCardsSize + newCardsSize;

        int randomIndex = random.nextInt(totalSize);
        if (randomIndex < oldCardsSize) {
            currentCard = oldCardsList.get(randomIndex);
        } else {
            currentCard = newCardsList.get(randomIndex - oldCardsSize);
        }

        algorithmButtonContainer = loadAlgorithmButtons();
    }

    /**
     * Handles loading the next card and displaying it in the view.
     */
    private void handleNextCard() {
        loadNextCard();
        if (currentCard != null) {
            showWord();
        } else {
            showRevisionEnd();
        }
    }

    /**
     * Loads the algorithm-specific revision buttons based on the revision algorithm of the current card's deck.
     *
     * @param <T> The type of the card.
     * @return Pane containing the revision buttons generated by the algorithm.
     */
    @SuppressWarnings("unchecked")
    private <T extends CardBase> Pane loadAlgorithmButtons() {
        RevisionAlgorithm<T> algorithm = (RevisionAlgorithm<T>) currentCard.getDeck().getRevisionAlgorithm();
        algorithm.setTranslationService(translationService);
        algorithm.initializeGUI(stage.getStage().getWidth(), rootPane.getCenter().getBoundsInLocal().getHeight() / 3, stage.getStageScaleFactor());

        List<RevisionStrategy<T>> strategies = algorithm.getAvailableRevisionStrategies();
        for (RevisionStrategy<T> strategy : strategies) {
            if (strategy instanceof RegularTextModeRevisionStrategy<T> regularStrategy) {
                Pane panel = regularStrategy.getRevisionButtonsPane((T) currentCard);
                setupButtonActions(panel, regularStrategy, (T) currentCard);
                return panel;
            }
        }
        return null;
    }

    /**
     * Sets up action listeners for the buttons generated by the regular text mode revision strategy.
     *
     * @param panel           The panel containing the buttons.
     * @param regularStrategy The regular text mode revision strategy.
     * @param currentCard     The card currently being revised.
     * @param <T>             The type of the card.
     */
    private <T extends CardBase> void setupButtonActions(Pane panel, RegularTextModeRevisionStrategy<T> regularStrategy, T currentCard) {
        panel.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                button.setOnAction(e -> handleButtonClick(button, regularStrategy, panel, currentCard));
            }
        });
    }

    /**
     * Handles the button click event during the revision session.
     * Updates the card revision state and loads the next card.
     *
     * @param button          The button that was clicked.
     * @param regularStrategy The regular text mode revision strategy.
     * @param panel           The panel containing the buttons.
     * @param currentCard     The card currently being revised.
     * @param <T>             The type of the card.
     */
    private <T extends CardBase> void handleButtonClick(Button button, RegularTextModeRevisionStrategy<T> regularStrategy, Pane panel, T currentCard) {
        int buttonIndex = panel.getChildren().indexOf(button);
        cardRevisionService.revise(currentCard, buttonIndex, regularStrategy);

        if (currentCard.isNewCard()) {
            deckStatisticsService.newCardRevised(currentCard.getDeck().getDeckBaseStatistics().getIdDeckStatistics());
        }

        boolean cardRevisionFinished = regularStrategy.reviseCard(button, currentCard);

        if (cardRevisionFinished) {
            newCardsList.remove(currentCard);
            oldCardsList.remove(currentCard);
            int strategyIndex = currentCard.getDeck().getRevisionAlgorithm().getRevisionStrategies().indexOf(regularStrategy);
            deckStatisticsService.cardRevised(currentCard.getDeck().getDeckBaseStatistics().getIdDeckStatistics(), strategyIndex);
        }

        currentCard.setNewCard(false);
        cardService.saveCard(currentCard);
        handleNextCard();
    }

    /**
     * Ends the revision session and returns to the main revision view.
     */
    private void endRevisionSession() {
        stage.setScene(MainStageScenes.REVISION_REVISE_SCENE);
    }

    /**
     * Initializes the icons used in the controller's UI components.
     */
    private void initializeIcons() {
        double scaleFactor = stage.getStageScaleFactor();
        showButtonIcon = ImageUtil.createImageView("/images/icons/show.png");
        showButton.setGraphic(showButtonIcon);
        endRevisionButtonIcon = ImageUtil.createImageView("/images/icons/complete.png");
        endRevisionButton.setGraphic(endRevisionButtonIcon);
        updateIcons(scaleFactor);
    }

    /**
     * Updates the size of each icon according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon.
     */
    private void updateIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(showButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(endRevisionButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
    }
}
