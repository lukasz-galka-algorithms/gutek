package gutek.gui.controllers.main;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.services.CardService;
import gutek.services.DeckService;
import gutek.services.RevisionAlgorithmService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeoutException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewDeckFXMLControllerTest extends ApplicationTest {

    private NewDeckFXMLController controller;
    private RevisionAlgorithmService mockRevisionAlgorithmService;
    private DeckService mockDeckService;
    private CardService mockCardService;
    private MainStage mockStage;

    @BeforeEach
    void setUp() throws Exception {
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);
        TranslationService mockTranslationService = mock(TranslationService.class);
        mockRevisionAlgorithmService = mock(RevisionAlgorithmService.class);
        mockDeckService = mock(DeckService.class);
        mockCardService = mock(CardService.class);
        MenuBarFXMLController mockMenuBarFXMLController = mock(MenuBarFXMLController.class);
        mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");

        controller = new NewDeckFXMLController(
                mockStage,
                mockFxmlFileLoader,
                mockTranslationService,
                mockMenuBarFXMLController,
                mockRevisionAlgorithmService,
                mockDeckService,
                mockCardService
        );

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main/NewDeckView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/main/NewDeckView.fxml"), any()))
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
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        Label nameLabel = lookup("#nameLabel").queryAs(Label.class);
        Label algorithmLabel = lookup("#algorithmLabel").queryAs(Label.class);
        Button addButton = lookup("#addButton").queryAs(Button.class);
        Button importButton = lookup("#importButton").queryAs(Button.class);

        // Assert
        assertNotNull(nameLabel, "nameLabel should not be null");
        assertEquals("Translated", nameLabel.getText(), "nameLabel should be translated");

        assertNotNull(algorithmLabel, "algorithmLabel should not be null");
        assertEquals("Translated", algorithmLabel.getText(), "algorithmLabel should be translated");

        assertNotNull(addButton, "addButton should not be null");
        assertEquals("Translated", addButton.getText(), "addButton should be translated");

        assertNotNull(importButton, "importButton should not be null");
        assertEquals("Translated", importButton.getText(), "importButton should be translated");
    }

    @Test
    void testHandleAddDeck() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> {
            TextField nameField = lookup("#nameField").queryAs(TextField.class);
            ComboBox<String> algorithmComboBox = lookup("#algorithmComboBox").queryAs(ComboBox.class);
            Button addButton = lookup("#addButton").queryAs(Button.class);

            nameField.setText("Test Deck");
            algorithmComboBox.getItems().add("Algorithm 1");
            algorithmComboBox.getSelectionModel().select("Algorithm 1");
            // Arrange
            when(mockRevisionAlgorithmService.createAlgorithmInstance("Algorithm 1"))
                    .thenReturn(mock(RevisionAlgorithm.class));
            // Act
            addButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockDeckService, times(1))
                .addNewDeck(any(), any(), eq("Test Deck"));
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Label nameLabel = lookup("#nameLabel").queryAs(Label.class);
        // Assert
        assertNotNull(nameLabel.getStyle(), "nameLabel should have updated style");
        assertTrue(nameLabel.getStyle().contains("-fx-font-size:"), "nameLabel should have font size updated");
    }
}
