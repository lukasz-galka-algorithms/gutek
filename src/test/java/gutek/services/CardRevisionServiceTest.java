package gutek.services;

import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.cards.CardBase;
import gutek.entities.cards.CardBaseRevision;
import gutek.repositories.CardBaseRepository;
import gutek.repositories.CardBaseRevisionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardRevisionServiceTest {

    @Mock
    private CardBaseRepository cardBaseRepository;

    @Mock
    private CardBaseRevisionRepository cardBaseRevisionRepository;

    @Mock
    private RevisionStrategy<?> revisionStrategy;

    private CardRevisionService cardRevisionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cardRevisionService = new CardRevisionService(cardBaseRevisionRepository, cardBaseRepository);
    }

    @Test
    void testRevise_WhenCardExists() {
        // Arrange
        Long cardId = 1L;
        Integer pressedButtonIndex = 2;

        CardBase mockCard = new CardBase();
        mockCard.setIdCard(cardId);

        CardBase mockCardFromRepo = new CardBase();
        mockCardFromRepo.setIdCard(cardId);

        when(cardBaseRepository.findById(cardId)).thenReturn(Optional.of(mockCardFromRepo));

        // Act
        cardRevisionService.revise(mockCard, pressedButtonIndex, revisionStrategy);

        // Assert
        ArgumentCaptor<CardBaseRevision> revisionCaptor = ArgumentCaptor.forClass(CardBaseRevision.class);
        verify(cardBaseRevisionRepository, times(1)).save(revisionCaptor.capture());

        CardBaseRevision capturedRevision = revisionCaptor.getValue();

        assertNotNull(capturedRevision);
        assertEquals(LocalDate.now(), capturedRevision.getRevisionDate());
        assertEquals(mockCardFromRepo, capturedRevision.getCardBase());
        assertEquals(pressedButtonIndex, capturedRevision.getPressedButtonIndex());
    }

    @Test
    void testRevise_WhenCardDoesNotExist() {
        // Arrange
        Long cardId = 1L;
        CardBase mockCard = new CardBase();
        mockCard.setIdCard(cardId);

        when(cardBaseRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act
        cardRevisionService.revise(mockCard, 2, revisionStrategy);

        // Assert
        verify(cardBaseRevisionRepository, never()).save(any());
    }
}