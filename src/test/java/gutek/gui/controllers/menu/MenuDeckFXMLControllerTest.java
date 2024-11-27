package gutek.gui.controllers.menu;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuDeckFXMLControllerTest extends ApplicationTest {

    private MenuDeckFXMLController controller;
    private MainStage mockStage;

    private DeckBase mockDeck;

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
        TranslationService mockTranslationService = mock(TranslationService.class);
        mockStage = mock(MainStage.class);
        mockDeck = mock(DeckBase.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");

        controller = new MenuDeckFXMLController(mockStage, mockFxmlFileLoader, mockTranslationService);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/menu/MenuDeckView.fxml"));
        fxmlLoader.setController(controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/menu/MenuDeckView.fxml"), any()))
                .thenReturn(mockRoot);
        controller.loadViewFromFXML();

        Platform.runLater(() -> {
            Stage stage = new Stage();
            when(mockStage.getStage()).thenReturn(stage);
            stage.setScene(new Scene(controller.getRoot()));
            stage.show();

            WaitForAsyncUtils.waitForFxEvents();
            controller.initWithParams(mockDeck);
        });
    }

    @Test
    void testInitWithParams() throws Exception {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Button addCardButton = lookup("#addCardButton").queryAs(Button.class);
        Button browseDeckButton = lookup("#browseDeckButton").queryAs(Button.class);
        Button revisionButton = lookup("#revisionButton").queryAs(Button.class);
        Button settingsButton = lookup("#settingsButton").queryAs(Button.class);
        Button statsButton = lookup("#statsButton").queryAs(Button.class);
        Button closeButton = lookup("#closeButton").queryAs(Button.class);

        // Assert
        assertNotNull(addCardButton, "addCardButton should not be null");
        assertNotNull(browseDeckButton, "browseDeckButton should not be null");
        assertNotNull(revisionButton, "revisionButton should not be null");
        assertNotNull(settingsButton, "settingsButton should not be null");
        assertNotNull(statsButton, "statsButton should not be null");
        assertNotNull(closeButton, "closeButton should not be null");
    }

    @Test
    void testUpdateTranslation() throws Exception {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        Button addCardButton = lookup("#addCardButton").queryAs(Button.class);
        Button browseDeckButton = lookup("#browseDeckButton").queryAs(Button.class);
        Button revisionButton = lookup("#revisionButton").queryAs(Button.class);
        Button settingsButton = lookup("#settingsButton").queryAs(Button.class);
        Button statsButton = lookup("#statsButton").queryAs(Button.class);
        Button closeButton = lookup("#closeButton").queryAs(Button.class);

        // Assert
        assertEquals("Translated", addCardButton.getText(), "addCardButton text should be translated");
        assertEquals("Translated", browseDeckButton.getText(), "browseDeckButton text should be translated");
        assertEquals("Translated", revisionButton.getText(), "revisionButton text should be translated");
        assertEquals("Translated", settingsButton.getText(), "settingsButton text should be translated");
        assertEquals("Translated", statsButton.getText(), "statsButton text should be translated");
        assertEquals("Translated", closeButton.getText(), "closeButton text should be translated");
    }

    @Test
    void testUpdateSize() throws Exception {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Button addCardButton = lookup("#addCardButton").queryAs(Button.class);
        Button browseDeckButton = lookup("#browseDeckButton").queryAs(Button.class);
        Button revisionButton = lookup("#revisionButton").queryAs(Button.class);
        Button settingsButton = lookup("#settingsButton").queryAs(Button.class);
        Button statsButton = lookup("#statsButton").queryAs(Button.class);
        Button closeButton = lookup("#closeButton").queryAs(Button.class);

        // Assert
        assertNotNull(addCardButton.getStyle(), "addCardButton should have a style");
        assertTrue(addCardButton.getStyle().contains("-fx-font-size:"), "addCardButton style should include font size");

        assertNotNull(browseDeckButton.getStyle(), "browseDeckButton should have a style");
        assertTrue(browseDeckButton.getStyle().contains("-fx-font-size:"), "browseDeckButton style should include font size");

        assertNotNull(revisionButton.getStyle(), "revisionButton should have a style");
        assertTrue(revisionButton.getStyle().contains("-fx-font-size:"), "revisionButton style should include font size");

        assertNotNull(settingsButton.getStyle(), "settingsButton should have a style");
        assertTrue(settingsButton.getStyle().contains("-fx-font-size:"), "settingsButton style should include font size");

        assertNotNull(statsButton.getStyle(), "statsButton should have a style");
        assertTrue(statsButton.getStyle().contains("-fx-font-size:"), "statsButton style should include font size");

        assertNotNull(closeButton.getStyle(), "closeButton should have a style");
        assertTrue(closeButton.getStyle().contains("-fx-font-size:"), "closeButton style should include font size");
    }

    @Test
    void testButtonActions() throws Exception {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Button addCardButton = lookup("#addCardButton").queryAs(Button.class);
        Button browseDeckButton = lookup("#browseDeckButton").queryAs(Button.class);
        Button revisionButton = lookup("#revisionButton").queryAs(Button.class);
        Button settingsButton = lookup("#settingsButton").queryAs(Button.class);
        Button statsButton = lookup("#statsButton").queryAs(Button.class);
        Button closeButton = lookup("#closeButton").queryAs(Button.class);

        Platform.runLater(addCardButton::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.REVISION_ADD_NEW_CARD_SCENE, mockDeck);

        // Act
        Platform.runLater(browseDeckButton::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.REVISION_SEARCH_SCENE, mockDeck);

        // Act
        Platform.runLater(revisionButton::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.REVISION_REVISE_SCENE, mockDeck);

        // Act
        Platform.runLater(settingsButton::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.REVISION_SETTINGS_SCENE, mockDeck);

        // Act
        Platform.runLater(statsButton::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.REVISION_STATISTICS_SCENE, mockDeck);

        // Act
        Platform.runLater(closeButton::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.DECKS_SCENE);
    }
}
