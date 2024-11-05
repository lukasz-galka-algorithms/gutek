package gutek.gui.charts;

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
     * Generates the chart based on the provided deck and the specified range of days.
     *
     * @param range the number of days to display in the chart
     * @param deck the deck for which the chart is generated
     * @return a {@link Chart} object representing the generated chart
     */
    public abstract Chart getChart(int range, DeckBase deck);

    /**
     * Retrieves the title of the chart.
     *
     * @return the translated title of the chart
     */
    public abstract String getChartTitle();
}
