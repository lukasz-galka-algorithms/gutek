package gutek.services;

import gutek.domain.charts.charts.StatisticsChart;
import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import javafx.scene.chart.Chart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ChartServiceTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private StatisticsChart mockIndependentChart;

    @Mock
    private StatisticsChart mockDependentChart;

    @Mock
    private DeckBase mockDeck;

    @Mock
    private RevisionAlgorithm<?> mockRevisionAlgorithm;

    @Mock
    private RevisionStrategy<?> mockRevisionStrategy;

    private ChartService chartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        List<StatisticsChart> availableCharts = Arrays.asList(mockIndependentChart, mockDependentChart);
        chartService = new ChartService(translationService, availableCharts);
    }

    @Test
    void testGetAvailableRanges() {
        // Arrange
        String translatedRangeString = "Translated Range";
        when(translationService.getTranslation(anyString())).thenReturn(translatedRangeString);

        // Act
        String[] ranges = chartService.getAvailableRanges();

        // Assert
        assertNotNull(ranges);
        for (String range : ranges) {
            assertEquals(translatedRangeString, range);
        }
    }

    @Test
    void testGetDeckCompatibleAvailableChartsTitles() {
        // Arrange
        String independentChartTitle = "Independent Chart";
        String dependentChartTitle = "Dependent Chart";
        when(mockDeck.getRevisionAlgorithm()).thenReturn((RevisionAlgorithm) mockRevisionAlgorithm);
        when(mockRevisionAlgorithm.getAvailableRevisionStrategies()).thenReturn(List.of((RevisionStrategy) mockRevisionStrategy));
        when(mockIndependentChart.isRevisionStrategyIndependent()).thenReturn(true);
        when(mockIndependentChart.getChartTitle(mockDeck, null)).thenReturn(independentChartTitle);
        when(mockDependentChart.isRevisionStrategyIndependent()).thenReturn(false);
        when(mockDependentChart.getChartTitle(mockDeck, 0)).thenReturn(dependentChartTitle);

        // Act
        String[] titles = chartService.getDeckCompatibleAvailableChartsTitles(mockDeck);

        // Assert
        assertArrayEquals(new String[]{independentChartTitle, dependentChartTitle}, titles);
    }

    @Test
    void testGetDeckCompatibleSelectedChart() {
        // Arrange
        when(mockDeck.getRevisionAlgorithm()).thenReturn( (RevisionAlgorithm) mockRevisionAlgorithm);
        when(mockRevisionAlgorithm.getAvailableRevisionStrategies()).thenReturn(List.of( (RevisionStrategy) mockRevisionStrategy));
        when(mockIndependentChart.isRevisionStrategyIndependent()).thenReturn(true);
        when(mockIndependentChart.getChart(anyInt(), eq(mockDeck), isNull()))
                .thenReturn(mock(Chart.class));
        when(mockDependentChart.isRevisionStrategyIndependent()).thenReturn(false);
        when(mockDependentChart.getChart(anyInt(), eq(mockDeck), eq(0)))
                .thenReturn(mock(Chart.class));

        // Act
        Chart selectedChart1 = chartService.getDeckCompatibleSelectedChart(0, 0, mockDeck);
        Chart selectedChart2 = chartService.getDeckCompatibleSelectedChart(1, 0, mockDeck);

        // Assert
        assertNotNull(selectedChart1);
        verify(mockIndependentChart, times(1)).getChart(eq(ChartService.getAVAILABLE_RANGES()[0]), eq(mockDeck), isNull());
        assertNotNull(selectedChart2);
        verify(mockDependentChart, times(1)).getChart(ChartService.getAVAILABLE_RANGES()[0], mockDeck, 0);
    }
}