package gutek.services;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.entities.decks.DeckBaseStatistics;
import gutek.entities.users.AppUser;
import gutek.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeckServiceTest {

    @Mock
    private DeckBaseRepository deckBaseRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private CardBaseRepository cardBaseRepository;

    @Mock
    private CardBaseRevisionRepository cardBaseRevisionRepository;

    @Mock
    private RevisionAlgorithmRepository revisionAlgorithmRepository;

    @Mock
    private DeckBaseStatisticsRepository deckBaseStatisticsRepository;

    private DeckService deckService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deckService = new DeckService(
                deckBaseRepository,
                appUserRepository,
                cardBaseRepository,
                cardBaseRevisionRepository,
                revisionAlgorithmRepository,
                deckBaseStatisticsRepository
        );
    }

    @Test
    void testGetAllCards() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        List<CardBase> mockCards = List.of(new CardBase(), new CardBase());
        when(cardBaseRepository.findByDeck(mockDeck)).thenReturn(mockCards);

        // Act
        List<CardBase> cards = deckService.getAllCards(mockDeck);

        // Assert
        assertEquals(2, cards.size());
        verify(cardBaseRepository, times(1)).findByDeck(mockDeck);
    }

    @Test
    void testGetRegularRevisionCards() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);
        List<CardBase> mockCards = List.of(new CardBase());
        when(cardBaseRepository.findByDeckIdDeckAndIsNewCardFalseAndNextRegularRevisionDateLessThanEqual(
                mockDeck.getIdDeck(), LocalDate.now())).thenReturn(mockCards);

        // Act
        List<CardBase> cards = deckService.getRegularRevisionCards(mockDeck);

        // Assert
        assertEquals(1, cards.size());
        verify(cardBaseRepository, times(1))
                .findByDeckIdDeckAndIsNewCardFalseAndNextRegularRevisionDateLessThanEqual(mockDeck.getIdDeck(), LocalDate.now());
    }

    @Test
    void testRemoveDeck() {
        // Arrange
        AppUser mockUser = new AppUser();
        DeckBase mockDeck = new DeckBase();
        mockDeck.setUser(mockUser);
        CardBase mockCard = new CardBase();
        mockDeck.getCards().add(mockCard);
        RevisionAlgorithm<?> mockAlgorithm = mock(RevisionAlgorithm.class);
        mockDeck.setRevisionAlgorithm(mockAlgorithm);

        // Act
        deckService.removeDeck(mockDeck);

        // Assert
        verify(cardBaseRevisionRepository, times(1)).deleteByCardBase(mockCard);
        verify(cardBaseRepository, times(1)).delete(mockCard);
        verify(deckBaseRepository, times(1)).delete(mockDeck);
        verify(revisionAlgorithmRepository, times(1)).delete(mockAlgorithm);
    }

    @Test
    void testAddNewDeck() {
        // Arrange
        AppUser mockUser = new AppUser();
        mockUser.setIdUser(1L);
        RevisionAlgorithm<?> mockAlgorithm = mock(RevisionAlgorithm.class);

        when(appUserRepository.findById(mockUser.getIdUser())).thenReturn(Optional.of(mockUser));

        // Act
        DeckBase newDeck = deckService.addNewDeck(mockUser, mockAlgorithm, "Test Deck");

        // Assert
        assertNotNull(newDeck);
        verify(deckBaseRepository, times(2)).save(newDeck);
        verify(deckBaseStatisticsRepository, times(1)).save(any(DeckBaseStatistics.class));
        verify(appUserRepository, times(1)).save(mockUser);
    }

    @Test
    void testFindDecksByUser() {
        // Arrange
        AppUser mockUser = new AppUser();
        List<DeckBase> mockDecks = List.of(new DeckBase(), new DeckBase());
        when(deckBaseRepository.findByUser(mockUser)).thenReturn(mockDecks);

        // Act
        List<DeckBase> decks = deckService.findDecksByUser(mockUser);

        // Assert
        assertEquals(2, decks.size());
        verify(deckBaseRepository, times(1)).findByUser(mockUser);
    }

    @Test
    void testGetAllCardsCount() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);
        when(cardBaseRepository.countByDeckIdDeck(mockDeck.getIdDeck())).thenReturn(10);

        // Act
        int count = deckService.getAllCardsCount(mockDeck);

        // Assert
        assertEquals(10, count);
        verify(cardBaseRepository, times(1)).countByDeckIdDeck(mockDeck.getIdDeck());
    }

    @Test
    void testAddNewCardToDeck_WhenCardDoesNotExist() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);
        CardBase mockCard = new CardBase();
        mockCard.setFront("Test Front");

        when(deckBaseRepository.findById(mockDeck.getIdDeck())).thenReturn(Optional.of(mockDeck));
        when(cardBaseRepository.findByFrontAndDeck(mockCard.getFront(), mockDeck)).thenReturn(Optional.empty());

        // Act
        deckService.addNewCardToDeck(mockCard, mockDeck);

        // Assert
        verify(deckBaseRepository, times(1)).save(mockDeck);
        verify(cardBaseRepository, times(1)).save(mockCard);
    }

    @Test
    void testAddNewCardToDeck_WhenCardExists() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);
        CardBase mockCard = new CardBase();
        mockCard.setFront("Test Front");

        when(deckBaseRepository.findById(mockDeck.getIdDeck())).thenReturn(Optional.of(mockDeck));
        when(cardBaseRepository.findByFrontAndDeck(mockCard.getFront(), mockDeck)).thenReturn(Optional.of(mockCard));

        // Act
        deckService.addNewCardToDeck(mockCard, mockDeck);

        // Assert
        verify(deckBaseRepository, never()).save(mockDeck);
        verify(cardBaseRepository, never()).save(mockCard);
    }

    @Test
    void testGetNewCardsForTodayRevision_WhenEnoughNewCards() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);

        CardBase card1 = new CardBase();
        card1.setCreationTime(LocalDateTime.now().minusDays(2));

        CardBase card2 = new CardBase();
        card2.setCreationTime(LocalDateTime.now().minusDays(1));

        CardBase card3 = new CardBase();
        card3.setCreationTime(LocalDateTime.now());

        List<CardBase> mockNewCards = List.of(card1, card2, card3);
        when(cardBaseRepository.findByDeckIdDeckAndIsNewCardTrue(mockDeck.getIdDeck())).thenReturn(mockNewCards);

        // Act
        List<CardBase> result = deckService.getNewCardsForTodayRevision(mockDeck, 2);

        // Assert
        assertEquals(2, result.size());
        assertEquals(card1, result.get(0));
        assertEquals(card2, result.get(1));
        verify(cardBaseRepository, times(1)).findByDeckIdDeckAndIsNewCardTrue(mockDeck.getIdDeck());
    }

    @Test
    void testGetNewCardsForTodayRevision_WhenNotEnoughNewCards() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);

        CardBase card1 = new CardBase();
        card1.setCreationTime(LocalDateTime.now().minusDays(2));

        CardBase card2 = new CardBase();
        card2.setCreationTime(LocalDateTime.now().minusDays(1));

        List<CardBase> mockNewCards = List.of(card1, card2);
        when(cardBaseRepository.findByDeckIdDeckAndIsNewCardTrue(mockDeck.getIdDeck())).thenReturn(mockNewCards);

        // Act
        List<CardBase> result = deckService.getNewCardsForTodayRevision(mockDeck, 5);

        // Assert
        assertEquals(2, result.size());
        assertEquals(card1, result.get(0));
        assertEquals(card2, result.get(1));
        verify(cardBaseRepository, times(1)).findByDeckIdDeckAndIsNewCardTrue(mockDeck.getIdDeck());
    }

    @Test
    void testGetNewCardsForTodayRevision_WhenNoNewCards() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);

        when(cardBaseRepository.findByDeckIdDeckAndIsNewCardTrue(mockDeck.getIdDeck())).thenReturn(List.of());

        // Act
        List<CardBase> result = deckService.getNewCardsForTodayRevision(mockDeck, 5);

        // Assert
        assertTrue(result.isEmpty());
        verify(cardBaseRepository, times(1)).findByDeckIdDeckAndIsNewCardTrue(mockDeck.getIdDeck());
    }

    @Test
    void testRestoreDeck() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIsDeleted(true);

        // Act
        deckService.restoreDeck(mockDeck);

        // Assert
        assertFalse(mockDeck.getIsDeleted());
        verify(deckBaseRepository, times(1)).save(mockDeck);
    }

    @Test
    void testDeleteDeck() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIsDeleted(false);

        // Act
        deckService.deleteDeck(mockDeck);

        // Assert
        assertTrue(mockDeck.getIsDeleted());
        verify(deckBaseRepository, times(1)).save(mockDeck);
    }

    @Test
    void testGetReverseRevisionCards() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);

        CardBase card1 = new CardBase();
        CardBase card2 = new CardBase();
        List<CardBase> expectedCards = Arrays.asList(card1, card2);

        when(cardBaseRepository.findByDeckIdDeckAndIsNewCardFalseAndNextReverseRevisionDateLessThanEqual(eq(1L), any(LocalDate.class)))
                .thenReturn(expectedCards);

        // Act
        List<CardBase> result = deckService.getReverseRevisionCards(mockDeck);

        // Assert
        assertEquals(expectedCards, result);
        verify(cardBaseRepository, times(1)).findByDeckIdDeckAndIsNewCardFalseAndNextReverseRevisionDateLessThanEqual(1L, LocalDate.now());
    }

    @Test
    void testGetRegularRevisionCardsCount() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);
        int expectedCount = 5;

        when(cardBaseRepository.countByDeckIdDeckAndIsNewCardFalseAndNextRegularRevisionDateLessThanEqual(1L, LocalDate.now()))
                .thenReturn(expectedCount);

        // Act
        int result = deckService.getRegularRevisionCardsCount(mockDeck);

        // Assert
        assertEquals(expectedCount, result);
        verify(cardBaseRepository, times(1)).countByDeckIdDeckAndIsNewCardFalseAndNextRegularRevisionDateLessThanEqual(1L, LocalDate.now());
    }

    @Test
    void testGetReverseRevisionCardsCount() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);
        int expectedCount = 3;

        when(cardBaseRepository.countByDeckIdDeckAndIsNewCardFalseAndNextReverseRevisionDateLessThanEqual(1L, LocalDate.now()))
                .thenReturn(expectedCount);

        // Act
        int result = deckService.getReverseRevisionCardsCount(mockDeck);

        // Assert
        assertEquals(expectedCount, result);
        verify(cardBaseRepository, times(1)).countByDeckIdDeckAndIsNewCardFalseAndNextReverseRevisionDateLessThanEqual(1L, LocalDate.now());
    }

    @Test
    void testSaveDeck() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);

        // Act
        deckService.saveDeck(mockDeck);

        // Assert
        verify(deckBaseRepository, times(1)).save(mockDeck);
    }

    @Test
    void testGetNewCardsCount() {
        // Arrange
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(1L);

        int expectedCount = 5;
        when(cardBaseRepository.countByDeckIdDeckAndIsNewCardTrue(mockDeck.getIdDeck())).thenReturn(expectedCount);

        // Act
        int actualCount = deckService.getNewCardsCount(mockDeck);

        // Assert
        assertEquals(expectedCount, actualCount);
        verify(cardBaseRepository, times(1)).countByDeckIdDeckAndIsNewCardTrue(mockDeck.getIdDeck());
    }

    @Test
    void testFindDecksByUserNotDeleted() {
        // Arrange
        AppUser mockUser = new AppUser();
        mockUser.setIdUser(1L);

        List<DeckBase> expectedDecks = Arrays.asList(new DeckBase(), new DeckBase());
        when(deckBaseRepository.findByUserAndIsDeletedFalse(mockUser)).thenReturn(expectedDecks);

        // Act
        List<DeckBase> actualDecks = deckService.findDecksByUserNotDeleted(mockUser);

        // Assert
        assertEquals(expectedDecks, actualDecks);
        verify(deckBaseRepository, times(1)).findByUserAndIsDeletedFalse(mockUser);
    }

    @Test
    void testFindDecksByUserDeleted() {
        // Arrange
        AppUser mockUser = new AppUser();
        mockUser.setIdUser(1L);

        List<DeckBase> expectedDecks = Arrays.asList(new DeckBase(), new DeckBase());
        when(deckBaseRepository.findByUserAndIsDeletedTrue(mockUser)).thenReturn(expectedDecks);

        // Act
        List<DeckBase> actualDecks = deckService.findDecksByUserDeleted(mockUser);

        // Assert
        assertEquals(expectedDecks, actualDecks);
        verify(deckBaseRepository, times(1)).findByUserAndIsDeletedTrue(mockUser);
    }

    @Test
    void testFindById() {
        // Arrange
        Long deckId = 1L;
        DeckBase expectedDeck = new DeckBase();
        expectedDeck.setIdDeck(deckId);

        when(deckBaseRepository.findById(deckId)).thenReturn(Optional.of(expectedDeck));

        // Act
        Optional<DeckBase> actualDeck = deckService.findById(deckId);

        // Assert
        assertEquals(Optional.of(expectedDeck), actualDeck);
        verify(deckBaseRepository, times(1)).findById(deckId);
    }

    @Test
    void testFindById_DeckNotFound() {
        // Arrange
        Long deckId = 1L;

        when(deckBaseRepository.findById(deckId)).thenReturn(Optional.empty());

        // Act
        Optional<DeckBase> actualDeck = deckService.findById(deckId);

        // Assert
        assertEquals(Optional.empty(), actualDeck);
        verify(deckBaseRepository, times(1)).findById(deckId);
    }
}
