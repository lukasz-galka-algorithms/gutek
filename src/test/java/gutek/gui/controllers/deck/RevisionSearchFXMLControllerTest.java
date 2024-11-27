package gutek.gui.controllers.deck;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.CardService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevisionSearchFXMLControllerTest extends ApplicationTest {

    private RevisionSearchFXMLController controller;
    private MenuBarFXMLController mockMenuBarController;
    private MenuDeckFXMLController mockMenuDeckController;

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
        CardService mockCardService = mock(CardService.class);
        mockMenuBarController = mock(MenuBarFXMLController.class);
        mockMenuDeckController = mock(MenuDeckFXMLController.class);
        MainStage mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");
        when(mockMenuBarController.getRoot()).thenReturn(new Label("MenuBar"));
        when(mockMenuDeckController.getRoot()).thenReturn(new Label("MenuDeck"));

        controller = new RevisionSearchFXMLController(mockStage, mockFxmlFileLoader,
                mockTranslationService, mockMenuBarController, mockMenuDeckController,
                mockCardService);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deck/RevisionSearchView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/deck/RevisionSearchView.fxml"), any()))
                .thenReturn(mockRoot);

        controller.loadViewFromFXML();
        mockDeck = mock(DeckBase.class);

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
    void testComponentsInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(controller::updateTranslation);
        WaitForAsyncUtils.waitForFxEvents();

        TextField frontTextField = (TextField) controller.getRoot().lookup("#frontInCardTextField");
        TextField backTextField = (TextField) controller.getRoot().lookup("#backInCardTextField");
        Button searchButton = (Button) controller.getRoot().lookup("#searchButton");

        // Assert
        assertNotNull(frontTextField, "Front text field should be initialized");
        assertNotNull(backTextField, "Back text field should be initialized");
        assertNotNull(searchButton, "Search button should be initialized");
        assertEquals("Translated", searchButton.getText());

        verify(mockMenuBarController, times(1)).updateTranslation();
        verify(mockMenuDeckController, times(1)).updateTranslation();
    }
}
