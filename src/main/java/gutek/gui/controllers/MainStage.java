package gutek.gui.controllers;

import gutek.entities.users.AppUser;
import gutek.gui.controllers.deck.*;
import gutek.gui.controllers.launch.LanguageSelectionFXMLController;
import gutek.gui.controllers.launch.LoginFXMLController;
import gutek.gui.controllers.main.AuthorsFXMLController;
import gutek.gui.controllers.main.DecksFXMLController;
import gutek.gui.controllers.main.NewDeckFXMLController;
import gutek.gui.controllers.main.TrashFXMLController;
import gutek.services.TranslationService;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import java.util.EnumMap;
import java.util.Map;

/**
 * Main application stage that manages the display of views and the application menu.
 * It handles view creation and interaction between different components of the GUI.
 */
@Component
@Data
public class MainStage{
    /** Default width of the application window, retrieved from application.properties */
    @Value("${app.window.width}")
    private int defaultWidth;

    /** Default height of the application window, retrieved from application.properties */
    @Value("${app.window.height}")
    private int defaultHeight;

    /** The user currently logged into the application. */
    private AppUser loggedUser;

    /** Spring context for managing beans and dependencies. */
    private final ConfigurableApplicationContext applicationContext;

    /** Service for managing translations across the application. */
    private final TranslationService translationService;

    /** The currently active view in the application. */
    private FXMLController currentController;

    /** Primary stage for displaying views */
    private Stage stage;

    /**
     * Constructs the MainStage with the given Spring context and translation service.
     *
     * @param applicationContext Spring context for managing beans
     * @param translationService Service for handling translations
     */
    public MainStage(ConfigurableApplicationContext applicationContext, TranslationService translationService) {
        this.translationService = translationService;
        this.applicationContext = applicationContext;
        translationService.updateLocale(null);
    }

    /**
     * Initializes the primary stage and sets its properties.
     *
     * @param stage the primary Stage of the application
     */
    public void initStage(Stage stage) {
        this.stage = stage;
        stage.setWidth(defaultWidth);
        stage.setHeight(defaultHeight);
        stage.setX((Screen.getPrimary().getBounds().getWidth() - defaultWidth) / 2);
        stage.setY((Screen.getPrimary().getBounds().getHeight() - defaultHeight) / 2);
        stage.widthProperty().addListener((obs, oldVal, newVal) -> updateSize());
        stage.heightProperty().addListener((obs, oldVal, newVal) -> updateSize());
        updateTranslation();
    }

    /**
     * Sets the view in the application based on the provided scene enum.
     * Retrieves the required view using the Spring context and updates the stage.
     *
     * @param nextScene      The view to display next
     * @param nextSceneParams Optional parameters for initializing the scene
     */
    public void setScene(MainStageScenes nextScene, Object... nextSceneParams) {
        if (nextScene == MainStageScenes.EXIT) {
            shutdown();
            return;
        }
        FXMLController controller = getControllerForScene(nextScene);

        if (controller != null) {
            loadController(controller, nextSceneParams);
        }
    }

    /**
     * Retrieves the appropriate controller for the given scene.
     *
     * @param nextScene The scene for which to get the controller.
     * @return The corresponding FXMLController, or null if none found.
     */
    private FXMLController getControllerForScene(MainStageScenes nextScene) {
        Map<MainStageScenes, Class<? extends FXMLController>> sceneControllerMap = getSceneControllerMap();
        Class<? extends FXMLController> controllerClass = sceneControllerMap.get(nextScene);

        if (controllerClass != null) {
            return applicationContext.getBean(controllerClass);
        }
        return null;
    }

    /**
     * Initializes the mapping between scenes and their corresponding controller classes.
     *
     * @return A map of MainStageScenes to FXMLController classes.
     */
    private Map<MainStageScenes, Class<? extends FXMLController>> getSceneControllerMap() {
        Map<MainStageScenes, Class<? extends FXMLController>> map = new EnumMap<>(MainStageScenes.class);
        map.put(MainStageScenes.LANGUAGE_SELECTION_SCENE, LanguageSelectionFXMLController.class);
        map.put(MainStageScenes.LOGIN_SCENE, LoginFXMLController.class);
        map.put(MainStageScenes.DECKS_SCENE, DecksFXMLController.class);
        map.put(MainStageScenes.NEW_DECK_SCENE, NewDeckFXMLController.class);
        map.put(MainStageScenes.TRASH_SCENE, TrashFXMLController.class);
        map.put(MainStageScenes.AUTHORS_SCENE, AuthorsFXMLController.class);
        map.put(MainStageScenes.REVISION_ADD_NEW_CARD_SCENE, RevisionAddCardFXMLController.class);
        map.put(MainStageScenes.REVISION_SEARCH_SCENE, RevisionSearchFXMLController.class);
        map.put(MainStageScenes.REVISION_REVISE_SCENE, RevisionFXMLController.class);
        map.put(MainStageScenes.REVISION_SETTINGS_SCENE, RevisionSettingsFXMLController.class);
        map.put(MainStageScenes.REVISION_STATISTICS_SCENE, RevisionStatisticsFXMLController.class);
        map.put(MainStageScenes.REVISION_EDIT_CARD_SCENE, RevisionEditCardFXMLController.class);
        map.put(MainStageScenes.REVISION_REGULAR_SCENE, RevisionRegularFXMLController.class);
        map.put(MainStageScenes.REVISION_REVERSE_SCENE, RevisionReverseFXMLController.class);
        return map;
    }

    /**
     * Loads the controller, sets up the scene, and updates the view.
     *
     * @param controller       The controller to load.
     * @param nextSceneParams  Optional parameters for initializing the scene.
     */
    private void loadController(FXMLController controller, Object... nextSceneParams) {
        this.currentController = controller;
        controller.loadViewFromFXML();
        controller.initWithParams(nextSceneParams);

        double currentX = this.stage.getX();
        double currentY = this.stage.getY();

        if (stage.getScene() != null) {
            stage.getScene().setRoot(new javafx.scene.Group());
        }

        this.stage.setScene(new Scene(controller.root));

        Platform.runLater(() -> {
            this.stage.setX(currentX);
            this.stage.setY(currentY);
        });

        controller.updateView();
        controller.updateSize();
        controller.updateTranslation();
    }

    /**
     * Updates the size and scaling of the application window components based on current dimensions.
     */
    public void updateSize() {
        if (currentController != null) {
            currentController.updateSize();
        }
    }

    /**
     * Updates the translation of the application's title based on the selected locale.
     */
    public void updateTranslation() {
        this.stage.setTitle(translationService.getTranslation("window.title"));
    }

    /**
     * Shuts down the application.
     */
    public void shutdown() {
        if (stage != null) {
            stage.close();
        }
        Platform.exit();
    }

    /**
     * Retrieves the scaling factor based on the current window size relative to the default dimensions.
     *
     * @return The calculated scaling factor
     */
    public double getStageScaleFactor(){
        return Math.min(stage.getWidth() / this.getDefaultWidth(), stage.getHeight() / this.getDefaultHeight());
    }
}
