package gutek.gui.controllers.menu;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.utils.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

/**
 * The `MenuDeckFXMLController` class represents a menu for deck-related actions,
 * including adding cards, browsing the deck, revising cards, viewing settings,
 * viewing statistics, and closing the menu.
 * This controller dynamically updates the button labels and sizes based on the
 * current language and screen size settings.
 */
@Component
public class MenuDeckFXMLController extends FXMLController {

    /** Button to add a new card to the deck. */
    @FXML
    private Button addCardButton;

    /**
     * Icon for the "addCardButton" menu item.
     */
    private ImageView addCardButtonIcon;

    /** Button to browse the deck for existing cards. */
    @FXML
    private Button browseDeckButton;

    /**
     * Icon for the "browseDeckButton" menu item.
     */
    private ImageView browseDeckButtonIcon;

    /** Button to start a revision session for the deck. */
    @FXML
    private Button revisionButton;

    /**
     * Icon for the "revisionButton" menu item.
     */
    private ImageView revisionButtonIcon;

    /** Button to open the settings of the deck's revision algorithm. */
    @FXML
    private Button settingsButton;

    /**
     * Icon for the "settingsButton" menu item.
     */
    private ImageView settingsButtonIcon;

    /** Button to view statistics related to the deck's revision process. */
    @FXML
    private Button statsButton;

    /**
     * Icon for the "statsButton" menu item.
     */
    private ImageView statsButtonIcon;

    /** Button to close the menu and return to the decks view. */
    @FXML
    private Button closeButton;

    /**
     * Icon for the "closeButton" menu item.
     */
    private ImageView closeButtonIcon;

    /** The deck associated with this menu, allowing context-sensitive actions. */
    private DeckBase deck;

    /**
     * Constructs a new `MenuDeckFXMLController` with buttons for interacting with the deck.
     *
     * @param stage the main stage of the application, used to switch views
     * @param fxmlFileLoader     Utility for loading FXML files.
     * @param translationService the service used for retrieving translations for the button labels
     */
    public MenuDeckFXMLController(MainStage stage, FXMLFileLoader fxmlFileLoader, TranslationService translationService) {
        super(stage, fxmlFileLoader, "/fxml/menu/MenuDeckView.fxml", translationService);
        loadViewFromFXML();
    }

    /**
     * Initializes the menu with the current deck if provided and sets up button actions.
     *
     * @param params Optional parameters, where the first element may be a `DeckBase` instance.
     */
    @Override
    public void initWithParams(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof DeckBase deckBase) {
            this.deck = deckBase;
        }
        initializeMenuActions();
        initializeIcons();
    }

    /**
     * Configures button actions to navigate between views related to the deck, including
     * adding a card, browsing, revising, accessing settings, viewing statistics, and closing the menu.
     */
    private void initializeMenuActions() {
        addCardButton.setOnAction(e -> stage.setScene(MainStageScenes.REVISION_ADD_NEW_CARD_SCENE, deck));
        browseDeckButton.setOnAction(e -> stage.setScene(MainStageScenes.REVISION_SEARCH_SCENE, deck));
        revisionButton.setOnAction(e -> stage.setScene(MainStageScenes.REVISION_REVISE_SCENE, deck));
        settingsButton.setOnAction(e -> stage.setScene(MainStageScenes.REVISION_SETTINGS_SCENE, deck));
        statsButton.setOnAction(e -> stage.setScene(MainStageScenes.REVISION_STATISTICS_SCENE, deck));
        closeButton.setOnAction(e -> stage.setScene(MainStageScenes.DECKS_SCENE));
    }

    /**
     * Updates the text of the buttons based on the current language settings using the translation service.
     */
    @Override
    public void updateTranslation() {
        addCardButton.setText(translationService.getTranslation("deck_view.menu.add_card"));
        browseDeckButton.setText(translationService.getTranslation("deck_view.menu.browse"));
        revisionButton.setText(translationService.getTranslation("deck_view.menu.revision"));
        settingsButton.setText(translationService.getTranslation("deck_view.menu.settings"));
        statsButton.setText(translationService.getTranslation("deck_view.menu.statistics"));
        closeButton.setText(translationService.getTranslation("deck_view.menu.close"));
    }

    /**
     * Adjusts the font sizes and button dimensions based on the current window size and scale factor.
     */
    @Override
    public void updateSize() {
        double scaleFactor = stage.getStageScaleFactor();
        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";
        String buttonRadiusStyle = "-fx-background-radius: " + (100 * scaleFactor) + "; -fx-border-radius: " + (100 * scaleFactor) + ";";

        addCardButton.setStyle(fontSizeStyle + buttonRadiusStyle);
        browseDeckButton.setStyle(fontSizeStyle + buttonRadiusStyle);
        revisionButton.setStyle(fontSizeStyle + buttonRadiusStyle);
        settingsButton.setStyle(fontSizeStyle + buttonRadiusStyle);
        statsButton.setStyle(fontSizeStyle + buttonRadiusStyle);
        closeButton.setStyle(fontSizeStyle + buttonRadiusStyle);

        addCardButton.setPrefSize(stage.getStage().getWidth() / 6, 60 * scaleFactor);
        browseDeckButton.setPrefSize(stage.getStage().getWidth() / 6, 60 * scaleFactor);
        revisionButton.setPrefSize(stage.getStage().getWidth() / 6, 60 * scaleFactor);
        settingsButton.setPrefSize(stage.getStage().getWidth() / 6, 60 * scaleFactor);
        statsButton.setPrefSize(stage.getStage().getWidth() / 6, 60 * scaleFactor);
        closeButton.setPrefSize(stage.getStage().getWidth() / 6, 60 * scaleFactor);

        updateIcons(scaleFactor);
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
     * Initializes the icons used in the controller's UI components.
     */
    private void initializeIcons() {
        double scaleFactor = stage.getStageScaleFactor();
        addCardButtonIcon = ImageUtil.createImageView("/images/icons/new.png");
        addCardButton.setGraphic(addCardButtonIcon);
        browseDeckButtonIcon = ImageUtil.createImageView("/images/icons/browse.png");
        browseDeckButton.setGraphic(browseDeckButtonIcon);
        revisionButtonIcon = ImageUtil.createImageView("/images/icons/revision.png");
        revisionButton.setGraphic(revisionButtonIcon);
        settingsButtonIcon = ImageUtil.createImageView("/images/icons/setting.png");
        settingsButton.setGraphic(settingsButtonIcon);
        statsButtonIcon = ImageUtil.createImageView("/images/icons/statistics.png");
        statsButton.setGraphic(statsButtonIcon);
        closeButtonIcon = ImageUtil.createImageView("/images/icons/back.png");
        closeButton.setGraphic(closeButtonIcon);
        updateIcons(scaleFactor);
    }

    /**
     * Updates the size of each icon according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon.
     */
    private void updateIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(addCardButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(browseDeckButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(revisionButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(settingsButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(statsButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(closeButtonIcon, 20 * scaleFactor, 20 * scaleFactor);
    }
}