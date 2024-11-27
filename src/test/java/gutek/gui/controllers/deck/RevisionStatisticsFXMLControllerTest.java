package gutek.gui.controllers.deck;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.ChartService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RevisionStatisticsFXMLControllerTest extends ApplicationTest {
    private RevisionStatisticsFXMLController controller;
    private TranslationService mockTranslationService;
    private ChartService mockChartService;

    private DeckBase mockDeck;

    @BeforeAll
    static void initToolkit() {
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.startup(() -> {
                });
            }
        } catch (IllegalStateException e) {
            // ignore
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);
        mockTranslationService = mock(TranslationService.class);
        MenuBarFXMLController mockMenuBarController = mock(MenuBarFXMLController.class);
        MenuDeckFXMLController mockMenuDeckController = mock(MenuDeckFXMLController.class);
        mockDeck = mock(DeckBase.class);
        RevisionAlgorithm mockAlgorithm = mock(RevisionAlgorithm.class);
        mockChartService = mock(ChartService.class);
        MainStage mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockDeck.getRevisionAlgorithm()).thenReturn(mockAlgorithm);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");
        when(mockMenuBarController.getRoot()).thenReturn(new Label("MenuBar"));
        when(mockMenuDeckController.getRoot()).thenReturn(new Label("MenuDeck"));
        when(mockChartService.getDeckCompatibleAvailableChartsTitles(mockDeck)).thenReturn(
                new String[] { "Bar Chart", "Pie Chart" });
        when(mockChartService.getAvailableRanges()).thenReturn(
                new String[] { "Last Week", "Last Month" });

        controller = new RevisionStatisticsFXMLController(
                mockStage,
                mockFxmlFileLoader,
                mockTranslationService,
                mockMenuBarController,
                mockMenuDeckController,
                mockChartService
        );

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deck/RevisionStatisticsView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/deck/RevisionStatisticsView.fxml"), any()))
                .thenReturn(mockRoot);
        controller.loadViewFromFXML();

        Platform.runLater(() -> {
            Stage stage = new Stage();
            when(mockStage.getStage()).thenReturn(stage);
            stage.setScene(new Scene(controller.getRoot()));
            stage.show();

            WaitForAsyncUtils.waitForFxEvents();
            controller.initWithParams(mockDeck);
        });
    }

    @Test
    void testInitWithParams() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.initWithParams(mockDeck));
        WaitForAsyncUtils.waitForFxEvents();

        ComboBox<String> chartTypeComboBox = lookup("#chartTypeComboBox").queryAs(ComboBox.class);
        ComboBox<String> timeRangeComboBox = lookup("#timeRangeComboBox").queryAs(ComboBox.class);

        // Assert
        assertNotNull(chartTypeComboBox, "chartTypeComboBox should not be null");
        assertNotNull(timeRangeComboBox, "timeRangeComboBox should not be null");

        assertEquals(2, chartTypeComboBox.getItems().size(), "Chart types should be initialized");
        assertEquals("Bar Chart", chartTypeComboBox.getItems().get(0));
        assertEquals("Pie Chart", chartTypeComboBox.getItems().get(1));

        assertEquals(2, timeRangeComboBox.getItems().size(), "Time ranges should be initialized");
        assertEquals("Last Week", timeRangeComboBox.getItems().get(0));
        assertEquals("Last Month", timeRangeComboBox.getItems().get(1));
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Label chartTypeLabel = lookup("#chartTypeLabel").queryAs(Label.class);
        Label chartRangeLabel = lookup("#chartRangeLabel").queryAs(Label.class);

        // Assert
        assertNotNull(chartTypeLabel, "chartTypeLabel should not be null");
        assertNotNull(chartRangeLabel, "chartRangeLabel should not be null");

        assertNotNull(chartTypeLabel.getStyle(), "ChartType label style should be updated");
        assertNotNull(chartRangeLabel.getStyle(), "ChartRange label style should be updated");
        assertTrue(chartTypeLabel.getStyle().contains("-fx-font-size:"), "ChartType label should have font size updated");
        assertTrue(chartRangeLabel.getStyle().contains("-fx-font-size:"), "ChartRange label should have font size updated");
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        when(mockTranslationService.getTranslation("deck_view.statistics.chart_type")).thenReturn("Chart Type");
        when(mockTranslationService.getTranslation("deck_view.statistics.chart_range")).thenReturn("Chart Range");

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        Label chartTypeLabel = lookup("#chartTypeLabel").queryAs(Label.class);
        Label chartRangeLabel = lookup("#chartRangeLabel").queryAs(Label.class);

        // Assert
        assertNotNull(chartTypeLabel, "chartTypeLabel should not be null");
        assertNotNull(chartRangeLabel, "chartRangeLabel should not be null");

        assertEquals("Chart Type", chartTypeLabel.getText(), "Chart type label should be translated");
        assertEquals("Chart Range", chartRangeLabel.getText(), "Chart range label should be translated");
    }

    @Test
    void testUpdateViewWithValidSelection() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        LineChart<Number, Number> lineChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        when(mockChartService.getDeckCompatibleSelectedChart(0, 0, mockDeck))
                .thenReturn(lineChart);

        // Act
        Platform.runLater(() -> {
            ComboBox<String> chartTypeComboBox = lookup("#chartTypeComboBox").queryAs(ComboBox.class);
            ComboBox<String> timeRangeComboBox = lookup("#timeRangeComboBox").queryAs(ComboBox.class);

            chartTypeComboBox.getSelectionModel().select(0);
            timeRangeComboBox.getSelectionModel().select(0);

            controller.updateView();
        });
        WaitForAsyncUtils.waitForFxEvents();

        Parent chartContainer = lookup("#chartContainer").query();
        // Assert
        assertNotNull(chartContainer, "chartContainer should not be null");
        assertEquals(1, chartContainer.getChildrenUnmodifiable().size(), "Chart container should have one chart");
        assertEquals(lineChart, chartContainer.getChildrenUnmodifiable().getFirst(), "Chart container should display the correct chart");
    }

    @Test
    void testUpdateViewWithInvalidSelection() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        when(mockChartService.getDeckCompatibleSelectedChart(0, 0, mockDeck)).thenReturn(null);

        // Act
        Platform.runLater(() -> {
            ComboBox<String> chartTypeComboBox = lookup("#chartTypeComboBox").queryAs(ComboBox.class);
            ComboBox<String> timeRangeComboBox = lookup("#timeRangeComboBox").queryAs(ComboBox.class);

            chartTypeComboBox.getSelectionModel().select(0);
            timeRangeComboBox.getSelectionModel().select(0);

            controller.updateView();
        });
        WaitForAsyncUtils.waitForFxEvents();

        Parent chartContainer = lookup("#chartContainer").query();
        // Assert
        assertNotNull(chartContainer, "chartContainer should not be null");
        assertTrue(chartContainer.getChildrenUnmodifiable().isEmpty(), "Chart container should be empty when no chart is available");
    }
}
