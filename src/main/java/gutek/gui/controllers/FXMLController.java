package gutek.gui.controllers;

import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.scene.Parent;
import lombok.Getter;

/**
 * The `FXMLController` class serves as an abstract base class for all FXML controllers in the application.
 * It provides common methods for updating translations, resizing, and refreshing views, which can be inherited
 * by specific view controllers.
 */
public abstract class FXMLController {

    /**
     * The main application stage that holds this view.
     */
    protected final MainStage stage;

    /**
     * Service responsible for handling translations in the application.
     */
    protected final TranslationService translationService;

    /**
     * Utility for loading FXML files within the application.
     */
    protected final FXMLFileLoader fxmlFileLoader;

    /**
     * Path to the FXML file associated with this scene.
     */
    protected final String fxmlFilePath;

    /**
     * Root element of the loaded FXML view, representing the UI structure.
     */
    @Getter
    protected Parent root;

    /**
     * Constructs a new `AppFXMLController` object with the specified dependencies.
     *
     * @param stage               the main application stage where the scene is displayed
     * @param fxmlFileLoader      utility for loading the FXML file associated with this scene
     * @param fxmlFilePath        the path to the FXML file for this scene
     * @param translationService  the service responsible for handling translations within this scene
     */
    protected FXMLController(MainStage stage, FXMLFileLoader fxmlFileLoader, String fxmlFilePath, TranslationService translationService) {
        this.stage = stage;
        this.translationService = translationService;
        this.fxmlFileLoader = fxmlFileLoader;
        this.fxmlFilePath = fxmlFilePath;
    }

    /**
     * Updates the translations of the current scene.
     * Expected to be overridden by subclasses to handle translations of specific components.
     */
    public void updateTranslation() {
    }

    /**
     * Updates the size of components in the current scene.
     * Expected to be overridden by subclasses to handle resizing of specific components.
     */
    public void updateSize() {
    }

    /**
     * Updates the contents of the current scene.
     * Subclasses can override this method to refresh or reload specific data or UI elements.
     */
    public void updateView() {
    }

    /**
     * Loads the view from the specified FXML file if it has not been loaded already.
     */
    public void loadViewFromFXML() {
        if (this.root == null) {
            this.root = fxmlFileLoader.loadFXML(fxmlFilePath, null);
        }
    }

    /**
     * Initializes the controller with optional parameters.
     * Subclasses can override this to initialize specific data or context.
     *
     * @param params optional parameters to initialize the controller
     */
    public void initWithParams(Object... params) {
    }
}
