package gutek.gui.charts;

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
     * Creates a bar chart showing the number of new cards added per day over a specified range of days.
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
     * Retrieves the title for the chart.
     *
     * @return the translated chart title
     */
    @Override
    public String getChartTitle() {
        return translationService.getTranslation("deck_view.statistics.added_new_title");
    }
}
