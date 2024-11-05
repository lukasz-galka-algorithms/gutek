package gutek.gui.controllers.deck;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.FXMLController;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.ChartService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;


/**
 * Controller for displaying revision statistics of a deck in chart form.
 * <p>
 * This view enables users to select various chart types and time ranges, updating dynamically
 * to show visualizations of deck revision statistics based on the selections.
 */
@Component
public class RevisionStatisticsFXMLController extends FXMLController {

    /**
     * Root layout pane for the statistics view.
     */
    @FXML
    private BorderPane rootPane;

    /**
     * Container for menu-related components.
     */
    @FXML
    private VBox menuContainer;

    /**
     * Label for the chart type selection.
     */
    @FXML
    private Label chartTypeLabel;

    /**
     * Dropdown menu for selecting the type of chart to display.
     */
    @FXML
    private ComboBox<String> chartTypeComboBox;

    /**
     * Label for the time range selection.
     */
    @FXML
    private Label chartRangeLabel;

    /**
     * Dropdown menu for selecting the time range for chart data.
     */
    @FXML
    private ComboBox<String> timeRangeComboBox;

    /**
     * Pane for displaying the chart.
     */
    @FXML
    private Pane chartContainer;

    /**
     * Service for generating and retrieving chart data.
     */
    private final ChartService chartService;

    /**
     * The deck whose revision statistics are displayed.
     */
    private DeckBase deck;

    /**
     * Controller for managing the main menu bar of the application.
     */
    private final MenuBarFXMLController menuBarFXMLController;

    /**
     * Controller for managing deck-specific menu actions.
     */
    private final MenuDeckFXMLController menuDeckFXMLController;

    /**
     * Constructs a new `RevisionStatisticsFXMLController` for visualizing deck revision statistics.
     *
     * @param stage                 The main stage of the application.
     * @param fxmlFileLoader        Utility for loading FXML files associated with this scene.
     * @param translationService    Service for retrieving translations for the UI.
     * @param chartService          Service for generating and managing chart data.
     * @param menuBarFXMLController Controller for the main menu bar.
     * @param menuDeckFXMLController Controller for deck-specific menu actions.
     */
    public RevisionStatisticsFXMLController(MainStage stage,
                                            FXMLFileLoader fxmlFileLoader,
                                            TranslationService translationService,
                                            MenuBarFXMLController menuBarFXMLController,
                                            MenuDeckFXMLController menuDeckFXMLController,
                                            ChartService chartService) {
        super(stage, fxmlFileLoader, "/fxml/deck/RevisionStatisticsView.fxml", translationService);
        this.chartService = chartService;
        this.menuBarFXMLController = menuBarFXMLController;
        this.menuDeckFXMLController = menuDeckFXMLController;
    }

    /**
     * Initializes the view with parameters, setting up the deck and configuring chart and range options.
     *
     * @param params Array of parameters, where the first element is expected to be a `DeckBase` instance.
     */
    @Override
    public void initWithParams(Object... params) {
        if (params != null && params.length > 0 && params[0] instanceof DeckBase deckBase) {
            this.deck = deckBase;
            menuDeckFXMLController.initWithParams(deck);
        }
        menuBarFXMLController.initWithParams();

        menuContainer.getChildren().setAll(menuBarFXMLController.getRoot(), menuDeckFXMLController.getRoot());

        chartTypeComboBox.getItems().setAll(chartService.getAvailableChartsTitles());
        timeRangeComboBox.getItems().setAll(chartService.getAvailableRanges());

        chartTypeComboBox.setOnAction(e -> {
            updateView();
            updateSize();
        });
        timeRangeComboBox.setOnAction(e -> {
            updateView();
            updateSize();
        });

        if (!chartTypeComboBox.getItems().isEmpty()) {
            chartTypeComboBox.getSelectionModel().select(0);
        }
        if (!timeRangeComboBox.getItems().isEmpty()) {
            timeRangeComboBox.getSelectionModel().select(0);
        }
    }

    /**
     * Updates the size of the view components based on the window size and scale factor.
     * Adjusts font sizes and component dimensions dynamically.
     */
    @Override
    public void updateSize() {
        menuBarFXMLController.updateSize();
        menuDeckFXMLController.updateSize();

        double scaleFactor = stage.getStageScaleFactor();
        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";

        chartTypeLabel.setStyle(fontSizeStyle);
        chartRangeLabel.setStyle(fontSizeStyle);
        chartTypeComboBox.setStyle(fontSizeStyle);
        timeRangeComboBox.setStyle(fontSizeStyle);

        chartTypeLabel.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
        chartRangeLabel.setPrefSize(200 * scaleFactor, 40 * scaleFactor);
        chartTypeComboBox.setPrefSize(300 * scaleFactor, 40 * scaleFactor);
        timeRangeComboBox.setPrefSize(200 * scaleFactor, 40 * scaleFactor);

        double chartWidth = stage.getStage().getWidth();
        double chartHeight = 300 * scaleFactor;
        chartContainer.setPrefSize(chartWidth, chartHeight);

        if (!chartContainer.getChildren().isEmpty() && chartContainer.getChildren().getFirst() instanceof Chart chart) {
            chart.setPrefWidth(chartWidth);
            chart.setPrefHeight(chartHeight);
        }
    }

    /**
     * Updates the text in the view components based on the current language settings.
     */
    @Override
    public void updateTranslation() {
        menuBarFXMLController.updateTranslation();
        menuDeckFXMLController.updateTranslation();

        chartTypeLabel.setText(translationService.getTranslation("deck_view.statistics.chart_type"));
        chartRangeLabel.setText(translationService.getTranslation("deck_view.statistics.chart_range"));

        int selectedChartTypeIndex = chartTypeComboBox.getSelectionModel().getSelectedIndex();
        int selectedRangeIndex = timeRangeComboBox.getSelectionModel().getSelectedIndex();

        chartTypeComboBox.getItems().setAll(chartService.getAvailableChartsTitles());
        timeRangeComboBox.getItems().setAll(chartService.getAvailableRanges());

        if (selectedChartTypeIndex >= 0 && selectedChartTypeIndex < chartTypeComboBox.getItems().size()) {
            chartTypeComboBox.getSelectionModel().select(selectedChartTypeIndex);
        } else if (!chartTypeComboBox.getItems().isEmpty()) {
            chartTypeComboBox.getSelectionModel().select(0);
        }

        if (selectedRangeIndex >= 0 && selectedRangeIndex < timeRangeComboBox.getItems().size()) {
            timeRangeComboBox.getSelectionModel().select(selectedRangeIndex);
        } else if (!timeRangeComboBox.getItems().isEmpty()) {
            timeRangeComboBox.getSelectionModel().select(0);
        }
    }

    /**
     * Updates the view by loading the selected chart and displaying it in the center panel.
     */
    @Override
    public void updateView(){
        menuBarFXMLController.updateView();
        menuDeckFXMLController.updateView();

        int selectedChartTypeIndex = chartTypeComboBox.getSelectionModel().getSelectedIndex();
        int selectedRangeIndex = timeRangeComboBox.getSelectionModel().getSelectedIndex();

        if (selectedChartTypeIndex >= 0 && selectedChartTypeIndex < chartTypeComboBox.getItems().size()
        && selectedRangeIndex >= 0 && selectedRangeIndex < timeRangeComboBox.getItems().size()) {
            Chart chart = chartService.getSelectedChart(selectedChartTypeIndex, selectedRangeIndex, deck);
            if(chart != null){
                chartContainer.getChildren().clear();
                chartContainer.getChildren().add(chart);
            }
        }
    }
}
