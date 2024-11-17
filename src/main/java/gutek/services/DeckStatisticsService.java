package gutek.services;

import gutek.entities.decks.DeckBaseStatistics;
import gutek.entities.decks.RevisionCounts;
import gutek.repositories.CardBaseRepository;
import gutek.repositories.DeckBaseStatisticsRepository;
import gutek.repositories.RevisionCountsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;
import static gutek.services.ChartService.MAX_RANGE;

/**
 * Service class responsible for managing deck statistics.
 */
@Service
@AllArgsConstructor
public class DeckStatisticsService {

    /**
     * Repository for accessing cards.
     */
    private final CardBaseRepository cardBaseRepository;

    /**
     * Repository for accessing deck statistics.
     */
    private final DeckBaseStatisticsRepository deckBaseStatisticsRepository;

    /**
     * Repository for managing revision counts.
     */
    private final RevisionCountsRepository revisionCountsRepository;

    /**
     * Updates the statistics for the given deck if they are not up-to-date.
     *
     * @param idDeckStatistics ID of the deck statistics to update.
     */
    private void updateStatisticsForToday(Long idDeckStatistics){
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        if (updatedStatistics.isPresent()){
            DeckBaseStatistics stat = updatedStatistics.get();
            LocalDate today = LocalDate.now();
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(stat.getTodayIndicator(), today);
            if (daysBetween > 0) {
                shiftStatistics(stat, (int) daysBetween);
                stat.setTodayIndicator(LocalDate.now());
                deckBaseStatisticsRepository.save(stat);
            }
        }
    }

    /**
     * Shifts the statistics by the given number of days.
     *
     * @param statistics   The statistics to shift.
     * @param daysBetween The number of days to shift.
     */
    private void shiftStatistics(DeckBaseStatistics statistics, int daysBetween) {
        int[] revisedForTheFirstTime = statistics.getRevisedForTheFirstTime();
        int range = MAX_RANGE;

        shiftArray(revisedForTheFirstTime, daysBetween, range);
        statistics.setRevisedForTheFirstTime(revisedForTheFirstTime);

        for (RevisionCounts revisionCount : statistics.getRevisionCounts().values()) {
            int[] counts = revisionCount.getCounts();
            shiftArray(counts, daysBetween, range);
            revisionCount.setCounts(counts);
            revisionCountsRepository.save(revisionCount);
        }
    }

    /**
     * Shifts the contents of an array forward by the specified number of days.
     * Older values are discarded, and new slots are filled with zeros.
     *
     * @param array The array to shift.
     * @param daysBetween The number of days to shift.
     * @param range The range of the array.
     */
    private void shiftArray(int[] array, int daysBetween, int range) {
        if (daysBetween >= range) {
            for (int i = 0; i < range; i++) {
                array[i] = 0;
            }
        } else {
            for (int i = range - 1; i >= daysBetween; i--) {
                array[i] = array[i - daysBetween];
            }
            for (int i = 0; i < daysBetween; i++) {
                array[i] = 0;
            }
        }
    }

    /**
     * Returns the number of new cards that can be revised today for the given deck.
     *
     * @param idDeckStatistics ID of the deck statistics.
     * @return Number of new cards for today's revision.
     */
    public int getNewCardsForToday(Long idDeckStatistics){
        updateStatisticsForToday(idDeckStatistics);
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        if (updatedStatistics.isPresent()){
            DeckBaseStatistics stat = updatedStatistics.get();
            int allNewCardsNumber = cardBaseRepository.countByDeckIdDeckAndIsNewCardTrue(stat.getDeck().getIdDeck());
            return Math.clamp((long) stat.getNewCardsPerDay() - stat.getRevisedForTheFirstTime()[0],0,allNewCardsNumber);
        }
        return -1;
    }

