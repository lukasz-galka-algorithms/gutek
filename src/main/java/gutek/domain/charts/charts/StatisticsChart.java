package gutek.domain.charts.charts;

import gutek.entities.decks.DeckBase;
import gutek.services.TranslationService;
import javafx.scene.chart.Chart;
import lombok.AllArgsConstructor;

/**
 * An abstract class representing a statistics chart for decks.
 * This class provides the structure for creating different types of charts,
 * such as charts for tracking card revisions or other statistics related to a deck.
 * It requires implementing classes to define how the chart is generated and to provide a chart title.
 */
@AllArgsConstructor
public abstract class StatisticsChart {

    /** Service for retrieving translations, used to localize chart labels and titles. */
    protected final TranslationService translationService;

    /**
     * Generates and returns a chart for the given range, deck, and revision strategy.
     *
     * @param range                The range of data points to include in the chart.
     * @param deck                 The deck for which the chart is generated.
     * @param revisionStrategyIndex The index of the revision strategy to consider for this chart,
     *                              if the chart is revision strategy-dependent.
     *                              Otherwise, this parameter is not used.
     * @return The generated chart object.
     */
    public abstract Chart getChart(int range, DeckBase deck, Integer revisionStrategyIndex);

    /**
     * Provides the title of the chart.
     *
     * @param deck                 The deck for which the chart is generated.
     * @param revisionStrategyIndex The index of the revision strategy to consider for this chart,
     *                              if the chart is revision strategy-dependent.
     *                              Otherwise, this parameter is not used.
     * @return The title of the chart as a localized string.
     */
    public abstract String getChartTitle(DeckBase deck, Integer revisionStrategyIndex);

    /**
     * Indicates whether the chart is independent of the revision strategy.
     *
     * @return {@code true} if the chart is revision strategy-independent; {@code false} otherwise
     */
    public abstract boolean isRevisionStrategyIndependent();
}
