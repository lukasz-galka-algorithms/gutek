package gutek.gui.controllers.deck;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.CardService;
import gutek.gui.controls.CardCell;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Locale;

/**
 * Controller class for searching cards within a deck.
 * <p>
 * This view provides a form for searching cards by their front or back text and displays matching results
 * in a list, with options to edit or delete each card.
 */
@Component
public class RevisionSearchFXMLController extends FXMLController {

    /**
     * Root pane containing the main layout for this view.
     */
    @FXML
    private BorderPane rootPane;

    /**
     * Container for the menu components.
     */
    @FXML
    private VBox menuContainer;

    /**
     * Text field for entering the search term to match the front text of cards.
     */
    @FXML
    private TextField frontInCardTextField;

    /**
     * Text field for entering the search term to match the back text of cards.
     */
    @FXML
    private TextField backInCardTextField;

    /**
     * Button to initiate the search based on the entered search terms.
     */
    @FXML
    private Button searchButton;

    /**
     * List view displaying the search results, showing cards that match the search terms.
     */
    @FXML
    private ListView<CardBase> cardListView;

    /**
     * Property representing the current scale factor, used to adjust component sizes.
     */
    @Getter
    private final DoubleProperty scaleFactorProperty = new SimpleDoubleProperty();

    /**
     * Property representing the current locale, used for applying translations.
     */
    @Getter
    private final ObjectProperty<Locale> currentLocaleProperty = new SimpleObjectProperty<>();

    /**
     * Service for managing card-related operations.
     */
    private final CardService cardService;

    /**
     * Controller for the main menu bar of the application.
     */
    private final MenuBarFXMLController menuBarFXMLController;

    /**
     * Controller for the deck-specific menu actions.
     */
    private final MenuDeckFXMLController menuDeckFXMLController;

    /**
     * The deck being searched, containing the cards to search within.
     */
    private DeckBase deck;

    /**
     * Constructs a new `RevisionSearchFXMLController` for searching cards in a deck.
     *
     * @param stage                 The main stage of the application.
     * @param fxmlFileLoader        Utility for loading FXML files associated with this scene.
     * @param translationService    Service for retrieving translations for the UI.
     * @param menuBarFXMLController Controller for the main menu bar.
     * @param menuDeckFXMLController Controller for deck-specific menu actions.
     * @param cardService           Service for managing cards.
     */
    public RevisionSearchFXMLController(MainStage stage,
                                        FXMLFileLoader fxmlFileLoader,
                                        TranslationService translationService,
                                        MenuBarFXMLController menuBarFXMLController,
                                        MenuDeckFXMLController menuDeckFXMLController,
                                        CardService cardService) {
        super(stage, fxmlFileLoader, "/fxml/deck/RevisionSearchView.fxml", translationService);
        this.cardService = cardService;
        this.menuBarFXMLController = menuBarFXMLController;
        this.menuDeckFXMLController = menuDeckFXMLController;
    }

    /**
     * Initializes the view with parameters, setting up the deck and configuring the menu components.
     * Binds the search button action to update the view with search results.
     *
     * @param params Array of parameters, where the first element is expected to be a `DeckBase` instance.
     */
    @Override
    public void initWithParams(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof DeckBase) {
            this.deck = (DeckBase) params[0];
            menuDeckFXMLController.initWithParams(deck);
        }
        menuBarFXMLController.initWithParams();

        menuContainer.getChildren().setAll(menuBarFXMLController.getRoot(), menuDeckFXMLController.getRoot());

        searchButton.setOnAction(e -> updateView());

        cardListView.setCellFactory(listView ->
                new CardCell(translationService, stage, fxmlFileLoader, cardService, this));
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
        scaleFactorProperty.set(scaleFactor);
        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";

        frontInCardTextField.setStyle(fontSizeStyle);
        backInCardTextField.setStyle(fontSizeStyle);
        searchButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;");

        frontInCardTextField.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        backInCardTextField.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        searchButton.setPrefSize(100 * scaleFactor, 40 * scaleFactor);
    }

    /**
     * Updates the text of the view components based on the current language settings.
     */
    @Override
    public void updateTranslation() {
        menuBarFXMLController.updateTranslation();
        menuDeckFXMLController.updateTranslation();

        currentLocaleProperty.set(translationService.getCurrentLocale());

        frontInCardTextField.setPromptText(translationService.getTranslation("deck_view.search_card.front_phase"));
        backInCardTextField.setPromptText(translationService.getTranslation("deck_view.search_card.back_phase"));
        searchButton.setText(translationService.getTranslation("deck_view.search_card.search"));
    }

    /**
     * Executes a search based on the user's input and displays the results in the card list view.
     */
    @Override
    public void updateView() {
        menuBarFXMLController.updateView();
        menuDeckFXMLController.updateView();

        String frontSearchTerm = frontInCardTextField.getText().trim();
        String backSearchTerm = backInCardTextField.getText().trim();

        List<CardBase> cards = cardService.findCardsByUser(frontSearchTerm, backSearchTerm, deck);
        cardListView.setItems(FXCollections.observableArrayList(cards));
    }

    /**
     * Removes a specified card from the list view.
     *
     * @param card The card to remove from the search results list.
     */
    public void removeCardFromListView(CardBase card){
        cardListView.getItems().remove(card);
    }
}
