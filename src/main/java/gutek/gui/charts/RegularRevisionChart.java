package gutek.gui.charts;

import gutek.entities.decks.DeckBase;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import javafx.scene.chart.*;
import org.springframework.stereotype.Component;

/**
 * A chart component that displays the number of cards scheduled for regular revision over a specified range of days.
 *
 * This class generates a bar chart showing the distribution of cards scheduled for regular revision
 * within a specified time range. It extends {@link StatisticsChart}, which provides base chart functionalities.
 */
@Component
public class RegularRevisionChart extends StatisticsChart{

    /** Service for retrieving deck statistics. */
    private final DeckStatisticsService deckStatisticsService;

    /**
     * Constructs a new chart for displaying the number of cards scheduled for regular revision over time.
     *
     * @param deckStatisticsService the service used for retrieving deck statistics
     * @param translationService the service used for retrieving translations
     */
    public RegularRevisionChart(DeckStatisticsService deckStatisticsService, TranslationService translationService) {
        super(translationService);
        this.deckStatisticsService = deckStatisticsService;
    }

    /**
     * Creates a bar chart showing the number of cards scheduled for regular revision over a specified range of days.
     *
     * @param range the number of days to display in the chart
     * @param deck the deck for which the chart is generated
     * @return a {@link Chart} object representing the chart
     */
    @Override
    public Chart getChart(int range, DeckBase deck) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(translationService.getTranslation("deck_view.statistics.day"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(translationService.getTranslation("deck_view.statistics.cards_number"));

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(getChartTitle());

        int[] regularRevisionCounts = deckStatisticsService.getRegularRevisionCounts(deck.getDeckBaseStatistics().getIdDeckStatistics());
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(translationService.getTranslation("deck_view.statistics.cards_number"));

        for (int i = 0; i < range; i++) {
            dataSeries.getData().add(new XYChart.Data<>(String.valueOf(-(range - i - 1)), regularRevisionCounts[range - i - 1]));
        }

        barChart.getData().add(dataSeries);
        return barChart;
    }

    /**
     * Retrieves the title for the chart.
     *
     * @return the translated chart title
     */
    @Override
    public String getChartTitle() {
        return translationService.getTranslation("deck_view.statistics.regular_revision_title");
    }
}
