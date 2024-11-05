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
        FXMLController controller = null;
        if (nextScene == MainStageScenes.LANGUAGE_SELECTION_SCENE) {
            controller = applicationContext.getBean(LanguageSelectionFXMLController.class);
        } else if (nextScene == MainStageScenes.LOGIN_SCENE) {
            controller = applicationContext.getBean(LoginFXMLController.class);
        } else if (nextScene == MainStageScenes.DECKS_SCENE) {
            controller = applicationContext.getBean(DecksFXMLController.class);
        } else if (nextScene == MainStageScenes.NEW_DECK_SCENE) {
            controller = applicationContext.getBean(NewDeckFXMLController.class);
        } else if (nextScene == MainStageScenes.TRASH_SCENE) {
            controller = applicationContext.getBean(TrashFXMLController.class);
        } else if (nextScene == MainStageScenes.AUTHORS_SCENE) {
            controller = applicationContext.getBean(AuthorsFXMLController.class);
        } else if (nextScene == MainStageScenes.EXIT) {
            shutdown();
        } else if (nextScene == MainStageScenes.REVISION_ADD_NEW_CARD_SCENE) {
            controller = applicationContext.getBean(RevisionAddCardFXMLController.class);
        } else if (nextScene == MainStageScenes.REVISION_SEARCH_SCENE) {
            controller = applicationContext.getBean(RevisionSearchFXMLController.class);
        } else if (nextScene == MainStageScenes.REVISION_REVISE_SCENE) {
            controller = applicationContext.getBean(RevisionFXMLController.class);
        } else if (nextScene == MainStageScenes.REVISION_SETTINGS_SCENE) {
            controller = applicationContext.getBean(RevisionSettingsFXMLController.class);
        } else if (nextScene == MainStageScenes.REVISION_STATISTICS_SCENE) {
            controller = applicationContext.getBean(RevisionStatisticsFXMLController.class);
        } else if (nextScene == MainStageScenes.REVISION_EDIT_CARD_SCENE) {
            controller = applicationContext.getBean(RevisionEditCardFXMLController.class);
        } else if (nextScene == MainStageScenes.REVISION_REGULAR_SCENE) {
            controller = applicationContext.getBean(RevisionRegularFXMLController.class);
        } else if (nextScene == MainStageScenes.REVISION_REVERSE_SCENE) {
            controller = applicationContext.getBean(RevisionReverseFXMLController.class);
        }

        if (controller != null) {
            this.currentController = controller;
            controller.loadViewFromFXML();
            controller.initWithParams(nextSceneParams);

            double currentX = this.stage.getX();
            double currentY = this.stage.getY();
            if (stage.getScene() != null) {
                stage.getScene().setRoot(new javafx.scene.Group());
            }
            this.stage.setScene(new Scene(controller.root));
            Platform.runLater(() ->{
                this.stage.setX(currentX);
                this.stage.setY(currentY);
            });
            controller.updateView();
            controller.updateSize();
            controller.updateTranslation();
        }
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
