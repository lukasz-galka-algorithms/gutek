package gutek.domain.charts;

import gutek.domain.charts.charts.RevisedForTheFirstTimeChart;
import gutek.entities.decks.DeckBase;
import gutek.entities.decks.DeckBaseStatistics;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevisedForTheFirstTimeChartTest {

    private RevisedForTheFirstTimeChart revisedForTheFirstTimeChart;
    private DeckStatisticsService mockDeckStatisticsService;
    private TranslationService mockTranslationService;
    private DeckBase mockDeck;
    private DeckBaseStatistics mockStatistics;

    @BeforeEach
    void setUp() {
        mockDeckStatisticsService = mock(DeckStatisticsService.class);
        mockTranslationService = mock(TranslationService.class);
        mockDeck = mock(DeckBase.class);
        mockStatistics = mock(DeckBaseStatistics.class);

        when(mockDeck.getDeckBaseStatistics()).thenReturn(mockStatistics);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");

        revisedForTheFirstTimeChart = new RevisedForTheFirstTimeChart(mockDeckStatisticsService, mockTranslationService);
    }

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

    @Test
    void testGetChart_ValidData() {
        // Arrange
        int range = 5;
        long deckStatisticsId = 1L;
        int[] mockData = {1, 2, 3, 4, 5};

        when(mockStatistics.getIdDeckStatistics()).thenReturn(deckStatisticsId);
        when(mockDeckStatisticsService.getReviseForTheFirstTimeCounts(deckStatisticsId)).thenReturn(mockData);

        // Act
        Chart chart = revisedForTheFirstTimeChart.getChart(range, mockDeck, null);

        // Assert
        assertNotNull(chart);
        assertInstanceOf(BarChart.class, chart);

        BarChart<String, Number> barChart = (BarChart<String, Number>) chart;
        assertEquals(1, barChart.getData().size());

        XYChart.Series<String, Number> dataSeries = barChart.getData().get(0);
        assertEquals(range, dataSeries.getData().size());
        assertEquals(1, dataSeries.getData().get(4).getYValue());
        assertEquals(5, dataSeries.getData().get(0).getYValue());
    }

    @Test
    void testGetChartTitle() {
        // Act
        String chartTitle = revisedForTheFirstTimeChart.getChartTitle(mockDeck, null);

        // Assert
        assertNotNull(chartTitle);
        assertEquals("Translated", chartTitle);
        verify(mockTranslationService).getTranslation("deck_view.statistics.revised_first_time_title");
    }

    @Test
    void testIsRevisionStrategyIndependent() {
        // Act
        boolean isIndependent = revisedForTheFirstTimeChart.isRevisionStrategyIndependent();

        // Assert
        assertTrue(isIndependent);
    }
}
