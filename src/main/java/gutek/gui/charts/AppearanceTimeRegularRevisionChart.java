package gutek.gui.charts;

import gutek.domain.revisions.AvailableRevisions;
import gutek.domain.revisions.RegularTextModeRevision;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import javafx.scene.chart.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

/**
 * A chart component that displays the number of cards scheduled for regular revision over a specified range of days.
 * This class generates a bar chart showing when cards will appear for regular revision within a specified period.
 * It extends {@link StatisticsChart}, which provides basic functionality for handling chart titles and translations.
 */
@Component
public class AppearanceTimeRegularRevisionChart extends StatisticsChart {

    /** Service for retrieving deck and card data. */
    private final DeckService deckService;

    /**
     * Constructs a new chart for displaying the number of cards scheduled for regular revision over time.
     *
     * @param translationService the service used for retrieving translations
     * @param deckService the service used for interacting with deck and card data
     */
    public AppearanceTimeRegularRevisionChart(TranslationService translationService, DeckService deckService) {
        super(translationService);
        this.deckService = deckService;
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

        int[] regularRevisionCardsPerDay = countRegularRevisionCardsPerDay(range, deck);
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(translationService.getTranslation("deck_view.statistics.cards_number"));

        for (int i = 0; i < regularRevisionCardsPerDay.length; i++) {
            dataSeries.getData().add(new XYChart.Data<>(String.valueOf(i), regularRevisionCardsPerDay[i]));
        }

        barChart.getData().add(dataSeries);
        return barChart;
    }

    /**
     * Counts the number of cards scheduled for regular revision each day over the given range for the specified deck.
     *
     * @param range the number of days to check
     * @param deck the deck for which the count is being performed
     * @return an array where each index represents the number of cards scheduled for revision on a specific day
     */
    private int[] countRegularRevisionCardsPerDay(int range, DeckBase deck) {
        int[] cardsPerDay = new int[range];

        List<CardBase> allCards = deckService.getAllCards(deck);
        LocalDate today = LocalDate.now();

        for (CardBase card : allCards) {
            if (!card.isNewCard()) {
                LocalDate revisionDate = card.getNextRegularRevisionDate();

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
     * Retrieves the title for the chart.
     *
     * @return the translated chart title
     */
    @Override
    public String getChartTitle() {
        return translationService.getTranslation("revision." + AvailableRevisions.getAVAILABLE_REVISIONS().get(getSupportedRevisionType()).translationKey() + ".statistics_appearance_title");
    }

    /**
     * Indicates that this chart is not revision type-independent and is specific to regular revision mode.
     *
     * @return {@code false} since this chart is specific to regular revisions
     */
    @Override
    public boolean isRevisionTypeIndependent(){
        return false;
    }

    /**
     * Specifies the supported revision type for this chart.
     *
     * @return the {@link Class} representing {@link RegularTextModeRevision}
     */
    @Override
    public Class<?> getSupportedRevisionType(){
        return RegularTextModeRevision.class;
    }
}