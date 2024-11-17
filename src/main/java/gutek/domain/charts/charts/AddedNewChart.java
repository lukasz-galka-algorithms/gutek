package gutek.domain.charts.charts;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import javafx.scene.chart.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

/**
 * A chart component that displays the number of new cards added per day over a specified range.
 * This class uses the JavaFX library to create a bar chart, showing the distribution of newly
 * added cards for a specific deck over a given period. It extends {@link StatisticsChart}, which
 * provides basic functionality for handling chart translations and titles.
 */
@Component
public class AddedNewChart extends StatisticsChart {

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
     * Generates a bar chart showing the number of newly added cards per day over the specified range.
     *
     * @param range the number of days to display on the chart
     * @param deck the deck for which the chart is generated
     * @param revisionStrategyIndex unused parameter as this chart is revision strategy-independent
     * @return a {@link BarChart} displaying the daily count of newly added cards
     */
    @Override
    public Chart getChart(int range, DeckBase deck, Integer revisionStrategyIndex) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(translationService.getTranslation("deck_view.statistics.day"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(translationService.getTranslation("deck_view.statistics.cards_number"));

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(getChartTitle(deck, revisionStrategyIndex));
        int[] addedNewCardsPerDay = countAddedNewCardsPerDay(range, deck);
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(translationService.getTranslation("deck_view.statistics.cards_number"));

        for (int i = addedNewCardsPerDay.length - 1; i >= 0; i--) {
            dataSeries.getData().add(new XYChart.Data<>(String.valueOf(-i), addedNewCardsPerDay[i]));
        }
        barChart.getData().add(dataSeries);
        return barChart;
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
     * Provides the title of the chart, localized to the current language setting.
     *
     * @param deck the deck for which the chart is generated
     * @param revisionStrategyIndex unused parameter as this chart is revision strategy-independent
     * @return the localized title of the chart
     */
    @Override
    public String getChartTitle(DeckBase deck, Integer revisionStrategyIndex) {
        return translationService.getTranslation("deck_view.statistics.added_new_title");
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
