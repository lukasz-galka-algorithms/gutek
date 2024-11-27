package gutek.gui.controllers.deck;

import gutek.domain.revisions.ReverseTextModeRevisionStrategy;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.entities.decks.DeckBaseStatistics;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.*;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevisionReverseFXMLControllerTest extends ApplicationTest {

    private RevisionReverseFXMLController controller;
    private DeckService mockDeckService;
    private MenuBarFXMLController mockMenuBarFXMLController;
    private MenuDeckFXMLController mockMenuDeckFXMLController;
    private MainStage mockStage;

    private DeckBase mockDeck;
    private Parent mockRoot;

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

    @BeforeEach
    void setUp() throws Exception {
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);
        TranslationService mockTranslationService = mock(TranslationService.class);
        mockDeckService = mock(DeckService.class);
        CardService mockCardService = mock(CardService.class);
        DeckStatisticsService mockDeckStatisticsService = mock(DeckStatisticsService.class);
        CardRevisionService mockCardRevisionService = mock(CardRevisionService.class);
        mockMenuBarFXMLController = mock(MenuBarFXMLController.class);
        mockMenuDeckFXMLController = mock(MenuDeckFXMLController.class);
        mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");
        when(mockMenuBarFXMLController.getRoot()).thenReturn(new Label("MenuBar"));
        when(mockMenuDeckFXMLController.getRoot()).thenReturn(new Label("MenuDeck"));

        mockDeck = mock(DeckBase.class);
        CardBase card1 = mock(CardBase.class);
        when(card1.getDeck()).thenReturn(mockDeck);
        when(card1.getFront()).thenReturn("Card front");
        when(card1.getBack()).thenReturn("Card back");
        List<CardBase> mockOldCardsList = List.of(card1);
        CardBase card2 = mock(CardBase.class);
        when(card2.getDeck()).thenReturn(mockDeck);
        when(card2.getFront()).thenReturn("Card front");
        when(card2.getBack()).thenReturn("Card back");
        List<CardBase> mockNewCardsList = List.of(card2);

        when(mockDeckService.getRegularRevisionCards(mockDeck)).thenReturn(mockOldCardsList);
        when(mockDeckService.getNewCardsForTodayRevision(eq(mockDeck), anyInt())).thenReturn(mockNewCardsList);
        DeckBaseStatistics deckBaseStatistics = mock(DeckBaseStatistics.class);
        when(mockDeck.getDeckBaseStatistics()).thenReturn(deckBaseStatistics);
        RevisionAlgorithm mockAlgorithm = mock(RevisionAlgorithm.class);
        when(mockDeck.getRevisionAlgorithm()).thenReturn(mockAlgorithm);
        ReverseTextModeRevisionStrategy mockRevisionStrategy = mock(ReverseTextModeRevisionStrategy.class);
        when(mockAlgorithm.getAvailableRevisionStrategies()).thenReturn(List.of(mockRevisionStrategy));
        Pane mockPane = new HBox();
        Button mockButton = new Button("Mock Button");
        mockPane.getChildren().add(mockButton);
        when(mockRevisionStrategy.getRevisionButtonsPane(any())).thenReturn(mockPane);

        controller = new RevisionReverseFXMLController(mockStage, mockFxmlFileLoader,
                mockTranslationService, mockMenuBarFXMLController, mockMenuDeckFXMLController,
                mockCardService, mockDeckStatisticsService, mockCardRevisionService, mockDeckService);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deck/RevisionReverseView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/deck/RevisionReverseView.fxml"), any()))
                .thenReturn(mockRoot);

        controller.loadViewFromFXML();

        Platform.runLater(() -> {
            Stage stage = new Stage();
            when(mockStage.getStage()).thenReturn(stage);
            stage.setScene(new Scene(mockRoot));
            stage.show();

            WaitForAsyncUtils.waitForFxEvents();
            controller.initWithParams(mockDeck);
        });
    }

    @Test
    void testLabelsInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(controller::updateTranslation);
        WaitForAsyncUtils.waitForFxEvents();

        Label translationLabel = lookup("#translationLabel").queryAs(Label.class);
        // Assert
        assertNotNull(translationLabel, "Translation label should be initialized");
        assertEquals("Card back", translationLabel.getText(), "Translation label should display card back");

        // Act
        TextArea wordTextField = (TextArea) controller.getRoot().lookup(".text-area");
        // Assert
        assertNotNull(wordTextField, "Word text field should be initialized");
        assertEquals("", wordTextField.getText(), "Word text field should be empty");
    }

    @Test
    void testShowButtonRevealsWord() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.loadNextCard());
        WaitForAsyncUtils.waitForFxEvents();

        Button showButton = lookup(".button").queryAs(Button.class);
        // Assert
        assertNotNull(showButton, "Show button should be initialized");

        // Act
        Platform.runLater(showButton::fire);
        WaitForAsyncUtils.waitForFxEvents();

        Optional<Label> wordLabel = controller.getRoot()
                .lookupAll(".label").stream()
                .map(node -> (Label) node)
                .filter(label -> "Card front".equals(label.getText()))
                .findFirst();
        // Assert
        assertTrue(wordLabel.isPresent());
    }

    @Test
    void testEndRevisionSession() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> {
            // Arrange
            when(mockDeckService.getReverseRevisionCards(mockDeck)).thenReturn(List.of());
            when(mockDeckService.getNewCardsForTodayRevision(eq(mockDeck), anyInt())).thenReturn(List.of());
            // Act
            controller.initWithParams(mockDeck);
        });
        WaitForAsyncUtils.waitForFxEvents();

        Button endRevisionButton = lookup(".button").queryAs(Button.class);
        // Assert
        assertNotNull(endRevisionButton, "End revision button should be initialized");

        // Act
        Platform.runLater(endRevisionButton::fire);
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockStage, atLeastOnce()).setScene(MainStageScenes.REVISION_REVISE_SCENE);
    }

    @Test
    void testUpdateSizeUpdatesComponents() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Optional<Label> wordLabel = controller.getRoot()
                .lookupAll(".label").stream()
                .map(node -> (Label) node)
                .filter(label -> "".equals(label.getText()))
                .findFirst();
        // Assert
        assertTrue(wordLabel.isPresent());

        // Act
        Label translationLabel = lookup("#translationLabel").queryAs(Label.class);
        // Assert
        assertTrue(wordLabel.get().getStyle().contains("-fx-font-size"), "Font size should be updated");
        assertTrue(translationLabel.getStyle().contains("-fx-font-size"), "Font size should be updated");

        verify(mockMenuBarFXMLController, times(1)).updateSize();
        verify(mockMenuDeckFXMLController, times(1)).updateSize();
    }

    @Test
    void testUpdateTranslationUpdatesButtonLabels() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> {
            controller.initWithParams(mockDeck);
            controller.updateTranslation();
        });
        WaitForAsyncUtils.waitForFxEvents();

        Button showButton = (Button) controller.getRoot().lookup(".button");
        // Assert
        assertNotNull(showButton, "Show button should be initialized");
        assertEquals("Translated", showButton.getText(), "Show button label should be 'Translated'");

        // Act
        Platform.runLater(() -> {
            // Arrange
            when(mockDeckService.getRegularRevisionCards(mockDeck)).thenReturn(List.of());
            when(mockDeckService.getNewCardsForTodayRevision(eq(mockDeck), anyInt())).thenReturn(List.of());
            // Act
            controller.initWithParams(mockDeck);
            controller.updateTranslation();
        });
        WaitForAsyncUtils.waitForFxEvents();

        Button endRevisionButton = (Button) controller.getRoot().lookup(".button");
        // Assert
        assertNotNull(endRevisionButton, "End revision button should be initialized");
        assertEquals("Translated", endRevisionButton.getText(), "End revision button label should be 'Translated'");

        verify(mockMenuBarFXMLController, times(2)).updateTranslation();
        verify(mockMenuDeckFXMLController, times(2)).updateTranslation();
    }
}
