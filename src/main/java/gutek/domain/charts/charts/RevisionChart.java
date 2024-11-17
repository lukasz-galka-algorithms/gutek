package gutek.domain.charts.charts;

import gutek.entities.decks.DeckBase;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import javafx.scene.chart.*;
import org.springframework.stereotype.Component;

/**
 * A chart component that visualizes the number of cards revised over time for a specific revision strategy.
 * <p>
 * This class generates a bar chart to display revision statistics for a given strategy
 * within a specified range of days. It extends {@link StatisticsChart}, which provides core chart functionalities.
 * </p>
 */
@Component
public class RevisionChart extends StatisticsChart {

    /** Service for retrieving deck statistics. */
    private final DeckStatisticsService deckStatisticsService;

    /**
     * Constructs a new {@code RevisionChart}.
     *
     * @param deckStatisticsService the service used for retrieving deck statistics
     * @param translationService the service used for retrieving translations
     */
    public RevisionChart(DeckStatisticsService deckStatisticsService, TranslationService translationService) {
        super(translationService);
        this.deckStatisticsService = deckStatisticsService;
    }

    /**
     * Generates a bar chart displaying the number of cards revised over a specified range of days.
     *
     * @param range the number of days to display on the chart
     * @param deck the deck for which the chart is generated
     * @param revisionStrategyIndex the index of the revision strategy to visualize
     * @return a {@link BarChart} representing the revision counts for the given strategy
     * @throws IllegalArgumentException if {@code revisionStrategyIndex} is null
     */
    @Override
    public Chart getChart(int range, DeckBase deck, Integer revisionStrategyIndex) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(translationService.getTranslation("deck_view.statistics.day"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(translationService.getTranslation("deck_view.statistics.cards_number"));

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(getChartTitle(deck, revisionStrategyIndex));

        int[] revisionCounts = deck.getRevisionAlgorithm()
                .getAvailableRevisionStrategies()
                .get(revisionStrategyIndex)
                .getRevisionCounts(deckStatisticsService, deck);
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(translationService.getTranslation("deck_view.statistics.cards_number"));

        for (int i = 0; i < range; i++) {
            dataSeries.getData().add(new XYChart.Data<>(String.valueOf(-(range - i - 1)), revisionCounts[range - i - 1]));
        }

        barChart.getData().add(dataSeries);
        return barChart;
    }

    /**
     * Provides the title of the chart, localized to the current language setting.
     *
     * @param deck the deck for which the chart is generated
     * @param revisionStrategyIndex the index of the revision strategy to include in the title
     * @return the localized title of the chart
     * @throws IllegalArgumentException if {@code revisionStrategyIndex} is null
     */
    @Override
    public String getChartTitle(DeckBase deck, Integer revisionStrategyIndex) {
        if (revisionStrategyIndex == null) {
            throw new IllegalArgumentException("RevisionStrategy cannot be null");
        }
        String revisionStrategyTranslationKey = deck.getRevisionAlgorithm().getAvailableRevisionStrategies().get(revisionStrategyIndex).getRevisionStrategyTranslationKey();
        return translationService.getTranslation("revision." + revisionStrategyTranslationKey + ".statistics_revision_title");
    }

    /**
     * Indicates whether the chart is independent of the revision strategy.
     * <p>
     * This chart is dependent on a specific revision strategy, and the {@code revisionStrategyIndex} parameter
     * is required to generate the chart.
     * </p>
     *
     * @return {@code false} as this chart is revision strategy-dependent
     */
    @Override
    public boolean isRevisionStrategyIndependent() {
        return false;
    }
}
