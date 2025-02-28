package gutek.domain.revisions;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.entities.decks.DeckBaseStatistics;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.DeckService;
import gutek.services.DeckStatisticsService;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevisionStrategyTest {

    private MockRevisionStrategy mockRevisionStrategy;
    private DeckService mockDeckService;
    private DeckStatisticsService mockDeckStatisticsService;
    private DeckBase mockDeck;
    private CardBase mockCard;

    @BeforeEach
    void setUp() {
        mockRevisionStrategy = new MockRevisionStrategy();
        mockDeckService = mock(DeckService.class);
        mockDeckStatisticsService = mock(DeckStatisticsService.class);
        mockDeck = mock(DeckBase.class);
        mockCard = mock(CardBase.class);
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
    void testGetRevisionStrategyTranslationKey() {
        // Arrange
        String expectedKey = "mock.revision.strategy";

        // Act
        String actualKey = mockRevisionStrategy.getRevisionStrategyTranslationKey();

        // Assert
        assertEquals(expectedKey, actualKey);
    }

    @Test
    void testGetRevisionStrategyColor() {
        // Arrange
        Color expectedColor = Color.BLUE;

        // Act
        Color actualColor = mockRevisionStrategy.getRevisionStrategyColor();

        // Assert
        assertEquals(expectedColor, actualColor);
    }

    @Test
    void testGetRevisionStrategyScene() {
        // Arrange
        MainStageScenes expectedScene = MainStageScenes.DECKS_SCENE;

        // Act
        MainStageScenes actualScene = mockRevisionStrategy.getRevisionStrategyScene();

        // Assert
        assertEquals(expectedScene, actualScene);
    }

    @Test
    void testGetRevisionStrategyCardsCount() {
        // Arrange
        when(mockDeckService.getAllCards(mockDeck)).thenReturn(null);

        // Act
        int cardCount = mockRevisionStrategy.getRevisionStrategyCardsCount(mockDeckService, mockDeck);

        // Assert
        assertEquals(42, cardCount);
    }

    @Test
    void testGetNextRevisionDate() {
        // Arrange
        LocalDate expectedDate = LocalDate.now().plusDays(1);

        // Act
        LocalDate actualDate = mockRevisionStrategy.getNextRevisionDate(mockCard);

        // Assert
        assertEquals(expectedDate, actualDate);
    }

    @Test
    void testGetRevisionButtonsPane() {
        // Arrange
        // No additional setup required

        // Act
        Pane pane = mockRevisionStrategy.getRevisionButtonsPane(mockCard);

        // Assert
        assertNotNull(pane);
    }

    @Test
    void testReviseCard() {
        // Arrange
        Button realButton = new Button("Click Me");

        // Act
        boolean result = mockRevisionStrategy.reviseCard(realButton, mockCard);

        // Assert
        assertTrue(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetRevisionCounts() {
        // Arrange
        RevisionAlgorithm<CardBase> mockAlgorithm = mock(RevisionAlgorithm.class);
        when(mockDeck.getRevisionAlgorithm()).thenReturn((RevisionAlgorithm) mockAlgorithm);
        when(mockAlgorithm.getRevisionStrategies()).thenReturn(List.of(mockRevisionStrategy));
        when(mockDeck.getDeckBaseStatistics()).thenReturn(mock(DeckBaseStatistics.class));
        when(mockDeck.getDeckBaseStatistics().getIdDeckStatistics()).thenReturn(1L);
        when(mockDeckStatisticsService.getRevisionCounts(1L, 0)).thenReturn(new int[]{1, 2, 3});

        // Act
        int[] counts = mockRevisionStrategy.getRevisionCounts(mockDeckStatisticsService, mockDeck);

        // Assert
        assertArrayEquals(new int[]{1, 2, 3}, counts);
    }
}
