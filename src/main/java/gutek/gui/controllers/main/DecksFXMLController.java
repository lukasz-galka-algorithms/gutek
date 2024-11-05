package gutek.gui.controllers.main;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controls.DeckCell;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Locale;


/**
 * Controller for the main view displaying all decks associated with a user.
 * <p>
 * This view provides options for navigating to different functionalities related to deck management,
 * such as adding cards, revising, searching, and viewing statistics.
 */
@Component
public class DecksFXMLController extends FXMLController {

    /** The root pane for this view, containing the menu bar and list of decks. */
    @FXML
    private BorderPane rootPane;

    /** The ListView displaying the user's decks as cells. */
    @FXML
    private ListView<DeckBase> deckListView;

    /** Controller for the menu bar at the top of the view. */
    private final MenuBarFXMLController menuBarFXMLController;

    /** Property for scaling UI components proportionally to the window size. */
    @Getter
    private final DoubleProperty scaleFactorProperty = new SimpleDoubleProperty();

    /** Property representing the current locale for language settings. */
    @Getter
    private final ObjectProperty<Locale> currentLocaleProperty = new SimpleObjectProperty<>();

    /** Service for handling deck operations. */
    private final DeckService deckService;

    /**
     * Constructs the `DecksFXMLController`, initializing components and layout.
     *
     * @param stage               The main application stage.
     * @param fxmlFileLoader      Utility for loading the FXML file for this view.
     * @param translationService  Service for managing translations.
     * @param menuBarFXMLController Controller for the menu bar in this view.
     * @param deckService         Service for managing decks.
     */
    public DecksFXMLController(MainStage stage,
                               FXMLFileLoader fxmlFileLoader,
                               TranslationService translationService,
                               MenuBarFXMLController menuBarFXMLController,
                               DeckService deckService) {
        super(stage, fxmlFileLoader, "/fxml/main/DecksView.fxml", translationService);
        this.deckService = deckService;
        this.menuBarFXMLController = menuBarFXMLController;
    }

    /**
     * Initializes the view with necessary components and setups.
     * Sets the top of the root pane to the menu bar and configures the deck list cells.
     *
     * @param params Optional parameters; currently unused.
     */
    @Override
    public void initWithParams(Object... params) {
        rootPane.setTop(menuBarFXMLController.getRoot());
        deckListView.setCellFactory(listView ->
                new DeckCell(translationService, stage, fxmlFileLoader, deckService, this));
        menuBarFXMLController.initWithParams();
    }

    /**
     * Updates the size and layout of the components, adjusting the deck cells based on the current window scale.
     */
    @Override
    public void updateSize() {
        menuBarFXMLController.updateSize();

        double scaleFactor = stage.getStageScaleFactor();
        scaleFactorProperty.set(scaleFactor);
    }

    /**
     * Updates the translations of the components, ensuring the UI displays the correct language.
     */
    @Override
    public void updateTranslation() {
        menuBarFXMLController.updateTranslation();
        currentLocaleProperty.set(translationService.getCurrentLocale());
    }

    /**
     * Updates the view by reloading the list of decks for the current user and displaying them in the panel.
     */
    @Override
    public void updateView() {
        menuBarFXMLController.updateView();

        List<DeckBase> decks = deckService.findDecksByUserNotDeleted(stage.getLoggedUser());
        deckListView.setItems(FXCollections.observableArrayList(decks));
    }

    /**
     * Removes a specific deck from the ListView when it is deleted or no longer needed in the view.
     *
     * @param deck The deck to be removed from the list.
     */
    public void removeDeckFromListView(DeckBase deck){
        deckListView.getItems().remove(deck);
    }
}
