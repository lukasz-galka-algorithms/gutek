package gutek.gui.controls;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.controls.TrashDeckCellFXMLController;
import gutek.gui.controllers.main.TrashFXMLController;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.scene.control.ListCell;

/**
 * Custom `ListCell` for displaying `DeckBase` items in the trash view.
 * Provides options to restore or permanently delete decks through the associated `TrashDeckCellFXMLController`.
 */
public class TrashDeckCell extends ListCell<DeckBase>{

    /** Controller responsible for handling deck actions (restore, delete) within the trash view. */
    private final TrashDeckCellFXMLController controller;

    /** The current deck displayed in this cell. */
    private DeckBase currentDeck;

    /**
     * Constructs a new `TrashDeckCell`.
     *
     * @param translationService the service used for translations
     * @param stage              the main stage of the application
     * @param fxmlFileLoader     the loader for FXML files
     * @param deckService        the service for managing decks
     * @param parentController   the parent controller managing the trash view
     */
    public TrashDeckCell(TranslationService translationService,
                    MainStage stage,
                    FXMLFileLoader fxmlFileLoader,
                    DeckService deckService,
                    TrashFXMLController parentController) {
        this.controller = new TrashDeckCellFXMLController(stage, fxmlFileLoader, translationService, deckService, parentController);
        this.controller.loadViewFromFXML();
        this.currentDeck = null;
    }

    /**
     * Updates the displayed `DeckBase` item in the trash view.
     *
     * @param deck  the deck to display in this cell
     * @param empty true if this cell is empty
     */
    @Override
    protected void updateItem(DeckBase deck, boolean empty) {
        super.updateItem(deck, empty);

        if (empty || deck == null) {
            setGraphic(null);
        } else {
            if (currentDeck != deck) {
                controller.setDeck(deck);
                currentDeck = deck;
            }
            setGraphic(controller.getRoot());
        }
    }
}