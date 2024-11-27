package gutek.entities;

import gutek.entities.algorithms.SuperMemo2RevisionAlgorithm;
import gutek.entities.cards.CardSuperMemo2;
import gutek.services.TranslationService;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

class SuperMemo2RevisionAlgorithmTest {

    private SuperMemo2RevisionAlgorithm algorithm;
    private TranslationService mockTranslationService;
    private CardSuperMemo2 mockCard;

    @BeforeEach
    void setUp() {
        mockTranslationService = mock(TranslationService.class);
        algorithm = new SuperMemo2RevisionAlgorithm();
        algorithm.setTranslationService(mockTranslationService);
        algorithm.initializeDefaultHiperparameters();
        algorithm.initializeGUI(800, 600, 1.0);
        mockCard = mock(CardSuperMemo2.class);
    }

    @BeforeAll
    static void initToolkit() {
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.startup(() -> {});
            }
        } catch (IllegalStateException e) {
            // ignore
        }
    }

    @Test
    void testInitializeDefaultHiperparameters() {
        // Assert default hyperparameters
        assertEquals(2.5, algorithm.getInitialEasinessFactor());
        assertEquals(3, algorithm.getIncorrectAnswerThreshold());
        assertEquals(2.5, algorithm.getReverseInitialEasinessFactor());
        assertEquals(3, algorithm.getReverseIncorrectAnswerThreshold());
    }

    @Test
    void testUpdateTranslation() {
        // Arrange
        when(mockTranslationService.getTranslation("revision_algorithm.supermemo2.normal_button_1")).thenReturn("Grade 1");
        when(mockTranslationService.getTranslation("revision_algorithm.supermemo2.reverse_button_1")).thenReturn("Reverse Grade 1");

        // Act
        algorithm.updateTranslation();

        // Assert
        assertEquals("Grade 1", algorithm.getButtonGrade1().getText());
        assertEquals("Reverse Grade 1", algorithm.getReverseButtonGrade1().getText());
    }

    @Test
    void testUpdateSize() {
        // Act
        algorithm.updateSize(1000, 100, 2.0);

        // Assert
        assertEquals(200.0, algorithm.getButtonGrade1().getPrefWidth());
        assertEquals(100.0, algorithm.getButtonGrade1().getPrefHeight());
    }

    @Test
    void testGetRegularRevisionButtonsPane() {
        // Act
        Pane pane = algorithm.getRegularRevisionButtonsPane(mockCard);

        // Assert
        assertNotNull(pane);
        assertEquals(5, pane.getChildren().size());
    }

    @Test
    void testGetReverseRevisionButtonsPane() {
        // Act
        Pane pane = algorithm.getReverseRevisionButtonsPane(mockCard);

        // Assert
        assertNotNull(pane);
        assertEquals(5, pane.getChildren().size());
    }

    @Test
    void testRegularReviseCard_GoodGrade() {
        // Arrange
        when(mockCard.getRepetition()).thenReturn(2);
        when(mockCard.getRegularInterval()).thenReturn(6);
        when(mockCard.getEasinessFactor()).thenReturn(2.5);

        // Act
        boolean result = algorithm.regularReviseCard(algorithm.getButtonGrade4(), mockCard);

        // Assert
        assertTrue(result);
        verify(mockCard).setRepetition(3);
        verify(mockCard).setRegularInterval(6);
        verify(mockCard).setEasinessFactor(2.5);
        verify(mockCard).setNextRegularRevisionDate(LocalDate.now().plusDays(6));
    }

    @Test
    void testRegularReviseCard_BadGrade() {
        // Arrange
        when(mockCard.getIncorrectCounter()).thenReturn(2);

        // Act
        boolean result = algorithm.regularReviseCard(algorithm.getButtonGrade2(), mockCard);

        // Assert
        assertTrue(result);
        verify(mockCard).setIncorrectCounter(3);
        verify(mockCard).setNextRegularRevisionDate(LocalDate.now().plusDays(1));
    }

    @Test
    void testReverseReviseCard_GoodGrade() {
        // Arrange
        when(mockCard.getReverseRepetition()).thenReturn(1);
        when(mockCard.getReverseInterval()).thenReturn(6);
        when(mockCard.getReverseEasinessFactor()).thenReturn(2.5);

        // Act
        boolean result = algorithm.reverseReviseCard(algorithm.getReverseButtonGrade4(), mockCard);

        // Assert
        assertTrue(result);
        verify(mockCard).setReverseRepetition(2);
        verify(mockCard).setReverseInterval(1);
        verify(mockCard).setReverseEasinessFactor(2.5);
        verify(mockCard).setNextReverseRevisionDate(LocalDate.now().plusDays(6));
    }

    @Test
    void testReverseReviseCard_BadGrade() {
        // Arrange
        when(mockCard.getReverseIncorrectCounter()).thenReturn(2);

        // Act
        boolean result = algorithm.reverseReviseCard(algorithm.getReverseButtonGrade2(), mockCard);

        // Assert
        assertTrue(result);
        verify(mockCard).setReverseIncorrectCounter(3);
        verify(mockCard).setNextReverseRevisionDate(LocalDate.now().plusDays(1));
    }

    @Test
    void testGetAlgorithmName() {
        // Arrange
        when(mockTranslationService.getTranslation("revision_algorithm.supermemo2.algorithm_name"))
                .thenReturn("SuperMemo2 Algorithm");

        // Act
        String name = algorithm.getAlgorithmName();

        // Assert
        assertEquals("SuperMemo2 Algorithm", name);
    }

    @Test
    void testCreateNewCard() {
        // Act
        CardSuperMemo2 card = algorithm.createNewCard("Front", "Back");

        // Assert
        assertNotNull(card);
        assertEquals("Front", card.getFront());
        assertEquals("Back", card.getBack());
        assertEquals(2.5, card.getEasinessFactor());
        assertEquals(2.5, card.getReverseEasinessFactor());
    }
}
