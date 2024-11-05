package gutek.gui.controllers.deck;

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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Controller for managing the reverse revision of cards in a deck.
 * <p>
 * This class facilitates a revision session where the back (translation) of a card is shown first, allowing the user
 * to guess the front (word). It supports options for handling revision based on the deck's revision algorithm.
 */
@Component
public class RevisionReverseFXMLController extends FXMLController {

    /**
     * Root pane for the view, containing all other components.
     */
    @FXML
    private BorderPane rootPane;

    /**
     * Container for the menu components in the view.
     */
    @FXML
    private VBox menuContainer;

    /**
     * Label for displaying the word (front) of the card.
     */
    private Label wordLabel;

    /**
     * Label for displaying the translation (back) of the card.
     */
    @FXML
    private Label translationLabel;

    /**
     * Text area for user input to guess the word.
     */
    private TextArea wordTextField;

    /**
     * Container for holding the word label and text field.
     */
    @FXML
    private HBox wordContainer;

    /**
     * Button to reveal the word associated with the current translation.
     */
    private Button showButton;

    /**
     * Button to end the current revision session.
     */
    private Button endRevisionButton;

    /**
     * Container for the main action buttons (show and end session).
     */
    @FXML
    private HBox buttonContainer;

    /**
     * Container for algorithm-specific revision buttons.
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
     * Service for managing deck statistics, including tracking progress.
     */
    private final DeckStatisticsService deckStatisticsService;

    /**
     * Service for handling the revision process of individual cards.
     */
    private final CardRevisionService cardRevisionService;

    /**
     * Random number generator for selecting cards for revision in random order.
     */
    private final Random random = new Random();

    /**
     * List of previously revised cards available for reverse revision.
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
     * The deck containing the cards to be revised.
     */
    private DeckBase deck;

    /**
     * Constructs a new `RevisionReverseFXMLController` to facilitate reverse revision of cards.
     *
     * @param stage                 The main stage of the application.
     * @param fxmlFileLoader        Utility for loading FXML files associated with this scene.
     * @param translationService    Service for retrieving translations for the UI.
     * @param menuBarFXMLController Controller for the main menu bar.
     * @param menuDeckFXMLController Controller for deck-specific menu actions.
     * @param cardService           Service for managing cards.
     * @param deckStatisticsService Service for managing deck statistics.
     * @param cardRevisionService   Service for handling card revisions.
     * @param deckService           Service for managing deck-related operations.
     */
    public RevisionReverseFXMLController(MainStage stage,
                                         FXMLFileLoader fxmlFileLoader,
                                         TranslationService translationService,
                                         MenuBarFXMLController menuBarFXMLController,
                                         MenuDeckFXMLController menuDeckFXMLController,
                                         CardService cardService,
                                         DeckStatisticsService deckStatisticsService,
                                         CardRevisionService cardRevisionService,
                                         DeckService deckService) {
        super(stage, fxmlFileLoader, "/fxml/deck/RevisionReverseView.fxml", translationService);
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
        if (params != null && params.length > 0 && params[0] instanceof DeckBase) {
            this.deck = (DeckBase) params[0];
            this.oldCardsList = deckService.getReverseRevisionCards(deck);
            this.newCardsList = deckService.getNewCardsForTodayRevision(deck, deckStatisticsService.getNewCardsForToday(deck.getDeckBaseStatistics().getIdDeckStatistics()));
            menuDeckFXMLController.initWithParams(deck);
        }
        menuBarFXMLController.initWithParams();

        menuContainer.getChildren().setAll(menuBarFXMLController.getRoot(), menuDeckFXMLController.getRoot());

        wordLabel = new Label();
        wordLabel.setWrapText(true);
        wordTextField = new TextArea();
        wordTextField.setWrapText(true);
        showButton = new Button();
        endRevisionButton = new Button();
        showButton.setOnAction(e -> showWord());
        endRevisionButton.setOnAction(e -> endRevisionSession());
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
                "-fx-font-size: " + (18 * scaleFactor) + "px; " +
                "-fx-alignment: center;";
        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";

        wordLabel.setStyle(fontSizeStyleText);
        wordTextField.setStyle(fontSizeStyleText);
        translationLabel.setStyle(fontSizeStyleText);
        showButton.setStyle(fontSizeStyle);
        endRevisionButton.setStyle(fontSizeStyle);

        if (currentCard != null) {
            new Timeline(new KeyFrame(Duration.millis(20), e -> {
                double sectionHeight = rootPane.getCenter().getBoundsInLocal().getHeight() / 3;
                wordLabel.setPrefSize(stage.getStage().getWidth() / 2, sectionHeight);
                wordTextField.setPrefSize(stage.getStage().getWidth() / 2, sectionHeight);
                translationLabel.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                showButton.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                endRevisionButton.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                currentCard.getDeck().getRevisionAlgorithm().updateSize(stage.getStage().getWidth(), sectionHeight, scaleFactor);
            })).play();
        }else{
            new Timeline(new KeyFrame(Duration.millis(20), e -> {
                double sectionHeight = rootPane.getCenter().getBoundsInLocal().getHeight() / 3;
                wordLabel.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                translationLabel.setPrefSize(stage.getStage().getWidth(), sectionHeight);
                endRevisionButton.setPrefSize(stage.getStage().getWidth(), sectionHeight);
            })).play();
        }
    }

