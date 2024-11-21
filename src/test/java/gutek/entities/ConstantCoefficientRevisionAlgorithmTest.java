package gutek.entities;

import gutek.entities.algorithms.ConstantCoefficientRevisionAlgorithm;
import gutek.entities.cards.CardConstantCoefficient;
import gutek.services.TranslationService;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

class ConstantCoefficientRevisionAlgorithmTest {

    private ConstantCoefficientRevisionAlgorithm algorithm;
    private TranslationService mockTranslationService;
    private CardConstantCoefficient mockCard;

    @BeforeEach
    void setUp() {
        mockTranslationService = mock(TranslationService.class);
        algorithm = new ConstantCoefficientRevisionAlgorithm();
        algorithm.setTranslationService(mockTranslationService);
        algorithm.initializeDefaultHiperparameters();
        algorithm.initializeGUI(800, 600, 1.0);
        mockCard = mock(CardConstantCoefficient.class);
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
        assertEquals(0.25, algorithm.getCoefficient1());
        assertEquals(0.5, algorithm.getCoefficient2());
        assertEquals(1.0, algorithm.getCoefficient3());
        assertEquals(1.5, algorithm.getCoefficient4());
        assertEquals(5, algorithm.getIncorrectAnswerThreshold());
        assertEquals(0.25, algorithm.getReverseCoefficient1());
        assertEquals(1.5, algorithm.getReverseCoefficient2());
        assertEquals(5, algorithm.getReverseIncorrectAnswerThreshold());
    }

    @Test
    void testUpdateTranslation() {
        // Arrange
        when(mockTranslationService.getTranslation("revision_algorithm.const_coeff.normal_button_1")).thenReturn("Button 1");
        when(mockTranslationService.getTranslation("revision_algorithm.const_coeff.normal_button_2")).thenReturn("Button 2");

        // Act
        algorithm.updateTranslation();

        // Assert
        assertEquals("Button 1", algorithm.getButton1().getText());
        assertEquals("Button 2", algorithm.getButton2().getText());
    }

    @Test
    void testUpdateSize() {
        // Act
        algorithm.updateSize(800, 100, 2.0);

        // Assert button sizes
        assertEquals(200.0, algorithm.getButton1().getPrefWidth());
        assertEquals(100.0, algorithm.getButton1().getPrefHeight());
    }

    @Test
    void testRegularRevisionButtonPanel() {
        // Act
        Pane pane = algorithm.getRegularRevisionButtonsPane(mockCard);

        // Assert
        assertNotNull(pane);
        assertEquals(4, pane.getChildren().size());
    }

    @Test
    void testReverseRevisionButtonPanel() {
        // Act
        Pane pane = algorithm.getReverseRevisionButtonsPane(mockCard);

        // Assert
        assertNotNull(pane);
        assertEquals(2, pane.getChildren().size());
    }

    @Test
    void testRegularReviseCardButton1() {
        // Arrange
        when(mockCard.getBaseRevisionTime()).thenReturn(10.0);
        when(mockCard.getIncorrectCounter()).thenReturn(0);

        // Act
        boolean result = algorithm.regularReviseCard(algorithm.getButton1(), mockCard);

        // Assert
        assertFalse(result);
        verify(mockCard).setBaseRevisionTime(2.5); // coefficient1 * baseRevisionTime
        verify(mockCard).setIncorrectCounter(1);
        verify(mockCard).setNextRegularRevisionDate(LocalDate.now());
    }

    @Test
    void testRegularReviseCardButton2() {
        // Arrange
        when(mockCard.getBaseRevisionTime()).thenReturn(10.0);

        // Act
        boolean result = algorithm.regularReviseCard(algorithm.getButton2(), mockCard);

        // Assert
        assertTrue(result);
        verify(mockCard).setBaseRevisionTime(5.0); // coefficient2 * baseRevisionTime
        verify(mockCard).setNextRegularRevisionDate(LocalDate.now().plusDays(10));
    }

    @Test
    void testReverseReviseCardButton1() {
        // Arrange
        when(mockCard.getBaseReverseRevisionTime()).thenReturn(8.0);
        when(mockCard.getReverseIncorrectCounter()).thenReturn(0);

        // Act
        boolean result = algorithm.reverseReviseCard(algorithm.getReverseButton1(), mockCard);

        // Assert
        assertFalse(result);
        verify(mockCard).setBaseReverseRevisionTime(2.0); // reverseCoefficient1 * baseReverseRevisionTime
        verify(mockCard).setReverseIncorrectCounter(1);
        verify(mockCard).setNextReverseRevisionDate(LocalDate.now());
    }

    @Test
    void testReverseReviseCardButton2() {
        // Arrange
        when(mockCard.getBaseReverseRevisionTime()).thenReturn(8.0);

        // Act
        boolean result = algorithm.reverseReviseCard(algorithm.getReverseButton2(), mockCard);

        // Assert
        assertTrue(result);
        verify(mockCard).setBaseReverseRevisionTime(12.0); // reverseCoefficient2 * baseReverseRevisionTime
        verify(mockCard).setNextReverseRevisionDate(LocalDate.now().plusDays(8));
    }

    @Test
    void testGetAlgorithmName() {
        // Arrange
        when(mockTranslationService.getTranslation("revision_algorithm.const_coeff.algorithm_name"))
                .thenReturn("Constant Coefficient Algorithm");

        // Act
        String name = algorithm.getAlgorithmName();

        // Assert
        assertEquals("Constant Coefficient Algorithm", name);
    }

    @Test
    void testCreateNewCard() {
        // Act
        CardConstantCoefficient card = algorithm.createNewCard("Front", "Back");

        // Assert
        assertNotNull(card);
        assertEquals("Front", card.getFront());
        assertEquals("Back", card.getBack());
    }
}
