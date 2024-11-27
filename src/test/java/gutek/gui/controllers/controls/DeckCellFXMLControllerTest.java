package gutek.gui.controllers.controls;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.main.DecksFXMLController;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeckCellFXMLControllerTest extends ApplicationTest {

    private DeckCellFXMLController controller;
    private DeckService deckService;
    private DecksFXMLController parentController;
    private DeckBase mockDeck;

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
    public void setUp() throws IOException {
        deckService = mock(DeckService.class);
        TranslationService translationService = mock(TranslationService.class);
        parentController = mock(DecksFXMLController.class);
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);
        MainStage mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(parentController.getScaleFactorProperty()).thenReturn(new SimpleDoubleProperty(1.0));
        when(parentController.getCurrentLocaleProperty()).thenReturn(new SimpleObjectProperty<>(Locale.ENGLISH));
        when(translationService.getTranslation(Mockito.anyString())).thenReturn("Translated");

        controller = new DeckCellFXMLController(mockStage, mockFxmlFileLoader, translationService, deckService, parentController);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/DeckCellView.fxml"));
        fxmlLoader.setController(controller);
        Parent mockRoot = fxmlLoader.load();
        when(mockFxmlFileLoader.loadFXML(eq("/fxml/controls/DeckCellView.fxml"), any()))
                .thenReturn(mockRoot);
        controller.loadViewFromFXML();

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(controller.getRoot()));
            stage.show();

            mockDeck = mock(DeckBase.class);
            RevisionAlgorithm mockRevisionAlgorithm = mock(RevisionAlgorithm.class);
            when(mockDeck.getRevisionAlgorithm()).thenReturn(mockRevisionAlgorithm);
            when(mockRevisionAlgorithm.getAlgorithmName()).thenReturn("Mock Algorithm");
            doNothing().when(mockRevisionAlgorithm).setTranslationService(any());

            when(mockDeck.getName()).thenReturn("Sample Deck");
            controller.setDeck(mockDeck);
        });
    }

    @Test
    void testLabelsInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Label deckNameLabel = lookup("#deckNameLabel").queryAs(Label.class);
        Label deckName = lookup("#deckName").queryAs(Label.class);

        // Assert
        assertNotNull(deckNameLabel, "Deck name label should be initialized");
        assertNotNull(deckName, "Deck name should be initialized");

        assertEquals("Translated", deckNameLabel.getText());
        assertEquals("Sample Deck", deckName.getText());
    }

    @Test
    void testButtonsInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Button deleteButton = lookup("#buttonDelete").queryAs(Button.class);
        Button openButton = lookup("#buttonOpen").queryAs(Button.class);
        Button exportButton = lookup("#buttonExport").queryAs(Button.class);

        // Assert
        assertNotNull(deleteButton, "Delete button should be initialized");
        assertNotNull(openButton, "Open button should be initialized");
        assertNotNull(exportButton, "Export button should be initialized");

        assertEquals("Translated", deleteButton.getText());
        assertEquals("Translated", openButton.getText());
        assertEquals("Translated", exportButton.getText());
    }

    @Test
    void testHandleDelete() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Button deleteButton = lookup("#buttonDelete").queryAs(Button.class);
        // Assert
        assertNotNull(deleteButton, "Delete button should be present");

        // Act
        Platform.runLater(deleteButton::fire);
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(deckService, times(1)).deleteDeck(mockDeck);
        verify(parentController, times(1)).removeDeckFromListView(mockDeck);
    }

    @Test
    void testHandleOpen() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Button openButton = lookup("#buttonOpen").queryAs(Button.class);
        // Assert
        assertNotNull(openButton, "Open button should be present");

        // Act
        Platform.runLater(openButton::fire);
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(parentController, never()).removeDeckFromListView(mockDeck);
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Label deckNameLabel = lookup("#deckNameLabel").queryAs(Label.class);
        Label deckName = lookup("#deckName").queryAs(Label.class);

        // Assert
        assertNotNull(deckNameLabel, "Deck name label should not be null");
        assertNotNull(deckName, "Deck name should not be null");

        assertTrue(deckNameLabel.getStyle().contains("-fx-font-size:"));
        assertTrue(deckName.getStyle().contains("-fx-font-size:"));
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        Label deckNameLabel = lookup("#deckNameLabel").queryAs(Label.class);
        Label deckName = lookup("#deckName").queryAs(Label.class);

        // Assert
        assertEquals("Translated", deckNameLabel.getText());
        assertEquals("Sample Deck", deckName.getText());
    }
}
