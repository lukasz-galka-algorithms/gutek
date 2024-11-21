package gutek.domain.charts;

import gutek.domain.charts.charts.RevisionChart;
import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevisionChartTest {

    private RevisionChart revisionChart;
    private DeckStatisticsService mockDeckStatisticsService;
    private TranslationService mockTranslationService;
    private DeckBase mockDeck;
    private RevisionAlgorithm mockAlgorithm;

    @BeforeEach
    void setUp() {
        mockDeckStatisticsService = mock(DeckStatisticsService.class);
        mockTranslationService = mock(TranslationService.class);
        mockDeck = mock(DeckBase.class);
        mockAlgorithm = mock(RevisionAlgorithm.class);

        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");

        revisionChart = new RevisionChart(mockDeckStatisticsService, mockTranslationService);
    }

    @BeforeAll
    static void initToolkit() {
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.startup(() -> {});
            }
        } catch (IllegalStateException e) {
            // ignore
        }
    }

    @Test
    void testGetChart_ValidData() {
        // Arrange
        int range = 5;
        int revisionStrategyIndex = 0;

        RevisionStrategy mockStrategy = mock(RevisionStrategy.class);
        when(mockDeck.getRevisionAlgorithm()).thenReturn(mockAlgorithm);
        when(mockAlgorithm.getAvailableRevisionStrategies()).thenReturn(List.of(mockStrategy));
        when(mockStrategy.getRevisionCounts(mockDeckStatisticsService, mockDeck)).thenReturn(new int[]{1, 2, 3, 4, 5});

        // Act
        Chart chart = revisionChart.getChart(range, mockDeck, revisionStrategyIndex);

        // Assert
        assertNotNull(chart);
        assertInstanceOf(BarChart.class, chart);

        BarChart<String, Number> barChart = (BarChart<String, Number>) chart;
        assertEquals(1, barChart.getData().size());

        XYChart.Series<String, Number> dataSeries = barChart.getData().get(0);
        assertEquals(range, dataSeries.getData().size());
    }

    @Test
    void testGetChartTitle_ValidIndex() {
        // Arrange
        int revisionStrategyIndex = 0;

        RevisionStrategy<CardBase> mockStrategy = mock(RevisionStrategy.class);
        when(mockDeck.getRevisionAlgorithm()).thenReturn(mockAlgorithm);
        when(mockAlgorithm.getAvailableRevisionStrategies()).thenReturn(List.of(mockStrategy));
        when(mockStrategy.getRevisionStrategyTranslationKey()).thenReturn("test_key");

        // Act
        String chartTitle = revisionChart.getChartTitle(mockDeck, revisionStrategyIndex);

        // Assert
        assertNotNull(chartTitle);
        assertEquals("Translated", chartTitle);
        verify(mockTranslationService).getTranslation("revision.test_key.statistics_revision_title");
    }

    @Test
    void testGetChartTitle_NullIndex() {
        // Arrange
        Integer revisionStrategyIndex = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> revisionChart.getChartTitle(mockDeck, revisionStrategyIndex));
    }

    @Test
    void testIsRevisionStrategyIndependent() {
        // Act
        boolean isIndependent = revisionChart.isRevisionStrategyIndependent();

        // Assert
        assertFalse(isIndependent);
    }
}
