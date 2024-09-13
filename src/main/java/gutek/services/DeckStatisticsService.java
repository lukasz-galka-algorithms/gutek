package gutek.services;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBaseStatistics;
import gutek.repositories.DeckBaseRepository;
import gutek.repositories.DeckBaseStatisticsRepository;
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
     * Repository for accessing deck information.
     */
    private final DeckBaseRepository deckBaseRepository;
    /**
     * Repository for accessing deck statistics.
     */
    private final DeckBaseStatisticsRepository deckBaseStatisticsRepository;
    /**
     * Updates the statistics for the given deck if they are not up-to-date.
     *
     * @param idDeckStatistics ID of the deck statistics to update.
     */
    private void updateStatisticsForToday(Long idDeckStatistics){
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        if (updatedStatistics.isPresent()){
            LocalDate today = LocalDate.now();
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(updatedStatistics.get().getTodayIndicator(), today);
            if (daysBetween > 0) {
                shiftStatistics(updatedStatistics.get(), (int) daysBetween);
                updatedStatistics.get().setTodayIndicator(LocalDate.now());
                deckBaseStatisticsRepository.save(updatedStatistics.get());
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
        int[] regularRevision = statistics.getRegularRevision();
        int[] reverseRevision = statistics.getReverseRevision();
        int range = MAX_RANGE;

        if (daysBetween >= range) {
            for (int i = 0; i < range; i++) {
                revisedForTheFirstTime[i] = 0;
                regularRevision[i] = 0;
                reverseRevision[i] = 0;
            }
        } else {
            for (int i = range - 1; i >= daysBetween; i--) {
                revisedForTheFirstTime[i] = revisedForTheFirstTime[i - daysBetween];
                regularRevision[i] = regularRevision[i - daysBetween];
                reverseRevision[i] = reverseRevision[i - daysBetween];
            }
            for (int i = 0; i < daysBetween; i++) {
                revisedForTheFirstTime[i] = 0;
                regularRevision[i] = 0;
                reverseRevision[i] = 0;
            }
        }

        statistics.setRevisedForTheFirstTime(revisedForTheFirstTime);
        statistics.setRegularRevision(regularRevision);
        statistics.setReverseRevision(reverseRevision);
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
            int allNewCardsNumber = deckBaseRepository.findById(updatedStatistics.get().getDeck().getIdDeck()).get().getCards().stream()
                    .filter(CardBase::isNewCard)
                    .toList().size();
            return Math.max(Math.min(updatedStatistics.get().getNewCardsPerDay() - updatedStatistics.get().getRevisedForTheFirstTime()[0], allNewCardsNumber),0);
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
        if (updatedStatistics.isPresent()){
            return updatedStatistics.get().getRevisedForTheFirstTime();
        }
        return null;
    }
    /**
     * Returns the counts of cards that underwent regular revision.
     *
     * @param idDeckStatistics ID of the deck statistics.
     * @return Array of counts for regular revision.
     */
    public int[] getRegularRevisionCounts(Long idDeckStatistics){
        updateStatisticsForToday(idDeckStatistics);
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        if (updatedStatistics.isPresent()){
            return updatedStatistics.get().getRegularRevision();
        }
        return null;
    }
    /**
     * Returns the counts of cards that underwent reverse revision.
     *
     * @param idDeckStatistics ID of the deck statistics.
     * @return Array of counts for reverse revision.
     */
    public int[] getReverseRevisionCounts(Long idDeckStatistics){
        updateStatisticsForToday(idDeckStatistics);
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        if (updatedStatistics.isPresent()){
            return updatedStatistics.get().getReverseRevision();
        }
        return null;
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
            updatedStatistics.get().getRevisedForTheFirstTime()[0]++;
            saveDeckStatistics(updatedStatistics.get());
        }
    }
    /**
     * Increments the count of regular revision for today.
     *
     * @param idDeckStatistics ID of the deck statistics.
     */
    public void cardRevisedRegular(Long idDeckStatistics){
        updateStatisticsForToday(idDeckStatistics);
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        if (updatedStatistics.isPresent()){
            updatedStatistics.get().getRegularRevision()[0]++;
            saveDeckStatistics(updatedStatistics.get());
        }
    }
    /**
     * Increments the count of reverse revision for today.
     *
     * @param idDeckStatistics ID of the deck statistics.
     */
    public void cardRevisedReverse(Long idDeckStatistics){
        updateStatisticsForToday(idDeckStatistics);
        Optional<DeckBaseStatistics> updatedStatistics = deckBaseStatisticsRepository.findById(idDeckStatistics);
        if (updatedStatistics.isPresent()){
            updatedStatistics.get().getReverseRevision()[0]++;
            saveDeckStatistics(updatedStatistics.get());
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
}