    /**
     * Returns the counts of cards that were revised for the first time.
     *
     * @param idDeckStatistics ID of the deck statistics.
     * @return Array of counts for cards revised for the first time.
     */
    public int[] getReviseForTheFirstTimeCounts(Long idDeckStatistics){
        updateStatisticsForToday(idDeckStatistics);
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        return updatedStatistics.map(DeckBaseStatistics::getRevisedForTheFirstTime).orElse(null);
    }

    /**
     * Returns the revision counts for a specific revision strategy in a given deck.
     *
     * @param idDeckStatistics ID of the deck statistics.
     * @param strategyIndex    The index of the revision strategy.
     * @return An array of revision counts for the specified strategy.
     */
    public int[] getRevisionCounts(Long idDeckStatistics, Integer strategyIndex){
        updateStatisticsForToday(idDeckStatistics);
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        if (updatedStatistics.isPresent()){
            DeckBaseStatistics stat = updatedStatistics.get();
            if (strategyIndex < 0 || strategyIndex >= stat.getDeck().getRevisionAlgorithm().getAvailableRevisionStrategies().size()) {
                throw new IllegalArgumentException("Strategy index " + strategyIndex + " is not supported by the deck.");
            }

            RevisionCounts revisionCount = stat.getRevisionCounts().computeIfAbsent(strategyIndex, k -> {
                RevisionCounts rc = new RevisionCounts();
                rc.setStrategyIndex(strategyIndex);
                rc.setDeckBaseStatistics(stat);
                rc.setCounts(new int[MAX_RANGE]);
                revisionCountsRepository.save(rc);
                return rc;
            });

            return revisionCount.getCounts();
        }
        return new int[0];
    }

    /**
     * Increments the count of new cards revised today.
     *
     * @param idDeckStatistics ID of the deck statistics.
     */
    public void newCardRevised(Long idDeckStatistics){
        updateStatisticsForToday(idDeckStatistics);
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        if (updatedStatistics.isPresent()){
            DeckBaseStatistics stat = updatedStatistics.get();
            stat.getRevisedForTheFirstTime()[0]++;
            saveDeckStatistics(stat);
        }
    }

    /**
     * Updates the statistics for a card revision for a specific strategy.
     *
     * @param idDeckStatistics ID of the deck statistics.
     * @param strategyIndex    The index of the revision strategy.
     */
    public void cardRevised(Long idDeckStatistics, int strategyIndex){
        updateStatisticsForToday(idDeckStatistics);
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        if (updatedStatistics.isPresent()){
            DeckBaseStatistics stat = updatedStatistics.get();
            if (strategyIndex < 0 || strategyIndex >= stat.getDeck().getRevisionAlgorithm().getAvailableRevisionStrategies().size()) {
                throw new IllegalArgumentException("Strategy index " + strategyIndex + " is not supported by the deck.");
            }
            RevisionCounts revisionCount = stat.getRevisionCounts().computeIfAbsent(strategyIndex, k -> {
                RevisionCounts rc = new RevisionCounts();
                rc.setStrategyIndex(strategyIndex);
                rc.setDeckBaseStatistics(stat);
                rc.setCounts(new int[MAX_RANGE]);
                return rc;
            });
            revisionCount.getCounts()[0]++;
            revisionCountsRepository.save(revisionCount);
        }
    }

    /**
     * Saves or updates the given deck statistics.
     *
     * @param deckStatistics The deck statistics to save or update.
     */
    public void saveDeckStatistics(DeckBaseStatistics deckStatistics){
        deckBaseStatisticsRepository.save(deckStatistics);
    }

    /**
     * Loads the statistics for the specified deck by its ID.
     *
     * @param deckStatisticsId The ID of the deck statistics to retrieve.
     * @return An {@link Optional} containing the {@link DeckBaseStatistics} if found, or an empty Optional if not found.
     */
    public Optional<DeckBaseStatistics> loadDeckStatistics(Long deckStatisticsId){
        return deckBaseStatisticsRepository.findById(deckStatisticsId);
    }
}
