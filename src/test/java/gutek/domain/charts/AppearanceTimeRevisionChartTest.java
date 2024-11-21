package gutek.domain.charts;

import gutek.domain.charts.charts.AppearanceTimeRevisionChart;
import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppearanceTimeRevisionChartTest {

    private AppearanceTimeRevisionChart appearanceTimeRevisionChart;
    private DeckService mockDeckService;
    private TranslationService mockTranslationService;
    private DeckBase mockDeck;
    private RevisionAlgorithm mockAlgorithm;
    private RevisionStrategy mockStrategy;

    @BeforeEach
    void setUp() {
        mockDeckService = mock(DeckService.class);
        mockTranslationService = mock(TranslationService.class);
        mockDeck = mock(DeckBase.class);
        mockAlgorithm = mock(RevisionAlgorithm.class);
        mockStrategy = mock(RevisionStrategy.class);

        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");

        appearanceTimeRevisionChart = new AppearanceTimeRevisionChart(mockTranslationService, mockDeckService);
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
        int revisionStrategyIndex = 0;
        LocalDate today = LocalDate.now();

        CardBase mockCard1 = mock(CardBase.class);
        when(mockCard1.isNewCard()).thenReturn(false);
        when(mockCard1.getNextRegularRevisionDate()).thenReturn(today.plusDays(1));

        CardBase mockCard2 = mock(CardBase.class);
        when(mockCard2.isNewCard()).thenReturn(false);
        when(mockCard2.getNextRegularRevisionDate()).thenReturn(today.plusDays(3));

        when(mockDeck.getRevisionAlgorithm()).thenReturn(mockAlgorithm);
        when(mockAlgorithm.getAvailableRevisionStrategies()).thenReturn(List.of(mockStrategy));
        when(mockStrategy.getNextRevisionDate(any(CardBase.class))).thenAnswer(invocation -> {
            CardBase card = invocation.getArgument(0);
            return card.getNextRegularRevisionDate();
        });
        when(mockDeckService.getAllCards(mockDeck)).thenReturn(List.of(mockCard1, mockCard2));

        // Act
        Chart chart = appearanceTimeRevisionChart.getChart(range, mockDeck, revisionStrategyIndex);

        // Assert
        assertNotNull(chart);
        assertInstanceOf(BarChart.class, chart);

        BarChart<String, Number> barChart = (BarChart<String, Number>) chart;
        assertEquals(1, barChart.getData().size());

        XYChart.Series<String, Number> dataSeries = barChart.getData().get(0);
        assertEquals(range, dataSeries.getData().size());
        assertEquals(0, dataSeries.getData().get(0).getYValue()); // Day 0
        assertEquals(1, dataSeries.getData().get(1).getYValue()); // Day 1
        assertEquals(0, dataSeries.getData().get(2).getYValue()); // Day 2
        assertEquals(1, dataSeries.getData().get(3).getYValue()); // Day 3
    }

    @Test
    void testGetChartTitle_ValidIndex() {
        // Arrange
        int revisionStrategyIndex = 0;
        when(mockDeck.getRevisionAlgorithm()).thenReturn(mockAlgorithm);
        when(mockAlgorithm.getAvailableRevisionStrategies()).thenReturn(List.of(mockStrategy));
        when(mockStrategy.getRevisionStrategyTranslationKey()).thenReturn("test_key");

        // Act
        String chartTitle = appearanceTimeRevisionChart.getChartTitle(mockDeck, revisionStrategyIndex);

        // Assert
        assertNotNull(chartTitle);
        assertEquals("Translated", chartTitle);
        verify(mockTranslationService).getTranslation("revision.test_key.statistics_appearance_title");
    }

    @Test
    void testGetChartTitle_NullIndex() {
        // Arrange
        Integer revisionStrategyIndex = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> appearanceTimeRevisionChart.getChartTitle(mockDeck, revisionStrategyIndex));
    }

    @Test
    void testIsRevisionStrategyIndependent() {
        // Act
        boolean isIndependent = appearanceTimeRevisionChart.isRevisionStrategyIndependent();

        // Assert
        assertFalse(isIndependent);
    }
}
