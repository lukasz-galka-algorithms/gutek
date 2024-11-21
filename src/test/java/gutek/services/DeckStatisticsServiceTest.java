package gutek.services;

import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import gutek.entities.decks.DeckBaseStatistics;
import gutek.entities.decks.RevisionCounts;
import gutek.repositories.CardBaseRepository;
import gutek.repositories.DeckBaseStatisticsRepository;
import gutek.repositories.RevisionCountsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static gutek.services.ChartService.MAX_RANGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeckStatisticsServiceTest {

    @Mock
    private CardBaseRepository cardBaseRepository;

    @Mock
    private DeckBaseStatisticsRepository deckBaseStatisticsRepository;

    @Mock
    private RevisionCountsRepository revisionCountsRepository;

    private DeckStatisticsService deckStatisticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deckStatisticsService = new DeckStatisticsService(
                cardBaseRepository,
                deckBaseStatisticsRepository,
                revisionCountsRepository
        );
    }

    @Test
    void testGetNewCardsForToday_StatisticsExist() {
        // Arrange
        Long statsId = 1L;
        DeckBase deck = new DeckBase();
        deck.setIdDeck(2L);

        DeckBaseStatistics stats = new DeckBaseStatistics();
        stats.setDeck(deck);
        stats.setTodayIndicator(LocalDate.now().minusDays(1));
        stats.setRevisedForTheFirstTime(new int[MAX_RANGE]);
        stats.setNewCardsPerDay(5);

        when(deckBaseStatisticsRepository.findById(statsId)).thenReturn(Optional.of(stats));
        when(cardBaseRepository.countByDeckIdDeckAndIsNewCardTrue(deck.getIdDeck())).thenReturn(10);

        // Act
        int newCardsForToday = deckStatisticsService.getNewCardsForToday(statsId);

        // Assert
        assertEquals(5, newCardsForToday);
        verify(deckBaseStatisticsRepository, times(2)).findById(statsId);
        verify(deckBaseStatisticsRepository, times(1)).save(stats);
    }

    @Test
    void testGetNewCardsForToday_StatisticsNotExist() {
        // Arrange
        Long statsId = 1L;
        when(deckBaseStatisticsRepository.findById(statsId)).thenReturn(Optional.empty());

        // Act
        int result = deckStatisticsService.getNewCardsForToday(statsId);

        // Assert
        assertEquals(-1, result);
        verify(deckBaseStatisticsRepository, times(2)).findById(statsId);
    }

    @Test
    void testGetReviseForTheFirstTimeCounts_StatisticsExist() {
        // Arrange
        Long statsId = 1L;
        DeckBaseStatistics stats = new DeckBaseStatistics();
        stats.setRevisedForTheFirstTime(new int[]{1, 2, 3});

        when(deckBaseStatisticsRepository.findById(statsId)).thenReturn(Optional.of(stats));

        // Act
        int[] counts = deckStatisticsService.getReviseForTheFirstTimeCounts(statsId);

        // Assert
        assertArrayEquals(new int[]{1, 2, 3}, counts);
        verify(deckBaseStatisticsRepository, times(2)).findById(statsId);
    }

    @Test
    void testGetReviseForTheFirstTimeCounts_StatisticsNotExist() {
        // Arrange
        Long statsId = 1L;
        when(deckBaseStatisticsRepository.findById(statsId)).thenReturn(Optional.empty());

        // Act
        int[] result = deckStatisticsService.getReviseForTheFirstTimeCounts(statsId);

        // Assert
        assertNull(result);
        verify(deckBaseStatisticsRepository, times(2)).findById(statsId);
    }

    @Test
    void testGetRevisionCounts_ValidStrategyIndex() {
        // Arrange
        Long statsId = 1L;
        int strategyIndex = 0;

        DeckBase deck = new DeckBase();
        RevisionAlgorithm mockRevisionAlgorithm = mock(RevisionAlgorithm.class);
        deck.setRevisionAlgorithm(mockRevisionAlgorithm);
        when(mockRevisionAlgorithm.getAvailableRevisionStrategies()).thenReturn(List.of(mock(RevisionStrategy.class)));

        DeckBaseStatistics stats = new DeckBaseStatistics();
        stats.setDeck(deck);

        RevisionCounts revisionCounts = new RevisionCounts();
        revisionCounts.setCounts(new int[]{1, 2, 3});

        stats.getRevisionCounts().put(strategyIndex, revisionCounts);

        when(deckBaseStatisticsRepository.findById(statsId)).thenReturn(Optional.of(stats));

        // Act
        int[] counts = deckStatisticsService.getRevisionCounts(statsId, strategyIndex);

        // Assert
        assertArrayEquals(new int[]{1, 2, 3}, counts);
        verify(deckBaseStatisticsRepository, times(2)).findById(statsId);
    }

    @Test
    void testGetRevisionCounts_InvalidStrategyIndex() {
        // Arrange
        Long statsId = 1L;
        int invalidIndex = 5;

        DeckBase deck = new DeckBase();
        deck.setRevisionAlgorithm(mock(RevisionAlgorithm.class));

        DeckBaseStatistics stats = new DeckBaseStatistics();
        stats.setDeck(deck);

        when(deckBaseStatisticsRepository.findById(statsId)).thenReturn(Optional.of(stats));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> deckStatisticsService.getRevisionCounts(statsId, invalidIndex));
        verify(deckBaseStatisticsRepository, times(2)).findById(statsId);
    }

    @Test
    void testNewCardRevised() {
        // Arrange
        Long statsId = 1L;
        DeckBaseStatistics stats = new DeckBaseStatistics();
        stats.setRevisedForTheFirstTime(new int[]{0});

        when(deckBaseStatisticsRepository.findById(statsId)).thenReturn(Optional.of(stats));

        // Act
        deckStatisticsService.newCardRevised(statsId);

        // Assert
        assertEquals(1, stats.getRevisedForTheFirstTime()[0]);
        verify(deckBaseStatisticsRepository, times(2)).findById(statsId);
        verify(deckBaseStatisticsRepository, times(1)).save(stats);
    }

    @Test
    void testCardRevised() {
        // Arrange
        Long statsId = 1L;
        int strategyIndex = 0;

        DeckBase deck = new DeckBase();
        deck.setIdDeck(2L);
        RevisionAlgorithm mockRevisionAlgorithm = mock(RevisionAlgorithm.class);
        deck.setRevisionAlgorithm(mockRevisionAlgorithm);
        when(mockRevisionAlgorithm.getAvailableRevisionStrategies()).thenReturn(List.of(mock(RevisionStrategy.class)));

        DeckBaseStatistics stats = new DeckBaseStatistics();
        stats.setDeck(deck);

        RevisionCounts revisionCounts = new RevisionCounts();
        revisionCounts.setCounts(new int[]{0});

        stats.getRevisionCounts().put(strategyIndex, revisionCounts);

        when(deckBaseStatisticsRepository.findById(statsId)).thenReturn(Optional.of(stats));

        // Act
        deckStatisticsService.cardRevised(statsId, strategyIndex);

        // Assert
        assertEquals(1, revisionCounts.getCounts()[0]);
        verify(deckBaseStatisticsRepository, times(2)).findById(statsId);
        verify(revisionCountsRepository, times(1)).save(revisionCounts);
    }

    @Test
    void testSaveDeckStatistics() {
        // Arrange
        DeckBaseStatistics stats = new DeckBaseStatistics();

        // Act
        deckStatisticsService.saveDeckStatistics(stats);

        // Assert
        verify(deckBaseStatisticsRepository, times(1)).save(stats);
    }

    @Test
    void testLoadDeckStatistics() {
        // Arrange
        Long statsId = 1L;
        DeckBaseStatistics stats = new DeckBaseStatistics();

        when(deckBaseStatisticsRepository.findById(statsId)).thenReturn(Optional.of(stats));

        // Act
        Optional<DeckBaseStatistics> result = deckStatisticsService.loadDeckStatistics(statsId);

        // Assert
        assertEquals(Optional.of(stats), result);
        verify(deckBaseStatisticsRepository, times(1)).findById(statsId);
    }
}
