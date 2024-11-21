package gutek.services;

import gutek.domain.charts.ChartEntry;
import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import gutek.domain.charts.charts.StatisticsChart;
import javafx.scene.chart.Chart;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Service class responsible for managing chart generation and translation of available ranges and chart types.
 */
@Service
public class ChartService {

    /**
     * Array of available time ranges for generating charts, in days.
     */
    @Getter
    private static final Integer[] AVAILABLE_RANGES = new Integer[]{
            31, 91, 181, 361, 721, 1081
    };

    /**
     * Maximum time range available for chart generation.
     */
    public static final Integer MAX_RANGE = Arrays.stream(AVAILABLE_RANGES).max(Integer::compareTo).orElse(0);

    /**
     * Array of translation keys corresponding to available time ranges for chart selection.
     */
    private static final String[] AVAILABLE_RANGES_TRANSLATION_KEYS = new String[]{
            "deck_view.statistics.available_ranges_1",
            "deck_view.statistics.available_ranges_2",
            "deck_view.statistics.available_ranges_3",
            "deck_view.statistics.available_ranges_4",
            "deck_view.statistics.available_ranges_5",
            "deck_view.statistics.available_ranges_6"
    };

    /**
     * Service responsible for providing translations for different locales.
     */
    private final TranslationService translationService;

    /**
     * List of available chart types provided by the application.
     */
    private final List<StatisticsChart> availableCharts;

    /**
     * Constructor for ChartService.
     *
     * @param translationService Service to handle translations.
     * @param availableCharts    List of available statistics charts.
     */
    public ChartService(TranslationService translationService,
                        List<StatisticsChart> availableCharts) {
        this.translationService = translationService;
        this.availableCharts = availableCharts;
    }

    /**
     * Retrieves a selected chart that is compatible with the given deck.
     *
     * @param typeIndex          The index of the chart type to select.
     * @param rangeIndex         The index of the time range to use for the chart.
     * @param deck               The deck for which the chart is generated.
     * @return A {@link Chart} object representing the selected chart.
     */
    public Chart getDeckCompatibleSelectedChart(int typeIndex, int rangeIndex, DeckBase deck) {
        List<ChartEntry> compatibleCharts = getDeckCompatibleCharts(deck);
        ChartEntry selectedEntry = compatibleCharts.get(typeIndex);
        StatisticsChart selectedChart = selectedEntry.chart();
        Integer revisionStrategyIndex = selectedEntry.revisionStrategyIndex();

        return selectedChart.getChart(AVAILABLE_RANGES[rangeIndex], deck, revisionStrategyIndex);
    }

    /**
     * Retrieves the titles of all charts compatible with the given deck, translated into the current locale.
     *
     * @param deck The deck for which the chart titles are retrieved.
     * @return An array of translated chart titles as {@link String}.
     */
    public String[] getDeckCompatibleAvailableChartsTitles(DeckBase deck) {
        List<ChartEntry> compatibleCharts = getDeckCompatibleCharts(deck);
        List<String> chartTitles = new ArrayList<>();

        for (ChartEntry entry : compatibleCharts) {
            StatisticsChart chart = entry.chart();
            Integer revisionStrategyIndex = entry.revisionStrategyIndex();
            chartTitles.add(chart.getChartTitle(deck, revisionStrategyIndex));
        }

        return chartTitles.toArray(new String[0]);
    }

    /**
     * Retrieves the available time ranges for chart selection, translated into the current locale.
     *
     * @return An array of translated range descriptions as {@link String}.
     */
    public String[] getAvailableRanges(){
        return Arrays.stream(AVAILABLE_RANGES_TRANSLATION_KEYS)
                .map(translationService::getTranslation)
                .toArray(String[]::new);
    }

    /**
     * Retrieves all charts that are compatible with the given deck.
     * <p>
     * Charts can be either independent of revision strategies or associated with specific revision strategies.
     * </p>
     *
     * @param deck The deck for which compatible charts are retrieved.
     * @return A {@link List} of {@link ChartEntry} objects representing compatible charts.
     */
    private List<ChartEntry> getDeckCompatibleCharts(DeckBase deck) {
        RevisionAlgorithm<?> revisionAlgorithm = deck.getRevisionAlgorithm();
        List<? extends RevisionStrategy<?>> revisionStrategies = revisionAlgorithm.getAvailableRevisionStrategies();

        List<ChartEntry> compatibleCharts = new ArrayList<>();

        for (StatisticsChart chart : availableCharts) {
            if (chart.isRevisionStrategyIndependent()) {
                compatibleCharts.add(new ChartEntry(chart, null));
            } else {
                for (int i = 0; i < revisionStrategies.size(); i++) {
                    compatibleCharts.add(new ChartEntry(chart, i));
                }
            }
        }

        return compatibleCharts;
    }
}
