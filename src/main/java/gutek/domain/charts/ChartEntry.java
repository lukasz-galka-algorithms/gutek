package gutek.domain.charts;

import gutek.domain.charts.charts.StatisticsChart;

/**
 * Represents an entry for a statistical chart along with an associated revision strategy index.
 * <p>
 * This class is used to pair a {@link StatisticsChart} instance with a specific revision strategy index.
 * It serves as a data container for managing charts in contexts where the revision strategy is relevant.
 * </p>
 *
 * @param chart                the {@link StatisticsChart} instance to associate with this entry
 * @param revisionStrategyIndex the index of the revision strategy associated with the chart, may be {@code null}
 */
public record ChartEntry(StatisticsChart chart, Integer revisionStrategyIndex) {
}