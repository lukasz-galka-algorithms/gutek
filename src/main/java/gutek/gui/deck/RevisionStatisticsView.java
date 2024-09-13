package gutek.gui.deck;

import gutek.entities.decks.DeckBase;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.controls.DeckMenuPanel;
import gutek.services.ChartService;
import gutek.services.DeckService;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import lombok.Setter;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * A view for displaying revision statistics in the form of charts.
 *
 * This view allows users to select different chart types and time ranges to visualize various statistics
 * about a deck's revision process. The view dynamically updates the chart based on the user's selections.
 */
@Component
public class RevisionStatisticsView extends AppView {
    /** The panel containing the deck menu options. */
    private DeckMenuPanel menuBarPanel;
    /** Label for the chart type selection. */
    private JLabel chartTypeLabel;
    /** Combo box for selecting the type of chart to display. */
    private JComboBox<String> chartTypeComboBox;
    /** Label for the chart time range selection. */
    private JLabel chartRangeLabel;
    /** Combo box for selecting the time range of the chart. */
    private JComboBox<String> timeRangeComboBox;
    /** Panel containing the chart controls (chart type and range selection). */
    private JPanel controlsPanel;
    /** Panel that displays the selected chart. */
    private JPanel centerPanel;
    /** The deck whose statistics are being displayed. */
    @Setter
    private DeckBase deck;
    /** Service responsible for generating charts. */
    private final ChartService chartService;
    /**
     * Constructs a new `RevisionStatisticsView` for visualizing deck revision statistics.
     *
     * @param frame the main frame of the application
     * @param translationService the service used for retrieving translations for the UI
     * @param chartService the service used for generating charts
     */
    public RevisionStatisticsView(MainFrame frame, TranslationService translationService,
                                  ChartService chartService) {
        super(frame, translationService);
        this.chartService = chartService;

        setLayout(new BorderLayout());

        menuBarPanel = new DeckMenuPanel(frame, translationService);
        add(menuBarPanel, BorderLayout.NORTH);

        centerPanel = new JPanel(new BorderLayout());
        controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        chartTypeComboBox = new JComboBox<>(chartService.getAvailableChartsTitles());
        chartTypeLabel = new JLabel();
        controlsPanel.add(chartTypeLabel);
        controlsPanel.add(chartTypeComboBox);

        timeRangeComboBox = new JComboBox<>(chartService.getAvailableRanges());
        chartRangeLabel = new JLabel();
        controlsPanel.add(chartRangeLabel);
        controlsPanel.add(timeRangeComboBox);

        centerPanel.add(controlsPanel, BorderLayout.NORTH);

        add(centerPanel, BorderLayout.CENTER);

        chartTypeComboBox.addActionListener(e -> handleChartSelectionChange());
        timeRangeComboBox.addActionListener(e -> handleChartSelectionChange());
    }
    /**
     * Updates the size of the view components based on the window size and scale factor.
     */
    @Override
    public void updateSize() {
        super.updateSize();

        Dimension dimensionScaled = frame.getScaledSize();
        double scaleFactor = frame.getScaleFactor();

        this.setPreferredSize(dimensionScaled);
        menuBarPanel.updateSize(dimensionScaled, scaleFactor);

        revalidate();
        repaint();
    }
    /**
     * Updates the text in the view components based on the current language settings.
     */
    @Override
    public void updateTranslation() {
        super.updateTranslation();
        menuBarPanel.updateTranslation();

        chartTypeLabel.setText(translationService.getTranslation("deck_view.statistics.chart_type"));
        chartRangeLabel.setText(translationService.getTranslation("deck_view.statistics.chart_range"));

        int selectedChartTypeIndex = chartTypeComboBox.getSelectedIndex();
        int selectedRangeIndex = timeRangeComboBox.getSelectedIndex();
        chartTypeComboBox.setModel(new DefaultComboBoxModel<>(
                chartService.getAvailableChartsTitles()
        ));
        timeRangeComboBox.setModel(new DefaultComboBoxModel<>(
                chartService.getAvailableRanges()
        ));
        chartTypeComboBox.setSelectedIndex(selectedChartTypeIndex);
        timeRangeComboBox.setSelectedIndex(selectedRangeIndex);

        handleChartSelectionChange();
    }
    /**
     * Updates the view by loading the selected chart and displaying it in the center panel.
     */
    @Override
    public void updateView() {
        super.updateView();
    }
    /**
     * Handles the change in chart selection (chart type or time range) and updates the displayed chart.
     *
     * This method retrieves the appropriate chart from the chart service and displays it in the center panel.
     */
    private void handleChartSelectionChange() {
        int selectedChartTypeIndex = chartTypeComboBox.getSelectedIndex();
        int selectedRangeIndex = timeRangeComboBox.getSelectedIndex();
        centerPanel.removeAll();
        JFreeChart chart = chartService.getSelectedChart(selectedChartTypeIndex, selectedRangeIndex, deck);
        ChartPanel chartPanel = new ChartPanel(chart);
        centerPanel.add(controlsPanel, BorderLayout.NORTH);
        centerPanel.add(chartPanel, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }
}
