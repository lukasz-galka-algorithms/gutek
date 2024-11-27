package gutek.gui.controllers.controls;

import gutek.entities.cards.CardBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.deck.RevisionSearchFXMLController;
import gutek.services.CardService;
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

class CardCellFXMLControllerTest extends ApplicationTest {

    private CardCellFXMLController controller;
    private CardService cardService;
    private TranslationService translationService;
    private RevisionSearchFXMLController parentController;
    private CardBase mockCard;

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
        cardService = mock(CardService.class);
        translationService = mock(TranslationService.class);
        parentController = mock(RevisionSearchFXMLController.class);
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);
        MainStage mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(parentController.getScaleFactorProperty()).thenReturn(new SimpleDoubleProperty(1.0));
        when(parentController.getCurrentLocaleProperty()).thenReturn(new SimpleObjectProperty<>(Locale.ENGLISH));
        when(translationService.getTranslation(Mockito.anyString())).thenReturn("Translated");

        controller = new CardCellFXMLController(mockStage, mockFxmlFileLoader, translationService, cardService, parentController);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/CardCellView.fxml"));
        fxmlLoader.setController(controller);
        Parent mockRoot = fxmlLoader.load();
        when(mockFxmlFileLoader.loadFXML(eq("/fxml/controls/CardCellView.fxml"), any()))
                .thenReturn(mockRoot);
        controller.loadViewFromFXML();

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setScene(new Scene(controller.getRoot()));
            stage.show();

            mockCard = mock(CardBase.class);
            when(mockCard.getFront()).thenReturn("Front Text");
            when(mockCard.getBack()).thenReturn("Back Text");

            controller.setCard(mockCard);
        });
    }

    @Test
    void testLabelsInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Label frontLabel = lookup("#frontLabel").queryAs(Label.class);
        Label backLabel = lookup("#backLabel").queryAs(Label.class);

        // Assert
        assertNotNull(frontLabel, "Front label should be initialized");
        assertNotNull(backLabel, "Back label should be initialized");

        assertEquals("Translated: Front Text", frontLabel.getText());
        assertEquals("Translated: Back Text", backLabel.getText());
    }

    @Test
    void testButtonsInitialized() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Button editButton = lookup("#editButton").queryAs(Button.class);
        Button deleteButton = lookup("#deleteButton").queryAs(Button.class);

        // Assert
        assertNotNull(editButton, "Edit button should be initialized");
        assertNotNull(deleteButton, "Delete button should be initialized");

        assertEquals("Translated", editButton.getText());
        assertEquals("Translated", deleteButton.getText());
    }

    @Test
    void testHandleEdit() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Button editButton = lookup("#editButton").queryAs(Button.class);

        // Assert
        assertNotNull(editButton, "Edit button should be present");

        // Act
        Platform.runLater(editButton::fire);
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(parentController, never()).removeCardFromListView(mockCard);
    }

    @Test
    void testHandleDelete() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        when(translationService.getTranslation("deck_view.search_card.delete_title")).thenReturn("Delete Title");
        when(translationService.getTranslation("deck_view.search_card.delete_confirm")).thenReturn("Are you sure?");
        when(translationService.getTranslation("deck_view.search_card.delete_success")).thenReturn("Success");

        // Act
        Button deleteButton = lookup("#deleteButton").queryAs(Button.class);
        // Assert
        assertNotNull(deleteButton, "Delete button should be present");

        // Act
        Platform.runLater(deleteButton::fire);
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(cardService, never()).removeCard(mockCard);
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
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        Label frontLabel = lookup("#frontLabel").queryAs(Label.class);
        Label backLabel = lookup("#backLabel").queryAs(Label.class);

        // Assert
        assertEquals("Translated: Front Text", frontLabel.getText());
        assertEquals("Translated: Back Text", backLabel.getText());
    }
}