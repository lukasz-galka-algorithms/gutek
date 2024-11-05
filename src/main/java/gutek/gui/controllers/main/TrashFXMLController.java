package gutek.gui.controllers.main;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controls.TrashDeckCell;
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
 * Controller for the Trash view, providing options to manage deleted decks.
 * <p>
 * The Trash view displays a list of deleted decks with options to either restore or permanently delete them.
 */
@Component
public class TrashFXMLController extends FXMLController {

    /** The root pane of the Trash view, containing all UI components. */
    @FXML
    private BorderPane rootPane;

    /** The ListView displaying deleted decks as cells, each with restore and delete options. */
    @FXML
    private ListView<DeckBase> trashDeckListView;

    /** Service for managing deck operations. */
    private final DeckService deckService;

    /** Controller for the top menu bar in the Trash view. */
    private final MenuBarFXMLController menuBarFXMLController;

    /** Property representing the scale factor for dynamically resizing UI components. */
    @Getter
    private final DoubleProperty scaleFactorProperty = new SimpleDoubleProperty();

    /** Property representing the current locale, for dynamic language updates. */
    @Getter
    private final ObjectProperty<Locale> currentLocaleProperty = new SimpleObjectProperty<>();

    /**
     * Constructs a new `TrashFXMLController` for managing deleted decks.
     *
     * @param stage              The main application stage.
     * @param fxmlFileLoader     Utility for loading the FXML file for this view.
     * @param translationService The service responsible for handling translations.
     * @param menuBarFXMLController    The controller for the menu bar.
     * @param deckService        The service responsible for managing deck operations.
     */
    public TrashFXMLController(MainStage stage,
                               FXMLFileLoader fxmlFileLoader,
                               TranslationService translationService,
                               MenuBarFXMLController menuBarFXMLController,
                               DeckService deckService) {
        super(stage, fxmlFileLoader, "/fxml/main/TrashView.fxml", translationService);
        this.deckService = deckService;
        this.menuBarFXMLController = menuBarFXMLController;
    }

    /**
     * Initializes the Trash view, setting up the cell factory for the deck list and initializing the menu bar.
     *
     * @param params Optional parameters, currently unused.
     */
    @Override
    public void initWithParams(Object... params) {
        trashDeckListView.setCellFactory(listView ->
                new TrashDeckCell(translationService, stage, fxmlFileLoader, deckService, this));

        menuBarFXMLController.initWithParams();
    }

    /**
     * Updates the size and layout of the UI components according to the current window size and scaling factor.
     */
    @Override
    public void updateSize() {
        menuBarFXMLController.updateSize();

        double scaleFactor = stage.getStageScaleFactor();
        scaleFactorProperty.set(scaleFactor);
    }

    /**
     * Updates the translations for the UI components based on the current locale.
     */
    @Override
    public void updateTranslation() {
        menuBarFXMLController.updateTranslation();
        currentLocaleProperty.set(translationService.getCurrentLocale());
    }

    /**
     * Updates the view with the list of deleted decks belonging to the currently logged-in user.
     * It removes all existing deck cells and reloads them based on the user's deleted decks.
     */
    @Override
    public void updateView() {
        rootPane.setTop(menuBarFXMLController.getRoot());
        menuBarFXMLController.updateView();

        List<DeckBase> decks = deckService.findDecksByUserDeleted(stage.getLoggedUser());
        trashDeckListView.setItems(FXCollections.observableArrayList(decks));
    }

    /**
     * Removes a specific deck from the ListView, reflecting that it has been restored or permanently deleted.
     *
     * @param deck The deck to remove from the ListView.
     */
    public void removeDeckFromListView(DeckBase deck){
        trashDeckListView.getItems().remove(deck);
    }
}
