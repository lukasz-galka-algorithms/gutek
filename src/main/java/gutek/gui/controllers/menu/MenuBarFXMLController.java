package gutek.gui.controllers.menu;

import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.ImageUtil;
import gutek.utils.StringUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
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
     * Icon for the main file menu. Displays an icon associated with the "File" menu in the menu bar.
     */
    private ImageView fileMenuIcon;

    /**
     * Icon for the "Decks" menu item. Represents an icon for navigating to the decks view.
     */
    private ImageView decksIcon;

    /**
     * Icon for the "New" menu item. Used to navigate to the view for creating a new deck.
     */
    private ImageView newIcon;

    /**
     * Icon for the "Trash" menu item. Used to open the view where deleted decks can be managed.
     */
    private ImageView trashIcon;

    /**
     * Icon for the "Authors" menu item. Represents an icon for navigating to the authors view.
     */
    private ImageView authorsIcon;

    /**
     * Icon for the "Exit" menu item. Used to represent the exit action in the file menu.
     */
    private ImageView exitIcon;

    /**
     * Icon for the "Logout" menu. Displays an icon for the session management menu.
     */
    private ImageView logoutMenuIcon;

    /**
     * Icon for the "Logout" menu item. Represents an icon for logging out of the application.
     */
    private ImageView logoutIcon;


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
        initializeMenuIcons();
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

            ImageView flagIcon = createFlagIcon(locale);
            langItem.setGraphic(flagIcon);

            langItem.setOnAction(e -> {
                translationService.updateLocale(locale);
                stage.updateTranslation();
                if (stage.getCurrentController() != null) {
                    stage.getCurrentController().updateTranslation();
                }
                updateSize();
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

        updateLanguageMenuIcons(scaleFactor);
        updateFileMenuIcons(scaleFactor);
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

    /**
     * Creates an ImageView containing the flag icon for the specified Locale.
     * This method uses the IconUtil class to generate an icon based on the country code
     * from the given Locale. The flag icon is loaded from the resources folder.
     *
     * @param locale the Locale object representing the language and country.
     * @return an ImageView containing the flag icon, or an empty ImageView if the icon is not found.
     */
    private ImageView createFlagIcon(Locale locale) {
        String countryCode = locale.getCountry();
        return ImageUtil.createImageView("/images/flags/" + countryCode + ".png");
    }

    /**
     * Updates the flag icons in the language menu to reflect the current application locale
     * and scales them according to the provided scale factor.
     * This method sets the flag icon for the main language menu item to reflect the
     * currently selected language. It also iterates through each language menu item,
     * scaling its icon based on the current scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each flag icon.
     */
    private void updateLanguageMenuIcons(double scaleFactor) {
        Locale currentLocale = translationService.getCurrentLocale();
        ImageView currentFlagIcon = createFlagIcon(currentLocale);
        ImageUtil.setImageViewSize(currentFlagIcon,20 * scaleFactor, 15 * scaleFactor);
        languageMenu.setGraphic(currentFlagIcon);

        for (MenuItem langItem : languageMenu.getItems()) {
            if (langItem.getGraphic() instanceof ImageView flagIcon) {
                ImageUtil.setImageViewSize(flagIcon,20 * scaleFactor, 15 * scaleFactor);
            }
        }
    }

    /**
     * Initializes the icons for menu without languages menu.
     * This method sets the initial icons for the menu items and stores them
     * so they can be resized later.
     */
    private void initializeMenuIcons() {
        double scaleFactor = stage.getStageScaleFactor();
        fileMenuIcon = ImageUtil.createImageView("/images/icons/home.png");
        fileMenu.setGraphic(fileMenuIcon);
        decksIcon = ImageUtil.createImageView("/images/icons/browse.png");
        decksMenuItem.setGraphic(decksIcon);
        newIcon = ImageUtil.createImageView("/images/icons/new.png");
        newMenuItem.setGraphic(newIcon);
        trashIcon = ImageUtil.createImageView("/images/icons/trash.png");
        trashMenuItem.setGraphic(trashIcon);
        authorsIcon = ImageUtil.createImageView("/images/icons/author.png");
        authorsMenuItem.setGraphic(authorsIcon);
        exitIcon = ImageUtil.createImageView("/images/icons/exit.png");
        exitMenuItem.setGraphic(exitIcon);
        logoutMenuIcon = ImageUtil.createImageView("/images/icons/session.png");
        logoutMenu.setGraphic(logoutMenuIcon);
        logoutIcon = ImageUtil.createImageView("/images/icons/logout.png");
        logoutMenuItem.setGraphic(logoutIcon);
        updateFileMenuIcons(scaleFactor);
    }

    /**
     * Updates the size of each icon in the menu according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon in fileMenu.
     */
    private void updateFileMenuIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(fileMenuIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(decksIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(newIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(trashIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(authorsIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(exitIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(logoutMenuIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(logoutIcon, 20 * scaleFactor, 20 * scaleFactor);
    }
}
