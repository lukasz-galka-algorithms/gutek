package gutek.services;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.repositories.CardBaseRepository;
import gutek.repositories.CardBaseRevisionRepository;
import gutek.repositories.DeckBaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardBaseRepository cardBaseRepository;

    @Mock
    private DeckBaseRepository deckBaseRepository;

    @Mock
    private CardBaseRevisionRepository cardBaseRevisionRepository;

    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cardService = new CardService(cardBaseRepository, deckBaseRepository, cardBaseRevisionRepository);
    }

    @Test
    void testRemoveCard_WhenDeckAndCardExist() {
        // Arrange
        Long deckId = 1L;
        Long cardId = 2L;

        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(deckId);

        CardBase mockCard = new CardBase();
        mockCard.setIdCard(cardId);
        mockCard.setDeck(mockDeck);

        when(deckBaseRepository.findById(deckId)).thenReturn(Optional.of(mockDeck));
        when(cardBaseRepository.findById(cardId)).thenReturn(Optional.of(mockCard));

        // Act
        cardService.removeCard(mockCard);

        // Assert
        verify(deckBaseRepository, times(1)).save(mockDeck);
        verify(cardBaseRepository, times(1)).delete(mockCard);
        verify(cardBaseRevisionRepository, times(1)).deleteByCardBase(mockCard);
    }

    @Test
    void testRemoveCard_WhenDeckDoesNotExist() {
        // Arrange
        Long deckId = 1L;
        CardBase mockCard = new CardBase();
        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(deckId);
        mockCard.setDeck(mockDeck);

        when(deckBaseRepository.findById(deckId)).thenReturn(Optional.empty());

        // Act
        cardService.removeCard(mockCard);

        // Assert
        verify(deckBaseRepository, never()).save(any());
        verify(cardBaseRepository, never()).delete(any());
        verify(cardBaseRevisionRepository, never()).deleteByCardBase(any());
    }

    @Test
    void testRemoveCard_WhenDeckExistsButCardDoesNotExist() {
        // Arrange
        Long deckId = 1L;
        Long cardId = 2L;

        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(deckId);

        CardBase mockCard = new CardBase();
        mockCard.setIdCard(cardId);
        mockCard.setDeck(mockDeck);

        when(deckBaseRepository.findById(deckId)).thenReturn(Optional.of(mockDeck));
        when(cardBaseRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act
        cardService.removeCard(mockCard);

        // Assert
        verify(deckBaseRepository, times(1)).save(mockDeck);
        verify(cardBaseRepository, never()).delete(any());
        verify(cardBaseRevisionRepository, never()).deleteByCardBase(any());
    }

    @Test
    void testAddNewCard_WhenDeckExists() {
        // Arrange
        Long deckId = 1L;
        String frontText = "Front";
        String backText = "Back";

        DeckBase mockDeck = mock(DeckBase.class);
        RevisionAlgorithm<CardBase> mockRevisionAlgorithm = mock(RevisionAlgorithm.class);

        when(mockDeck.getIdDeck()).thenReturn(deckId);
        when(deckBaseRepository.findById(deckId)).thenReturn(Optional.of(mockDeck));
        when(mockDeck.getRevisionAlgorithm()).thenReturn( (RevisionAlgorithm) mockRevisionAlgorithm);

        CardBase mockNewCard = new CardBase();
        mockNewCard.setFront(frontText);
        mockNewCard.setBack(backText);
        when(mockRevisionAlgorithm.createNewCard(frontText, backText)).thenReturn(mockNewCard);

        // Act
        cardService.addNewCard(frontText, backText, mockDeck);

        // Assert
        verify(cardBaseRepository, times(1)).save(mockNewCard);
        verify(deckBaseRepository, times(1)).save(mockDeck);
    }

    @Test
    void testAddNewCard_WhenDeckDoesNotExist() {
        // Arrange
        Long deckId = 1L;
        String frontText = "Front";
        String backText = "Back";

        DeckBase mockDeck = new DeckBase();
        mockDeck.setIdDeck(deckId);

        when(deckBaseRepository.findById(deckId)).thenReturn(Optional.empty());

        // Act
        cardService.addNewCard(frontText, backText, mockDeck);

        // Assert
        verify(cardBaseRepository, never()).save(any());
        verify(deckBaseRepository, never()).save(any());
    }

    @Test
    void testSaveCard() {
        // Arrange
        CardBase mockCard = mock(CardBase.class);

        // Act
        cardService.saveCard(mockCard);

        // Assert
        verify(cardBaseRepository, times(1)).save(mockCard);
    }

    @Test
    void testFindCardsByUser_WhenBothPhrasesAreEmpty() {
        // Arrange
        DeckBase mockDeck = new DeckBase();

        when(cardBaseRepository.findByDeck(mockDeck)).thenReturn(List.of(new CardBase()));

        // Act
        List<CardBase> result = cardService.findCardsByUser("", "", mockDeck);

        // Assert
        assertNotNull(result);
        verify(cardBaseRepository, times(1)).findByDeck(mockDeck);
    }

    @Test
    void testFindCardsByUser_WhenOnlyFrontPhraseIsProvided() {
        // Arrange
        String phraseInFront = "Front";
        DeckBase mockDeck = new DeckBase();

        when(cardBaseRepository.findByFrontContainingAndDeck(phraseInFront, mockDeck))
                .thenReturn(List.of(new CardBase()));

        // Act
        List<CardBase> result = cardService.findCardsByUser(phraseInFront, "", mockDeck);

        // Assert
        assertNotNull(result);
        verify(cardBaseRepository, times(1)).findByFrontContainingAndDeck(phraseInFront, mockDeck);
    }

    @Test
    void testFindCardsByUser_WhenOnlyBackPhraseIsProvided() {
        // Arrange
        String phraseInBack = "Back";
        DeckBase mockDeck = new DeckBase();

        when(cardBaseRepository.findByBackContainingAndDeck(phraseInBack, mockDeck))
                .thenReturn(List.of(new CardBase()));

        // Act
        List<CardBase> result = cardService.findCardsByUser("", phraseInBack, mockDeck);

        // Assert
        assertNotNull(result);
        verify(cardBaseRepository, times(1)).findByBackContainingAndDeck(phraseInBack, mockDeck);
    }

    @Test
    void testFindCardsByUser_WhenFrontAndBackAreProvided() {
        // Arrange
        String phraseInFront = "Front";
        String phraseInBack = "Back";
        DeckBase mockDeck = mock(DeckBase.class);
        List<CardBase> mockCards = Arrays.asList(mock(CardBase.class), mock(CardBase.class));

        when(cardBaseRepository.findByFrontContainingAndBackContainingAndDeck(phraseInFront, phraseInBack, mockDeck))
                .thenReturn(mockCards);

        // Act
        List<CardBase> result = cardService.findCardsByUser(phraseInFront, phraseInBack, mockDeck);

        // Assert
        assertEquals(mockCards, result);
    }

    @Test
    void testFindCardByFrontAndDeck() {
        // Arrange
        String frontText = "Front";
        DeckBase mockDeck = new DeckBase();
        CardBase mockCard = new CardBase();

        when(cardBaseRepository.findByFrontAndDeck(frontText, mockDeck)).thenReturn(Optional.of(mockCard));

        // Act
        Optional<CardBase> result = cardService.findCardByFrontAndDeck(frontText, mockDeck);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockCard, result.get());
        verify(cardBaseRepository, times(1)).findByFrontAndDeck(frontText, mockDeck);
    }
}
