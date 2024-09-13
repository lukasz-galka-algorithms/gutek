package gutek.gui.charts;

import gutek.entities.decks.DeckBase;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;

import java.awt.*;

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
     * @return a {@link JFreeChart} object representing the chart
     */
    @Override
    public JFreeChart getChart(int range, DeckBase deck) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int[] regularRevisionCounts = deckStatisticsService.getRegularRevisionCounts(deck.getDeckBaseStatistics().getIdDeckStatistics());
        String rowKey = translationService.getTranslation("deck_view.statistics.cards_number");

        for (int i = 0; i < range; i++) {
            dataset.addValue(regularRevisionCounts[range - i - 1], rowKey, String.valueOf(-(range - i - 1)));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                getChartTitle(),
                translationService.getTranslation("deck_view.statistics.day"),
                translationService.getTranslation("deck_view.statistics.cards_number"),
                dataset, PlotOrientation.VERTICAL,
                false, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        CategoryAxis domainAxis = plot.getDomainAxis();
        int step = Math.max(1, range / 30);

        domainAxis.setTickLabelFont(new Font("Serif", Font.PLAIN, 10));
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelsVisible(true);

        for (int i = 0; i < dataset.getColumnCount(); i++) {
            if (i % step != 0) {
                domainAxis.setTickLabelPaint(dataset.getColumnKey(i), new Color(0, 0, 0, 0));
            }
        }

        return chart;
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
