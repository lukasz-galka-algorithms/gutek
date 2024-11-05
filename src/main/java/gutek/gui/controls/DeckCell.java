package gutek.gui.controls;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.controls.DeckCellFXMLController;
import gutek.gui.controllers.main.DecksFXMLController;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.scene.control.ListCell;

/**
 * Custom cell for displaying `DeckBase` objects within a ListView.
 * Each cell uses a `DeckCellFXMLController` to handle the display of a `DeckBase` object.
 */
public class DeckCell extends ListCell<DeckBase> {

    /** Controller responsible for managing the view for this cell */
    private final DeckCellFXMLController controller;

    /** The deck currently displayed in this cell */
    private DeckBase currentDeck;

    /**
     * Constructs a new `DeckCell` with the required dependencies to display a `DeckBase`.
     *
     * @param translationService  Service for handling translations within the cell
     * @param stage               Main application stage, used for switching scenes if needed
     * @param fxmlFileLoader      Utility for loading FXML files
     * @param deckService         Service for managing deck-related operations
     * @param parentController    Reference to the parent `DecksFXMLController` for coordination
     */
    public DeckCell(TranslationService translationService,
                    MainStage stage,
                    FXMLFileLoader fxmlFileLoader,
                    DeckService deckService,
                    DecksFXMLController parentController) {
        this.controller = new DeckCellFXMLController(stage, fxmlFileLoader, translationService, deckService, parentController);
        this.controller.loadViewFromFXML();
        this.currentDeck = null;
    }

    /**
     * Updates the content of the cell to display the provided `DeckBase` object, or clears it if empty.
     *
     * @param deck  The `DeckBase` object to display in this cell
     * @param empty Whether this cell should be empty (true) or not (false)
     */
    @Override
    protected void updateItem(DeckBase deck, boolean empty) {
        super.updateItem(deck, empty);

        if (empty || deck == null) {
            setGraphic(null);
        } else {
            if (currentDeck != deck){
                controller.setDeck(deck);
                currentDeck = deck;
            }
            setGraphic(controller.getRoot());
        }
    }
}