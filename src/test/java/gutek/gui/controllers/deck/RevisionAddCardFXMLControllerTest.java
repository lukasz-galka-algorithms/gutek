package gutek.gui.controllers.deck;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.CardService;
import gutek.services.DeckService;
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

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevisionAddCardFXMLControllerTest extends ApplicationTest {

    private RevisionAddCardFXMLController controller;
    private CardService mockCardService;
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
    public void setUp() throws IOException {
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);
        TranslationService mockTranslationService = mock(TranslationService.class);
        mockCardService = mock(CardService.class);
        DeckService mockDeckService = mock(DeckService.class);
        mockMenuBarController = mock(MenuBarFXMLController.class);
        mockMenuDeckController = mock(MenuDeckFXMLController.class);
        MainStage mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");
        when(mockMenuBarController.getRoot()).thenReturn(new Label("MenuBar"));
        when(mockMenuDeckController.getRoot()).thenReturn(new Label("MenuDeck"));

        controller = new RevisionAddCardFXMLController(mockStage, mockFxmlFileLoader,
                mockTranslationService, mockMenuBarController, mockMenuDeckController,
                mockCardService, mockDeckService);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deck/RevisionAddCardView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/deck/RevisionAddCardView.fxml"), any()))
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
    void testLabelsInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        Label frontLabel = lookup("#frontLabel").queryAs(Label.class);
        Label backLabel = lookup("#backLabel").queryAs(Label.class);

        // Assert
        assertNotNull(frontLabel, "Front label should be initialized");
        assertNotNull(backLabel, "Back label should be initialized");

        assertEquals("Translated", frontLabel.getText());
        assertEquals("Translated", backLabel.getText());
    }

    @Test
    void testTextFieldsInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        TextField frontTextField = lookup("#frontTextField").queryAs(TextField.class);
        TextField backTextField = lookup("#backTextField").queryAs(TextField.class);

        // Assert
        assertNotNull(frontTextField, "Front text field should be initialized");
        assertNotNull(backTextField, "Back text field should be initialized");

        assertTrue(frontTextField.getText().isEmpty(), "Front text field should initially be empty");
        assertTrue(backTextField.getText().isEmpty(), "Back text field should initially be empty");
    }

    @Test
    void testAddButtonAction() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        TextField frontTextField = lookup("#frontTextField").queryAs(TextField.class);
        TextField backTextField = lookup("#backTextField").queryAs(TextField.class);
        Button addButton = lookup("#addButton").queryAs(Button.class);

        Platform.runLater(() -> {
            frontTextField.setText("Front Text");
            backTextField.setText("Back Text");
            addButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockCardService).addNewCard("Front Text", "Back Text", mockDeck);
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Label frontLabel = lookup("#frontLabel").queryAs(Label.class);
        Label backLabel = lookup("#backLabel").queryAs(Label.class);

        // Assert
        assertNotNull(frontLabel, "Front label should not be null");
        assertNotNull(backLabel, "Back label should not be null");

        assertTrue(frontLabel.getStyle().contains("-fx-font-size:"));
        assertTrue(backLabel.getStyle().contains("-fx-font-size:"));

        verify(mockMenuBarController, times(1)).updateSize();
        verify(mockMenuDeckController, times(1)).updateSize();
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(controller::updateTranslation);
        WaitForAsyncUtils.waitForFxEvents();

        Label frontLabel = lookup("#frontLabel").queryAs(Label.class);
        Label backLabel = lookup("#backLabel").queryAs(Label.class);

        // Assert
        assertEquals("Translated", frontLabel.getText());
        assertEquals("Translated", backLabel.getText());
    }
}