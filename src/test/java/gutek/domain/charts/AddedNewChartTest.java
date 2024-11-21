package gutek.domain.charts;

import gutek.domain.charts.charts.AddedNewChart;
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

class AddedNewChartTest {

    private AddedNewChart addedNewChart;
    private TranslationService mockTranslationService;
    private DeckService mockDeckService;
    private DeckBase mockDeck;

    @BeforeEach
    void setUp() {
        mockTranslationService = mock(TranslationService.class);
        mockDeckService = mock(DeckService.class);
        mockDeck = mock(DeckBase.class);

        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");

        addedNewChart = new AddedNewChart(mockTranslationService, mockDeckService);
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
        LocalDate today = LocalDate.now();

        CardBase mockCard1 = mock(CardBase.class);
        when(mockCard1.getCreationTime()).thenReturn(today.minusDays(2).atStartOfDay());

        CardBase mockCard2 = mock(CardBase.class);
        when(mockCard2.getCreationTime()).thenReturn(today.minusDays(3).atStartOfDay());

        when(mockDeckService.getAllCards(mockDeck)).thenReturn(List.of(mockCard1, mockCard2));

        // Act
        Chart chart = addedNewChart.getChart(range, mockDeck, null);

        // Assert
        assertNotNull(chart);
        assertInstanceOf(BarChart.class, chart);

        BarChart<String, Number> barChart = (BarChart<String, Number>) chart;
        assertEquals(1, barChart.getData().size());

        XYChart.Series<String, Number> dataSeries = barChart.getData().get(0);
        assertEquals(range, dataSeries.getData().size());
        assertEquals(1, dataSeries.getData().get(2).getYValue());
    }

    @Test
    void testGetChart_EmptyData() {
        // Arrange
        int range = 5;
        when(mockDeckService.getAllCards(mockDeck)).thenReturn(List.of());

        // Act
        Chart chart = addedNewChart.getChart(range, mockDeck, null);

        // Assert
        assertNotNull(chart);
        assertInstanceOf(BarChart.class, chart);

        BarChart<String, Number> barChart = (BarChart<String, Number>) chart;
        assertEquals(1, barChart.getData().size());

        XYChart.Series<String, Number> dataSeries = barChart.getData().get(0);
        assertEquals(range, dataSeries.getData().size());
        for (XYChart.Data<String, Number> data : dataSeries.getData()) {
            assertEquals(0, data.getYValue());
        }
    }

    @Test
    void testGetChartTitle() {
        // Act
        String title = addedNewChart.getChartTitle(mockDeck, null);

        // Assert
        assertNotNull(title);
        assertEquals("Translated", title);
        verify(mockTranslationService).getTranslation("deck_view.statistics.added_new_title");
    }

    @Test
    void testIsRevisionStrategyIndependent() {
        // Act
        boolean isIndependent = addedNewChart.isRevisionStrategyIndependent();

        // Assert
        assertTrue(isIndependent);
    }
}
