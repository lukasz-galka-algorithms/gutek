package gutek.gui.charts;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.services.DeckService;
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
import java.time.LocalDate;
import java.util.List;

/**
 * A chart component that displays the number of new cards added per day over a specified range.
 *
 * This class uses the JFreeChart library to create a bar chart, showing the distribution of newly
 * added cards for a specific deck over a given period. It extends {@link StatisticsChart}, which
 * provides basic functionality for handling chart translations and titles.
 */
@Component
public class AddedNewChart extends StatisticsChart{
    /** Service for retrieving deck and card data. */
    private final DeckService deckService;
    /**
     * Constructs a new chart for displaying the number of newly added cards per day.
     *
     * @param translationService the service used for retrieving translations
     * @param deckService the service used for interacting with deck and card data
     */
    public AddedNewChart(TranslationService translationService, DeckService deckService) {
        super(translationService);
        this.deckService = deckService;
    }
    /**
     * Creates a bar chart showing the number of new cards added per day over a specified range of days.
     *
     * @param range the number of days to display in the chart
     * @param deck the deck for which the chart is generated
     * @return a {@link JFreeChart} object representing the chart
     */
    @Override
    public JFreeChart getChart(int range, DeckBase deck) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int[] addedNewCardsPerDay = countAddedNewCardsPerDay(range,deck);
        String rowKey = translationService.getTranslation("deck_view.statistics.cards_number");
        for (int i = addedNewCardsPerDay.length - 1 ; i >=0 ; i--) {
            dataset.addValue(addedNewCardsPerDay[i], rowKey, String.valueOf(-i));
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
     * Counts the number of new cards added each day over the given range for the specified deck.
     *
     * @param range the number of days to check
     * @param deck the deck for which the count is being performed
     * @return an array where each index represents the number of new cards added on a specific day
     */
    private int[] countAddedNewCardsPerDay(int range, DeckBase deck) {
        int[] cardsPerDay = new int[range];

        List<CardBase> allCards = deckService.getAllCards(deck);
        LocalDate today = LocalDate.now();

        for (CardBase card : allCards) {
            LocalDate creationDate = card.getCreationTime().toLocalDate();
            if (!creationDate.isBefore(today.minusDays(range))) {
                int daysAgo = creationDate.until(today).getDays();
                if (daysAgo >= 0 && daysAgo < range) {
                    cardsPerDay[daysAgo]++;
                }
            }
        }
        return cardsPerDay;
    }
    /**
     * Retrieves the title for the chart.
     *
     * @return the translated chart title
     */
    @Override
    public String getChartTitle() {
        return translationService.getTranslation("deck_view.statistics.added_new_title");
    }
}
