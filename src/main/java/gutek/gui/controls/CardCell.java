package gutek.gui.controls;

import gutek.entities.cards.CardBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.controls.CardCellFXMLController;
import gutek.gui.controllers.deck.RevisionSearchFXMLController;
import gutek.services.CardService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.scene.control.ListCell;

/**
 * Custom cell for displaying `CardBase` objects within a ListView.
 * Each cell loads and displays the content of a `CardBase` using the `CardCellFXMLController`.
 */
public class CardCell extends ListCell<CardBase> {

    /** Controller responsible for managing the view associated with this cell */
    private final CardCellFXMLController controller;

    /** The card currently displayed in this cell */
    private CardBase currentCard;

    /**
     * Constructs a new `CardCell` with dependencies required for managing and displaying card information.
     *
     * @param translationService  Service for handling translations within the cell
     * @param stage               Main application stage, used to switch scenes if necessary
     * @param fxmlFileLoader      Utility for loading FXML files
     * @param cardService         Service for managing card-related operations
     * @param parentController    Reference to the parent `RevisionSearchFXMLController` for coordination
     */
    public CardCell(TranslationService translationService,
                    MainStage stage,
                    FXMLFileLoader fxmlFileLoader,
                    CardService cardService,
                    RevisionSearchFXMLController parentController) {
        this.controller = new CardCellFXMLController(stage, fxmlFileLoader, translationService, cardService, parentController);
        this.controller.loadViewFromFXML();
        this.currentCard = null;
    }

    /**
     * Updates the content of the cell to display the `CardBase` object or clears it if empty.
     *
     * @param card  The `CardBase` object to display in this cell
     * @param empty Whether this cell should be empty (true) or not (false)
     */
    @Override
    protected void updateItem(CardBase card, boolean empty) {
        super.updateItem(card, empty);

        if (empty || card == null) {
            setGraphic(null);
        } else {
            if (currentCard != card) {
                controller.setCard(card);
                currentCard = card;
            }
            setGraphic(controller.getRoot());
        }
    }
}
