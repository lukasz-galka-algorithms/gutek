package gutek.gui.controllers.menu;

import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.StringUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Locale;

/**
 * The `MenuBarFXMLController` class manages the menu bar of the application,
 * providing options for navigating between views and managing language and session settings.
 */
@Component
public class MenuBarFXMLController extends FXMLController {

    /** The main File menu, containing options for deck management and authors. */
    @FXML
    private Menu fileMenu;

    /** Menu item to navigate to the decks view. */
    @FXML
    private MenuItem decksMenuItem;

    /** Menu item to navigate to the new deck creation view. */
    @FXML
    private MenuItem newMenuItem;

    /** Menu item to navigate to the trash view, where deleted decks can be managed. */
    @FXML
    private MenuItem trashMenuItem;

    /** Menu item to navigate to the authors view. */
    @FXML
    private MenuItem authorsMenuItem;

    /** Menu item to exit the application. */
    @FXML
    private MenuItem exitMenuItem;

    /** Menu for selecting the application's language. */
    @FXML
    private Menu languageMenu;

    /** Session management menu, containing options for logging out. */
    @FXML
    private Menu logoutMenu;

    /** Menu item to log out of the current session and return to the login view. */
    @FXML
    private MenuItem logoutMenuItem;

    /**
     * Constructs the `MenuBarFXMLController` and loads the menu bar view from FXML.
     *
     * @param stage              The main stage of the application.
     * @param fxmlFileLoader     Utility for loading FXML files.
     * @param translationService The service used for managing translations.
     */
    public MenuBarFXMLController(MainStage stage, FXMLFileLoader fxmlFileLoader, TranslationService translationService) {
        super(stage, fxmlFileLoader, "/fxml/menu/MenuBarView.fxml", translationService);
        loadViewFromFXML();
    }

    /**
     * Initializes the menu bar by setting actions for each menu item.
     *
     * @param params Optional parameters, currently unused.
     */
    @Override
    public void initWithParams(Object... params) {
        initializeMenuActions();
    }

    /**
     * Binds actions to the menu items to navigate between views, exit the application, or log out.
     */
    private void initializeMenuActions() {
        decksMenuItem.setOnAction(e -> stage.setScene(MainStageScenes.DECKS_SCENE));
        newMenuItem.setOnAction(e -> stage.setScene(MainStageScenes.NEW_DECK_SCENE));
        trashMenuItem.setOnAction(e -> stage.setScene(MainStageScenes.TRASH_SCENE));
        authorsMenuItem.setOnAction(e -> stage.setScene(MainStageScenes.AUTHORS_SCENE));
        exitMenuItem.setOnAction(e -> stage.setScene(MainStageScenes.EXIT));
        logoutMenuItem.setOnAction(e -> {
            stage.setLoggedUser(null);
            stage.setScene(MainStageScenes.LOGIN_SCENE);
        });
    }

    /**
     * Updates the view by refreshing the list of available languages in the language menu.
     */
    @Override
    public void updateView() {
        List<Locale> availableLocales = translationService.getAvailableLocales();
        languageMenu.getItems().clear();
        for (Locale locale : availableLocales) {
            String name = StringUtil.capitalizeFirstLetter(locale.getDisplayLanguage(locale));
            MenuItem langItem = new MenuItem(name);
            langItem.setOnAction(e -> {
                translationService.updateLocale(locale);
                stage.updateTranslation();
                if (stage.getCurrentController() != null) {
                    stage.getCurrentController().updateTranslation();
                }
            });
            languageMenu.getItems().add(langItem);
        }
    }

    /**
     * Updates the translations for each menu and menu item based on the selected language.
     */
    @Override
    public void updateTranslation() {
        fileMenu.setText(translationService.getTranslation("menu_bar.file"));
        decksMenuItem.setText(translationService.getTranslation("menu_bar.file.decks"));
        newMenuItem.setText(translationService.getTranslation("menu_bar.file.new"));
        trashMenuItem.setText(translationService.getTranslation("menu_bar.file.trash"));
        authorsMenuItem.setText(translationService.getTranslation("menu_bar.file.authors"));
        exitMenuItem.setText(translationService.getTranslation("menu_bar.file.exit"));

        languageMenu.setText(translationService.getTranslation("menu_bar.language"));
        logoutMenu.setText(translationService.getTranslation("menu_bar.session"));
        logoutMenuItem.setText(translationService.getTranslation("menu_bar.logout"));
    }

    /**
     * Updates the font size of menu items based on the current scale factor.
     */
    @Override
    public void updateSize() {
        double scaleFactor = stage.getStageScaleFactor();
        String fontSizeStyle = "-fx-font-size: " + (15 * scaleFactor) + "px;";

        fileMenu.setStyle(fontSizeStyle);
        languageMenu.setStyle(fontSizeStyle);
        logoutMenu.setStyle(fontSizeStyle);
        decksMenuItem.setStyle(fontSizeStyle);
        newMenuItem.setStyle(fontSizeStyle);
        trashMenuItem.setStyle(fontSizeStyle);
        authorsMenuItem.setStyle(fontSizeStyle);
        exitMenuItem.setStyle(fontSizeStyle);
        logoutMenuItem.setStyle(fontSizeStyle);
    }

    /**
     * Loads the view from the specified FXML file if it has not been loaded already.
     */
    @Override
    public void loadViewFromFXML() {
        if (this.root == null) {
            this.root = fxmlFileLoader.loadFXML(fxmlFilePath, this);
        }
    }
}
