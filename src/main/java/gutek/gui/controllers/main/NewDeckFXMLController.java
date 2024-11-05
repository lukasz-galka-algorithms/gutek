package gutek.gui.controllers.main;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.services.DeckService;
import gutek.services.RevisionAlgorithmService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import static gutek.utils.AlertMessageUtil.showAlert;

/**
 * Controller for the New Deck view, allowing users to create a new deck or import cards from a file.
 * <p>
 * Users can specify a deck name and choose a revision algorithm, then add a new deck or import
 * cards directly into the deck from an external file.
 */
@Component
public class NewDeckFXMLController extends FXMLController {

    /** The root pane for this view, containing all UI components. */
    @FXML
    private BorderPane rootPane;

    /** Label prompting the user to enter a deck name. */
    @FXML
    private Label nameLabel;

    /** Text field for inputting the name of the new deck. */
    @FXML
    private TextField nameField;

    /** Label prompting the user to select a revision algorithm. */
    @FXML
    private Label algorithmLabel;

    /** ComboBox for selecting a revision algorithm for the deck. */
    @FXML
    private ComboBox<String> algorithmComboBox;

    /** Button to add a new deck with the specified name and selected algorithm. */
    @FXML
    private Button addButton;

    /** Button to import a deck from a file. */
    @FXML
    private Button importButton;

    /**
     * Service for managing revision algorithms.
     */
    private final RevisionAlgorithmService revisionAlgorithmService;
    /**
     * Service for managing decks.
     */
    private final DeckService deckService;

    /** Controller for the menu bar at the top of the view. */
    private final MenuBarFXMLController menuBarFXMLController;

    /**
     * Constructs the `NewDeckFXMLController`, initializing components and layout.
     *
     * @param stage                    The main application stage.
     * @param fxmlFileLoader           Utility for loading the FXML file for this view.
     * @param translationService       The service for managing translations.
     * @param menuBarFXMLController    The controller for the menu bar.
     * @param revisionAlgorithmService Service providing available revision algorithms.
     * @param deckService              Service for managing deck operations.
     */
    public NewDeckFXMLController(MainStage stage,
                                 FXMLFileLoader fxmlFileLoader,
                                 TranslationService translationService,
                                 MenuBarFXMLController menuBarFXMLController,
                                 RevisionAlgorithmService revisionAlgorithmService,
                                 DeckService deckService) {
        super(stage, fxmlFileLoader, "/fxml/main/NewDeckView.fxml", translationService);
        this.revisionAlgorithmService = revisionAlgorithmService;
        this.deckService = deckService;
        this.menuBarFXMLController = menuBarFXMLController;
    }

    /**
     * Initializes the view components and configures button actions.
     *
     * @param params Optional parameters, currently unused.
     */
    @Override
    public void initWithParams(Object... params) {
        addButton.setOnAction(event -> handleAddDeck());
        importButton.setOnAction(event -> handleImportDeck());

        menuBarFXMLController.initWithParams();
    }

    /**
     * Updates the size and layout of the UI components according to the current window size and scaling factor.
     */
    @Override
    public void updateSize() {
        menuBarFXMLController.updateSize();

        double scaleFactor = stage.getStageScaleFactor();
        String fontSizeStyle = "-fx-font-size: " + (14 * scaleFactor) + "px;";

        nameLabel.setStyle(fontSizeStyle);
        nameField.setStyle(fontSizeStyle);
        algorithmLabel.setStyle(fontSizeStyle);
        algorithmComboBox.setStyle(fontSizeStyle);
        addButton.setStyle(fontSizeStyle + " -fx-background-color: green; -fx-text-fill: white;");
        importButton.setStyle(fontSizeStyle + " -fx-background-color: blue; -fx-text-fill: white;");

        nameLabel.setPrefSize(200 * scaleFactor, 30 * scaleFactor);
        nameField.setPrefSize(300 * scaleFactor, 30 * scaleFactor);
        algorithmLabel.setPrefSize(200 * scaleFactor,30 * scaleFactor);
        algorithmComboBox.setPrefSize(300 * scaleFactor,30 * scaleFactor);
        addButton.setPrefSize(150 * scaleFactor,40 * scaleFactor);
        importButton.setPrefSize(150 * scaleFactor,40 * scaleFactor);
    }

