package gutek.domain.charts.charts;

import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import javafx.scene.chart.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * A chart component that displays the distribution of cards by their next revision date
 * over a specified range of days for a specific revision strategy.
 * <p>
 * This class generates a bar chart that visualizes how many cards are scheduled for revision
 * on each day in the given range. It extends {@link StatisticsChart}, inheriting core chart
 * generation and translation functionality.
 * </p>
 */
@Component
public class AppearanceTimeRevisionChart extends StatisticsChart {

    /** Service for retrieving deck and card data. */
    private final DeckService deckService;

    /**
     * Constructs a new chart for displaying the distribution of cards by their next revision date.
     *
     * @param translationService the service used for retrieving translations
     * @param deckService the service used for retrieving deck and card information
     */
    public AppearanceTimeRevisionChart(TranslationService translationService, DeckService deckService) {
        super(translationService);
        this.deckService = deckService;
    }

    /**
     * Generates a bar chart showing the distribution of cards scheduled for revision over time
     * for a specific revision strategy.
     *
     * @param range the number of days to display on the chart
     * @param deck the deck for which the chart is generated
     * @param revisionStrategyIndex the index of the revision strategy to consider for the chart
     * @return a {@link BarChart} representing the distribution of revision cards by day
     */
    @Override
    public Chart getChart(int range, DeckBase deck, Integer revisionStrategyIndex) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(translationService.getTranslation("deck_view.statistics.day"));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(translationService.getTranslation("deck_view.statistics.cards_number"));

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(getChartTitle(deck, revisionStrategyIndex));

        int[] revisionCardsPerDay = countRevisionCardsPerDay(range, deck, revisionStrategyIndex);
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(translationService.getTranslation("deck_view.statistics.cards_number"));

        for (int i = 0; i < revisionCardsPerDay.length; i++) {
            dataSeries.getData().add(new XYChart.Data<>(String.valueOf(i), revisionCardsPerDay[i]));
        }

        barChart.getData().add(dataSeries);
        return barChart;
    }

    /**
     * Counts the number of cards scheduled for revision each day over the given range.
     *
     * @param range the number of days to check
     * @param deck the deck for which the count is being performed
     * @param revisionStrategyIndex the index of the revision strategy to consider
     * @param <T> the type of the cards being revised, extending {@link CardBase}
     * @return an array where each index represents the number of cards scheduled for revision on a specific day
     */
    @SuppressWarnings("unchecked")
    private <T extends CardBase> int[] countRevisionCardsPerDay(int range, DeckBase deck, Integer revisionStrategyIndex) {
        int[] cardsPerDay = new int[range];

        RevisionAlgorithm<T> revisionAlgorithm = (RevisionAlgorithm<T>) deck.getRevisionAlgorithm();
        RevisionStrategy<T> revisionStrategy = revisionAlgorithm.getAvailableRevisionStrategies().get(revisionStrategyIndex);
        List<T> allCards = (List<T>) deckService.getAllCards(deck);
        LocalDate today = LocalDate.now();

        for (T card : allCards) {
            if (!card.isNewCard()) {
                LocalDate revisionDate = revisionStrategy.getNextRevisionDate(card);

                if (revisionDate.isBefore(today)) {
                    cardsPerDay[0]++;
                } else {
                    int daysUntilRevision = today.until(revisionDate).getDays();
                    if (daysUntilRevision >= 0 && daysUntilRevision < range) {
                        cardsPerDay[daysUntilRevision]++;
                    }
                }
            }
        }

        return cardsPerDay;
    }

    /**
     * Provides the title of the chart, localized to the current language setting.
     *
     * @param deck the deck for which the chart is generated
     * @param revisionStrategyIndex the index of the revision strategy being considered
     * @return the localized title of the chart
     * @throws IllegalArgumentException if {@code revisionStrategyIndex} is null
     */
    @Override
    public String getChartTitle(DeckBase deck, Integer revisionStrategyIndex) {
        if (revisionStrategyIndex == null) {
            throw new IllegalArgumentException("RevisionStrategy cannot be null");
        }
        String revisionStrategyTranslationKey = deck.getRevisionAlgorithm().getAvailableRevisionStrategies().get(revisionStrategyIndex).getRevisionStrategyTranslationKey();
        return translationService.getTranslation("revision." + revisionStrategyTranslationKey + ".statistics_appearance_title");
    }

    /**
     * Indicates whether the chart is independent of the revision strategy.
     * <p>
     * This chart depends on a specific revision strategy and uses the {@code revisionStrategyIndex}
     * parameter to retrieve relevant data.
     * </p>
     *
     * @return {@code false} as this chart is revision strategy-dependent
     */
    @Override
    public boolean isRevisionStrategyIndependent() {
        return false;
    }
}