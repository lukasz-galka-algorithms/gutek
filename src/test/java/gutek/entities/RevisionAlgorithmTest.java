package gutek.entities;

import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.cards.CardBase;
import gutek.services.TranslationService;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevisionAlgorithmTest {

    private ConcreteRevisionAlgorithm algorithm;
    private TranslationService mockTranslationService;

    @BeforeEach
    void setUp() {
        mockTranslationService = mock(TranslationService.class);
        algorithm = new ConcreteRevisionAlgorithm();
        algorithm.setTranslationService(mockTranslationService);
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
    void testGetAlgorithmName() {
        // Arrange
        String expectedName = "Concrete Revision Algorithm";

        // Act
        String actualName = algorithm.getAlgorithmName();

        // Assert
        assertEquals(expectedName, actualName);
    }

    @Test
    void testCreateNewCard() {
        // Arrange
        String front = "Front Content";
        String back = "Back Content";

        // Act
        CardBase card = algorithm.createNewCard(front, back);

        // Assert
        assertNotNull(card);
        assertEquals(front, card.getFront());
        assertEquals(back, card.getBack());
    }

    @Test
    void testInitializeDefaultRevisionStrategies() {
        // Act
        algorithm.initializeDefaultRevisionStrategies();

        // Assert
        List<RevisionStrategy<CardBase>> strategies = algorithm.getAvailableRevisionStrategies();
        assertFalse(strategies.isEmpty());
        assertEquals(1, strategies.size());
    }

    @Test
    void testUpdateTranslation() {
        // Act
        algorithm.updateTranslation();

        // Assert
        verify(mockTranslationService, atLeastOnce()).getTranslation(anyString());
    }

    @Test
    void testGetAvailableRevisionStrategies() {
        // Act
        List<RevisionStrategy<CardBase>> strategies = algorithm.getAvailableRevisionStrategies();

        // Assert
        assertNotNull(strategies);
        assertEquals(1, strategies.size());
    }
}