    /**
     * Updates the translations for the UI components based on the current locale.
     */
    @Override
    public void updateTranslation() {
        menuBarFXMLController.updateTranslation();

        nameLabel.setText(translationService.getTranslation("new_deck_view.name_label"));
        algorithmLabel.setText(translationService.getTranslation("new_deck_view.algorithm_label"));
        addButton.setText(translationService.getTranslation("new_deck_view.add_button"));
        importButton.setText(translationService.getTranslation("new_deck_view.import_button"));

        String selectedAlgorithm = algorithmComboBox.getSelectionModel().getSelectedItem();
        algorithmComboBox.getItems().clear();
        algorithmComboBox.getItems().addAll(FXCollections.observableArrayList(revisionAlgorithmService.getAlgorithmNames()));
        if (selectedAlgorithm != null) {
            algorithmComboBox.getSelectionModel().select(selectedAlgorithm);
        }
    }

    /**
     * Updates the view by setting the menu bar and refreshing the list of algorithms in the ComboBox.
     */
    @Override
    public void updateView() {
        rootPane.setTop(menuBarFXMLController.getRoot());
        menuBarFXMLController.updateView();
        algorithmComboBox.setItems(FXCollections.observableArrayList(revisionAlgorithmService.getAlgorithmNames()));
    }

    /**
     * Handles the action of adding a new deck with the specified name and selected algorithm.
     */
    private void handleAddDeck() {
        String deckName = nameField.getText();
        String selectedAlgorithmName = algorithmComboBox.getSelectionModel().getSelectedItem();

        RevisionAlgorithm<?> algorithm = revisionAlgorithmService.createAlgorithmInstance(selectedAlgorithmName);

        if (algorithm != null){
            algorithm.setTranslationService(translationService);
            if (deckName.trim().isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, translationService.getTranslation("new_deck_view.deck_name_empty"));
            }else{
                deckService.addNewDeck(stage.getLoggedUser(), algorithm, deckName);
                showAlert(Alert.AlertType.INFORMATION, translationService.getTranslation("new_deck_view.deck_added"));
            }
        }else{
            showAlert(Alert.AlertType.INFORMATION, translationService.getTranslation("new_deck_view.algorithm_empty"));
        }
    }

    /**
     * Handles the action of importing a deck from a file and adding it to the system with the selected algorithm.
     */
    private void handleImportDeck() {
        String deckName = nameField.getText();
        String selectedAlgorithmName = algorithmComboBox.getSelectionModel().getSelectedItem();

        RevisionAlgorithm<?> algorithm = revisionAlgorithmService.createAlgorithmInstance(selectedAlgorithmName);
        if (algorithm != null){
            algorithm.setTranslationService(translationService);
            if (deckName.trim().isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, translationService.getTranslation("new_deck_view.deck_name_empty"));
            }else{
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
                File selectedFile = fileChooser.showOpenDialog(stage.getStage());

                if (selectedFile != null) {
                    DeckBase deckBase = deckService.addNewDeck(stage.getLoggedUser(), algorithm, deckName);
                    try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                        long cardsNumber = Long.parseLong(br.readLine());
                        for (int i = 0; i < cardsNumber; i++) {
                            CardBase card = algorithm.createNewCard(br.readLine(), br.readLine());
                            deckService.addNewCardToDeck(card, deckBase);
                        }
                        showAlert(Alert.AlertType.INFORMATION, translationService.getTranslation("new_deck_view.deck_imported"));
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.INFORMATION, translationService.getTranslation("new_deck_view.import_error"));
                        ex.printStackTrace();
                    }
                }
            }
        }else{
            showAlert(Alert.AlertType.INFORMATION, translationService.getTranslation("new_deck_view.algorithm_empty"));
        }
    }
}
