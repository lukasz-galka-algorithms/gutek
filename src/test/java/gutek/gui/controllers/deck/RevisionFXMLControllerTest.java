package gutek.gui.controllers.deck;

import gutek.domain.revisions.RevisionStrategy;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import gutek.entities.decks.DeckBaseStatistics;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.DeckService;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import gutek.gui.controls.NumberTextField;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

class RevisionFXMLControllerTest extends ApplicationTest {

    private RevisionFXMLController controller;
    private TranslationService mockTranslationService;
    private DeckStatisticsService mockDeckStatisticsService;
    private MenuBarFXMLController mockMenuBarController;
    private MenuDeckFXMLController mockMenuDeckController;
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
        mockTranslationService = mock(TranslationService.class);
        mockDeckStatisticsService = mock(DeckStatisticsService.class);
        DeckService mockDeckService = mock(DeckService.class);
        mockMenuBarController = mock(MenuBarFXMLController.class);
        mockMenuDeckController = mock(MenuDeckFXMLController.class);
        mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");
        when(mockMenuBarController.getRoot()).thenReturn(new Label("MenuBar"));
        when(mockMenuDeckController.getRoot()).thenReturn(new Label("MenuDeck"));

        controller = new RevisionFXMLController(mockStage, mockFxmlFileLoader,
                mockTranslationService, mockMenuBarController, mockMenuDeckController,
                mockDeckService, mockDeckStatisticsService);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deck/RevisionView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/deck/RevisionView.fxml"), any()))
                .thenReturn(mockRoot);

        controller.loadViewFromFXML();

        mockDeck = mock(DeckBase.class);
        DeckBaseStatistics mockDeckStatistics = mock(DeckBaseStatistics.class);

        when(mockDeck.getDeckBaseStatistics()).thenReturn(mockDeckStatistics);
        when(mockDeckStatisticsService.loadDeckStatistics(anyLong())).thenReturn(Optional.of(mockDeckStatistics));
        when(mockDeckStatistics.getNewCardsPerDay()).thenReturn(10);

        RevisionAlgorithm mockAlgorithm = mock(RevisionAlgorithm.class);
        when(mockDeck.getRevisionAlgorithm()).thenReturn(mockAlgorithm);
        RevisionStrategy mockStrategy = mock(RevisionStrategy.class);
        when(mockAlgorithm.getAvailableRevisionStrategies()).thenReturn(List.of(mockStrategy));

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
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        Label newCardsPerDayLabel = lookup("#newCardsPerDayLabel").queryAs(Label.class);
        // Assert
        assertNotNull(newCardsPerDayLabel, "New cards per day label should be initialized");
        assertEquals("Translated", newCardsPerDayLabel.getText());
    }

    @Test
    void testNewCardsPerDayTextFieldInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        NumberTextField newCardsPerDayTextField = lookup("#newCardsPerDayTextField").queryAs(NumberTextField.class);
        // Assert
        assertNotNull(newCardsPerDayTextField, "New cards per day text field should be initialized");
        assertEquals("10", newCardsPerDayTextField.getText());
    }

    @Test
    void testUpdateNewCardsPerDay() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        NumberTextField newCardsPerDayTextField = lookup("#newCardsPerDayTextField").queryAs(NumberTextField.class);

        Platform.runLater(() -> newCardsPerDayTextField.setText("15"));
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockDeckStatisticsService).saveDeckStatistics(any());
    }

    @Test
    void testUpdateSizeUpdatesComponents() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Label newCardsPerDayLabel = lookup("#newCardsPerDayLabel").queryAs(Label.class);

        // Assert
        assertTrue(newCardsPerDayLabel.getStyle().contains("-fx-font-size"), "Font size should be updated");

        verify(mockMenuBarController, times(1)).updateSize();
        verify(mockMenuDeckController, times(1)).updateSize();
    }

    @Test
    void testUpdateTranslationUpdatesButtonLabels() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockTranslationService).getTranslation("deck_view.revise.new_cards_per_day");
    }
}
