package gutek.domain.charts;

import gutek.domain.charts.charts.StatisticsChart;
import gutek.entities.decks.DeckBase;
import gutek.services.TranslationService;
import javafx.application.Platform;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsChartTest {

    private StatisticsChart statisticsChart;
    private TranslationService mockTranslationService;
    private DeckBase mockDeck;

    @BeforeEach
    void setUp() {
        mockTranslationService = mock(TranslationService.class);
        mockDeck = mock(DeckBase.class);

        statisticsChart = new StatisticsChart(mockTranslationService) {
            @Override
            public Chart getChart(int range, DeckBase deck, Integer revisionStrategyIndex) {
                return new LineChart<>(new NumberAxis(), new NumberAxis());
            }

            @Override
            public String getChartTitle(DeckBase deck, Integer revisionStrategyIndex) {
                return "Mock Chart Title";
            }

            @Override
            public boolean isRevisionStrategyIndependent() {
                return true;
            }
        };
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
    void testGetChart() {
        // Arrange
        int range = 10;
        Integer revisionStrategyIndex = null;

        // Act
        Chart chart = statisticsChart.getChart(range, mockDeck, revisionStrategyIndex);

        // Assert
        assertNotNull(chart);
        assertInstanceOf(LineChart.class, chart);
    }

    @Test
    void testGetChartTitle() {
        // Arrange
        Integer revisionStrategyIndex = null;

        // Act
        String chartTitle = statisticsChart.getChartTitle(mockDeck, revisionStrategyIndex);

        // Assert
        assertEquals("Mock Chart Title", chartTitle);
    }

    @Test
    void testIsRevisionStrategyIndependent() {
        // Act
        boolean isIndependent = statisticsChart.isRevisionStrategyIndependent();

        // Assert
        assertTrue(isIndependent);
    }
}
