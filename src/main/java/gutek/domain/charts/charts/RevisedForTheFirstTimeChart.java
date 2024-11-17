package gutek.domain.charts.charts;

import gutek.entities.decks.DeckBase;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import javafx.scene.chart.*;
import org.springframework.stereotype.Component;

/**
 * A chart component that displays the number of cards revised for the first time over a specified range of days.
 * This class generates a bar chart showing the distribution of cards revised for the first time
 * within a specified time range. It extends {@link StatisticsChart}, which provides basic chart functionalities.
 */
@Component
public class RevisedForTheFirstTimeChart extends StatisticsChart{

    /** Service for retrieving deck statistics. */
    private final DeckStatisticsService deckStatisticsService;

    /**
     * Constructs a new chart for displaying the number of cards revised for the first time over time.
     *
     * @param deckStatisticsService the service used for retrieving deck statistics
     * @param translationService the service used for retrieving translations
     */
    public RevisedForTheFirstTimeChart(DeckStatisticsService deckStatisticsService, TranslationService translationService) {
        super(translationService);
        this.deckStatisticsService = deckStatisticsService;
    }

    /**
     * Generates a bar chart displaying the number of cards revised for the first time over the specified range of days.
     *
     * @param range the number of days to display on the chart
     * @param deck the deck for which the chart is generated
     * @param revisionStrategyIndex unused parameter as this chart is revision strategy-independent
     * @return a {@link BarChart} representing the distribution of first-time card revisions over time
     */
    @Override
    public Chart getChart(int range, DeckBase deck, Integer revisionStrategyIndex) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(translationService.getTranslation("deck_view.statistics.day"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(translationService.getTranslation("deck_view.statistics.cards_number"));

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(getChartTitle(deck, revisionStrategyIndex));

        int[] revisedForTheFirstTime = deckStatisticsService.getReviseForTheFirstTimeCounts(deck.getDeckBaseStatistics().getIdDeckStatistics());
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(translationService.getTranslation("deck_view.statistics.cards_number"));

        for (int i = 0; i < range; i++) {
            dataSeries.getData().add(new XYChart.Data<>(String.valueOf(-(range - i - 1)), revisedForTheFirstTime[range - i - 1]));
        }

        barChart.getData().add(dataSeries);
        return barChart;
    }

    /**
     * Provides the title of the chart, localized to the current language setting.
     *
     * @param deck the deck for which the chart is generated
     * @param revisionStrategyIndex unused parameter as this chart is revision strategy-independent
     * @return the localized title of the chart
     */
    @Override
    public String getChartTitle(DeckBase deck, Integer revisionStrategyIndex) {
        return translationService.getTranslation("deck_view.statistics.revised_first_time_title");
    }

    /**
     * Indicates whether the chart is independent of the revision strategy.
     * <p>
     * For this chart, the data displayed does not depend on any specific revision strategy,
     * and the {@code revisionStrategyIndex} parameter is not used.
     * </p>
     *
     * @return {@code true} as this chart is revision strategy-independent
     */
    @Override
    public boolean isRevisionStrategyIndependent() {
        return true;
    }
}
