package gutek.gui.controllers.main;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrashFXMLControllerTest extends ApplicationTest {

    private TrashFXMLController controller;
    private TranslationService mockTranslationService;
    private MenuBarFXMLController mockMenuBarFXMLController;
    private MainStage mockStage;

    @BeforeAll
    static void initToolkit() {
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.startup(() -> {
                });
            }
        } catch (IllegalStateException e) {
            // ignore
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);
        mockTranslationService = mock(TranslationService.class);
        DeckService mockDeckService = mock(DeckService.class);
        mockMenuBarFXMLController = mock(MenuBarFXMLController.class);
        mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");

        controller = new TrashFXMLController(
                mockStage,
                mockFxmlFileLoader,
                mockTranslationService,
                mockMenuBarFXMLController,
                mockDeckService
        );

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main/TrashView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/main/TrashView.fxml"), any()))
                .thenReturn(mockRoot);

        controller.loadViewFromFXML();

        Platform.runLater(() -> {
            Stage stage = new Stage();
            when(mockStage.getStage()).thenReturn(stage);
            stage.setScene(new Scene(controller.getRoot()));
            stage.show();

            WaitForAsyncUtils.waitForFxEvents();
            controller.initWithParams();
        });
    }

    @Test
    void testRemoveDeckFromListView() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        DeckBase mockDeck = mock(DeckBase.class);
        // Act
        ListView<DeckBase> deckListView = lookup("#trashDeckListView").queryAs(ListView.class);

        Platform.runLater(() -> {
            deckListView.setItems(FXCollections.observableArrayList(mockDeck));
            controller.removeDeckFromListView(mockDeck);
        });
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        assertFalse(deckListView.getItems().contains(mockDeck), "deckListView should not contain the removed deck");
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        when(mockTranslationService.getCurrentLocale()).thenReturn(Locale.of("pl", "PL"));

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockMenuBarFXMLController, times(1)).updateTranslation();
        assertEquals(Locale.of("pl", "PL"), controller.getCurrentLocaleProperty().get(), "Current locale should be updated");
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockMenuBarFXMLController, times(1)).updateSize();
        assertEquals(1.0, controller.getScaleFactorProperty().get(), "Scale factor should be updated");
    }
}
