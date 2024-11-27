package gutek.gui.controllers.controls;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.main.TrashFXMLController;
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

import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrashDeckCellFXMLControllerTest extends ApplicationTest {

    private TrashDeckCellFXMLController controller;
    private DeckService deckService;
    private TrashFXMLController parentController;
    private DeckBase mockDeck;

    @BeforeAll
    static void initToolkit() {
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.startup(() -> {});
            }
        } catch (IllegalStateException e) {
            // Ignore
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        deckService = mock(DeckService.class);
        TranslationService translationService = mock(TranslationService.class);
        parentController = mock(TrashFXMLController.class);
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);
        MainStage mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(parentController.getScaleFactorProperty()).thenReturn(new SimpleDoubleProperty(1.0));
        when(parentController.getCurrentLocaleProperty()).thenReturn(new SimpleObjectProperty<>(Locale.ENGLISH));
        when(translationService.getTranslation(Mockito.anyString())).thenReturn("Translated");

        controller = new TrashDeckCellFXMLController(mockStage, mockFxmlFileLoader, translationService, deckService, parentController);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/TrashDeckCellView.fxml"));
        fxmlLoader.setController(controller);
        Parent mockRoot = fxmlLoader.load();
        when(mockFxmlFileLoader.loadFXML(eq("/fxml/controls/TrashDeckCellView.fxml"), any()))
                .thenReturn(mockRoot);
        controller.loadViewFromFXML();

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(controller.getRoot()));
            stage.show();

            mockDeck = mock(DeckBase.class);
            RevisionAlgorithm mockRevisionAlgorithm = mock(RevisionAlgorithm.class);
            when(mockDeck.getRevisionAlgorithm()).thenReturn(mockRevisionAlgorithm);
            when(mockDeck.getName()).thenReturn("Mock Deck");
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
        assertEquals("Mock Deck", deckName.getText());
    }

    @Test
    void testButtonsInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Button buttonDelete = lookup("#buttonDelete").queryAs(Button.class);
        Button buttonRestore = lookup("#buttonRestore").queryAs(Button.class);

        // Assert
        assertNotNull(buttonDelete, "Delete button should be initialized");
        assertNotNull(buttonRestore, "Restore button should be initialized");

        assertEquals("Translated", buttonDelete.getText());
        assertEquals("Translated", buttonRestore.getText());
    }

    @Test
    void testHandleRestore() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Button buttonRestore = lookup("#buttonRestore").queryAs(Button.class);
        // Assert
        assertNotNull(buttonRestore, "Restore button should be present");

        // Act
        Platform.runLater(buttonRestore::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(deckService).restoreDeck(mockDeck);
        verify(parentController).removeDeckFromListView(mockDeck);
    }

    @Test
    void testHandleDelete() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Button buttonDelete = lookup("#buttonDelete").queryAs(Button.class);
        // Assert
        assertNotNull(buttonDelete, "Delete button should be present");

        // Act
        Platform.runLater(buttonDelete::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(deckService).removeDeck(mockDeck);
        verify(parentController).removeDeckFromListView(mockDeck);
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
            // Act
            Platform.runLater(controller::updateTranslation);
            WaitForAsyncUtils.waitForFxEvents();
        });

        // Act
        Label deckNameLabel = lookup("#deckNameLabel").queryAs(Label.class);
        Button buttonDelete = lookup("#buttonDelete").queryAs(Button.class);
        Button buttonRestore = lookup("#buttonRestore").queryAs(Button.class);
        // Assert
        assertEquals("Translated", deckNameLabel.getText());
        assertEquals("Translated", buttonDelete.getText());
        assertEquals("Translated", buttonRestore.getText());
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
            // Act
            Platform.runLater(controller::updateSize);
            WaitForAsyncUtils.waitForFxEvents();
        });

        // Act
        Label deckNameLabel = lookup("#deckNameLabel").queryAs(Label.class);
        Button buttonDelete = lookup("#buttonDelete").queryAs(Button.class);
        Button buttonRestore = lookup("#buttonRestore").queryAs(Button.class);
        // Assert
        assertTrue(deckNameLabel.getStyle().contains("-fx-font-size"));
        assertTrue(buttonDelete.getStyle().contains("-fx-background-color: red"));
        assertTrue(buttonRestore.getStyle().contains("-fx-background-color: green"));
    }
}