    /**
     * Updates the text in the view components based on the current language settings.
     */
    @Override
    public void updateTranslation() {
        this.menuBarFXMLController.updateTranslation();
        this.menuDeckFXMLController.updateTranslation();

        showButton.setText(translationService.getTranslation("deck_view.reverse_revision.show_button"));
        endRevisionButton.setText(translationService.getTranslation("deck_view.reverse_revision.end_button"));

        if (currentCard != null) {
            currentCard.getDeck().getRevisionAlgorithm().updateTranslation();
        } else {
            translationLabel.setText(translationService.getTranslation("deck_view.reverse_revision.end_title"));
            wordLabel.setText(translationService.getTranslation("deck_view.reverse_revision.end_message"));
        }
    }

    /**
     * Updates the view.
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
     * Displays the word (front) of the current card.
     */
    private void showWord() {
        translationLabel.setText(currentCard.getBack());
        wordLabel.setText(currentCard.getFront());

        buttonContainer.getChildren().setAll(algorithmButtonContainer);
        wordContainer.getChildren().setAll(wordLabel,wordTextField);
    }

    /**
     * Displays the message indicating the end of the revision session.
     */
    private void showRevisionEnd() {
        translationLabel.setText(translationService.getTranslation("deck_view.reverse_revision.end_title"));
        wordLabel.setText(translationService.getTranslation("deck_view.reverse_revision.end_message"));

        buttonContainer.getChildren().setAll(endRevisionButton);
        wordContainer.getChildren().setAll(wordLabel);
        updateSize();
    }

    /**
     * Displays the translation (back) of the card and prepares the button to reveal the word (front).
     */
    private void showTranslation() {
        wordLabel.setText("");
        wordTextField.setText("");
        translationLabel.setText(currentCard.getBack());

        buttonContainer.getChildren().setAll(showButton);
        wordContainer.getChildren().setAll(wordLabel,wordTextField);
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
            showTranslation();
        } else {
            showRevisionEnd();
        }
    }

    /**
     * Loads the algorithm-specific buttons for revising the current card.
     *
     * @param <T> the type of card being revised
     * @return the panel containing the algorithm-specific buttons
     */
    private <T extends CardBase> Pane loadAlgorithmButtons() {
        RevisionAlgorithm<T> algorithm = (RevisionAlgorithm<T>) currentCard.getDeck().getRevisionAlgorithm();
        Pane panel = algorithm.getReverseRevisionButtonsPane((T) currentCard);
        algorithm.setTranslationService(translationService);
        updateSize();
        updateTranslation();

        panel.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(e -> {
                    cardRevisionService.reviseReverse(currentCard, algorithmButtonContainer.getChildren().indexOf(button));
                    if (currentCard.isNewCard()) {
                        deckStatisticsService.newCardRevised(currentCard.getDeck().getDeckBaseStatistics().getIdDeckStatistics());
                    }
                    boolean cardRevisionFinished = algorithm.reverseReviseCard(button, (T) currentCard);
                    if (cardRevisionFinished) {
                        newCardsList.remove(currentCard);
                        oldCardsList.remove(currentCard);
                        deckStatisticsService.cardRevisedReverse(currentCard.getDeck().getDeckBaseStatistics().getIdDeckStatistics());
                    }
                    currentCard.setNewCard(false);
                    cardService.saveCard(currentCard);

                    handleNextCard();
                });
            }
        });
        return panel;
    }

    /**
     * Ends the revision session and returns to the main revision view.
     */
    private void endRevisionSession() {
        stage.setScene(MainStageScenes.REVISION_REVISE_SCENE);
    }
}
