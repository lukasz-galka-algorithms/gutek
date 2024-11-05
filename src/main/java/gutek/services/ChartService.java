package gutek.services;

import gutek.entities.decks.DeckBase;
import gutek.gui.charts.StatisticsChart;
import javafx.scene.chart.Chart;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

/**
 * Service class responsible for managing chart generation and translation of available ranges and chart types.
 */
@Service
public class ChartService {

    /**
     * Array of available time ranges for generating charts, in days.
     */
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
     * Retrieves the selected chart based on the provided type index and time range index.
     *
     * @param typeIndex  The index of the chart type.
     * @param rangeIndex The index of the time range.
     * @param deck       The deck for which the chart is generated.
     * @return The generated {@link Chart} for the specified parameters.
     */
    public Chart getSelectedChart(int typeIndex, int rangeIndex, DeckBase deck){
        return availableCharts.get(typeIndex).getChart(AVAILABLE_RANGES[rangeIndex], deck);
    }

    /**
     * Provides the titles of available charts for display.
     *
     * @return An array of chart titles as {@link String}.
     */
    public String[] getAvailableChartsTitles() {
        return availableCharts.stream()
                .map(StatisticsChart::getChartTitle)
                .toArray(String[]::new);
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
}
