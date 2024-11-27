package gutek.gui.controllers.deck;

import gutek.entities.cards.CardBase;
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

import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevisionEditCardFXMLControllerTest extends ApplicationTest {

    private RevisionEditCardFXMLController controller;
    private TranslationService mockTranslationService;
    private CardService mockCardService;
    private MainStage mockStage;

    private CardBase mockCard;
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
        mockCardService = mock(CardService.class);
        MenuBarFXMLController mockMenuBarController = mock(MenuBarFXMLController.class);
        MenuDeckFXMLController mockMenuDeckController = mock(MenuDeckFXMLController.class);
        mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");
        when(mockMenuBarController.getRoot()).thenReturn(new Label("MenuBar"));
        when(mockMenuDeckController.getRoot()).thenReturn(new Label("MenuDeck"));

        controller = new RevisionEditCardFXMLController(mockStage, mockFxmlFileLoader,
                mockTranslationService, mockMenuBarController, mockMenuDeckController,
                mockCardService);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deck/RevisionEditCardView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/deck/RevisionEditCardView.fxml"), any()))
                .thenReturn(mockRoot);

        controller.loadViewFromFXML();
        mockDeck = mock(DeckBase.class);
        mockCard = mock(CardBase.class);
        when(mockCard.getDeck()).thenReturn(mockDeck);
        when(mockCard.getFront()).thenReturn("Old Front");
        when(mockCard.getBack()).thenReturn("Old Back");

        Platform.runLater(() -> {
            Stage stage = new Stage();
            when(mockStage.getStage()).thenReturn(stage);
            stage.setScene(new Scene(mockRoot));
            stage.show();

            WaitForAsyncUtils.waitForFxEvents();
            controller.initWithParams(mockCard);
        });
    }

    @Test
    void testLabelsInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

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
    void testTextFieldsInitializedWithCardData() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        TextField frontTextField = lookup("#frontTextField").queryAs(TextField.class);
        TextField backTextField = lookup("#backTextField").queryAs(TextField.class);

        // Assert
        assertNotNull(frontTextField, "Front text field should be initialized");
        assertNotNull(backTextField, "Back text field should be initialized");

        assertEquals("Old Front", frontTextField.getText());
        assertEquals("Old Back", backTextField.getText());
    }

    @Test
    void testSaveButtonActionUpdatesCard() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        TextField frontTextField = lookup("#frontTextField").queryAs(TextField.class);
        TextField backTextField = lookup("#backTextField").queryAs(TextField.class);
        Button saveButton = lookup("#saveButton").queryAs(Button.class);

        Platform.runLater(() -> {
            frontTextField.setText("New Front");
            backTextField.setText("New Back");
            saveButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockCard).setFront("New Front");
        verify(mockCard).setBack("New Back");
        verify(mockCardService).saveCard(mockCard);
    }

    @Test
    void testSaveButtonActionWithEmptyFieldsShowsWarning() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        TextField frontTextField = lookup("#frontTextField").queryAs(TextField.class);
        TextField backTextField = lookup("#backTextField").queryAs(TextField.class);
        Button saveButton = lookup("#saveButton").queryAs(Button.class);

        Platform.runLater(() -> {
            frontTextField.setText("");
            backTextField.setText("");
            saveButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockTranslationService).getTranslation("deck_view.edit_card.empty_text");
        verifyNoInteractions(mockCardService);
    }

    @Test
    void testSaveButtonActionWithDuplicateFrontTextShowsWarning() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});
        CardBase anotherCard = mock(CardBase.class);
        when(anotherCard.getIdCard()).thenReturn(2L);
        when(mockCard.getIdCard()).thenReturn(1L);
        when(mockCardService.findCardByFrontAndDeck("Duplicate Front", mockDeck)).thenReturn(Optional.of(anotherCard));

        // Act
        TextField frontTextField = lookup("#frontTextField").queryAs(TextField.class);
        TextField backTextField = lookup("#backTextField").queryAs(TextField.class);
        Button saveButton = lookup("#saveButton").queryAs(Button.class);

        Platform.runLater(() -> {
            frontTextField.setText("Duplicate Front");
            backTextField.setText("New Back");
            saveButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockTranslationService).getTranslation("deck_view.edit_card.front_unique");
        verify(mockCardService).findCardByFrontAndDeck("Duplicate Front",mockDeck);
    }
}
