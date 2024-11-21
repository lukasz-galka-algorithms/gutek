package gutek.domain.revisions;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.DeckService;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegularTextModeRevisionStrategyTest {

    private RegularTextModeRevisionStrategy<CardBase> revisionStrategy;
    private DeckService mockDeckService;
    private DeckBase mockDeckBase;
    private CardBase mockCard;

    @BeforeEach
    void setUp() {
        Function<CardBase, Pane> mockPaneFunction = card -> new Pane();
        BiPredicate<Button, CardBase> mockPredicate = (button, card) -> true;

        revisionStrategy = new RegularTextModeRevisionStrategy<>(mockPaneFunction, mockPredicate);
        mockDeckService = mock(DeckService.class);
        mockDeckBase = mock(DeckBase.class);
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
        String expectedKey = "regular_text_mode";

        // Act
        String actualKey = revisionStrategy.getRevisionStrategyTranslationKey();

        // Assert
        assertEquals(expectedKey, actualKey);
    }

    @Test
    void testGetRevisionStrategyColor() {
        // Arrange
        Color expectedColor = Color.MAGENTA;

        // Act
        Color actualColor = revisionStrategy.getRevisionStrategyColor();

        // Assert
        assertEquals(expectedColor, actualColor);
    }

    @Test
    void testGetRevisionStrategyScene() {
        // Arrange
        MainStageScenes expectedScene = MainStageScenes.REVISION_REGULAR_SCENE;

        // Act
        MainStageScenes actualScene = revisionStrategy.getRevisionStrategyScene();

        // Assert
        assertEquals(expectedScene, actualScene);
    }

    @Test
    void testGetRevisionStrategyCardsCount() {
        // Arrange
        int expectedCount = 5;
        when(mockDeckService.getRegularRevisionCardsCount(mockDeckBase)).thenReturn(expectedCount);

        // Act
        int actualCount = revisionStrategy.getRevisionStrategyCardsCount(mockDeckService, mockDeckBase);

        // Assert
        assertEquals(expectedCount, actualCount);
    }

    @Test
    void testGetNextRevisionDate() {
        // Arrange
        LocalDate expectedDate = LocalDate.now().plusDays(1);
        when(mockCard.getNextRegularRevisionDate()).thenReturn(expectedDate);

        // Act
        LocalDate actualDate = revisionStrategy.getNextRevisionDate(mockCard);

        // Assert
        assertEquals(expectedDate, actualDate);
    }

    @Test
    void testGetRevisionButtonsPane() {
        // Arrange
        // No specific setup required

        // Act
        Pane pane = revisionStrategy.getRevisionButtonsPane(mockCard);

        // Assert
        assertNotNull(pane);
    }

    @Test
    void testReviseCard() {
        // Arrange
        Button mockButton = new Button("Click Me");

        // Act
        boolean result = revisionStrategy.reviseCard(mockButton, mockCard);

        // Assert
        assertTrue(result);
    }
}
