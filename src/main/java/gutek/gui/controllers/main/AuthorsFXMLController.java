package gutek.gui.controllers.main;

import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

/**
 * Controller for the view displaying author and year information.
 * <p>
 * This view shows the name of the author and the creation year in a centered layout.
 * It also includes a menu bar at the top of the view.
 */
@Component
public class AuthorsFXMLController extends FXMLController {

    /**
     *  Root pane for the view, used to hold the menu and author information labels.
     */
    @FXML
    private BorderPane rootPane;

    /** Label displaying the author's name. */
    @FXML
    private Label authorLabel;

    /** Label displaying the year of creation. */
    @FXML
    private Label yearLabel;

    /** Controller for the menu bar included at the top of the view. */
    private final MenuBarFXMLController menuBarFXMLController;

    /**
     * Constructs the `AuthorsFXMLController`, initializing its components.
     *
     * @param stage              The main frame of the application.
     * @param fxmlFileLoader     Utility for loading the FXML file associated with this view.
     * @param translationService The service used for managing translations in the application.
     * @param menuBarFXMLController Controller for the menu bar included in this view.
     */
    public AuthorsFXMLController(MainStage stage,
                                 FXMLFileLoader fxmlFileLoader,
                                 TranslationService translationService,
                                 MenuBarFXMLController menuBarFXMLController) {
        super(stage, fxmlFileLoader, "/fxml/main/AuthorsView.fxml", translationService);
        this.menuBarFXMLController = menuBarFXMLController;
    }

    /**
     * Updates the size and layout of the components based on the current window size and scale factor.
     * The labels are centered in the view with dynamic font sizes and positions.
     */
    @Override
    public void updateSize() {
        menuBarFXMLController.updateSize();

        double scaleFactor = stage.getStageScaleFactor();
        String fontSizeStyle = "-fx-font-size: " + (40 * scaleFactor) + "px; -fx-font-family: 'Comic Sans MS';";

        authorLabel.setStyle(fontSizeStyle);
        yearLabel.setStyle(fontSizeStyle);
    }

    /**
     * Updates the translations for the components. Currently, this view does not have dynamic translations,
     * but this method ensures compatibility with the overall translation structure.
     */
    @Override
    public void updateTranslation(){
        menuBarFXMLController.updateTranslation();
    }

    /**
     * Updates the view by reloading the list of decks for the current user and displaying them in the panel.
     */
    @Override
    public void updateView() {
        menuBarFXMLController.updateView();
    }

    /**
     * Initializes the controller with optional parameters.
     * Currently, it simply initializes the menu bar without needing any specific parameters.
     *
     * @param params Optional parameters; currently unused.
     */
    @Override
    public void initWithParams(Object... params) {
        rootPane.setTop(menuBarFXMLController.getRoot());
        menuBarFXMLController.initWithParams();
    }
}
