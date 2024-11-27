package gutek.gui.controllers.deck;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.gui.controllers.menu.MenuDeckFXMLController;
import gutek.services.RevisionAlgorithmService;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

class RevisionSettingsFXMLControllerTest extends ApplicationTest {

    private RevisionSettingsFXMLController controller;
    private TranslationService mockTranslationService;
    private MenuBarFXMLController mockMenuBarController;
    private MenuDeckFXMLController mockMenuDeckController;
    private RevisionAlgorithmService mockRevisionAlgorithmService;

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
    public void setUp() throws Exception {
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);
        mockTranslationService = mock(TranslationService.class);
        mockMenuBarController = mock(MenuBarFXMLController.class);
        mockMenuDeckController = mock(MenuDeckFXMLController.class);
        mockRevisionAlgorithmService = mock(RevisionAlgorithmService.class);
        mockDeck = mock(DeckBase.class);
        RevisionAlgorithm mockAlgorithm = mock(RevisionAlgorithm.class);
        MainStage mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockDeck.getRevisionAlgorithm()).thenReturn(mockAlgorithm);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");
        when(mockMenuBarController.getRoot()).thenReturn(new Label("MenuBar"));
        when(mockMenuDeckController.getRoot()).thenReturn(new Label("MenuDeck"));

        controller = new RevisionSettingsFXMLController(
                mockStage,
                mockFxmlFileLoader,
                mockTranslationService,
                mockMenuBarController,
                mockMenuDeckController,
                mockRevisionAlgorithmService
        );

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/deck/RevisionSettingsView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/deck/RevisionSettingsView.fxml"), any()))
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
    void testLoadSettings() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        TestRevisionAlgorithm testAlgorithm = new TestRevisionAlgorithm();
        testAlgorithm.setParam1("Test Value");
        testAlgorithm.setParam2(42);

        when(mockDeck.getRevisionAlgorithm()).thenReturn((RevisionAlgorithm) testAlgorithm);
        when(mockTranslationService.getTranslation("param1.description")).thenReturn("Translated Param 1");
        when(mockTranslationService.getTranslation("param2.description")).thenReturn("Translated Param 2");

        // Act
        Platform.runLater(() -> controller.loadSettings());
        WaitForAsyncUtils.waitForFxEvents();

        VBox settingsContainer = lookup("#settingsContainer").queryAs(VBox.class);
        // Assert
        assertNotNull(settingsContainer, "Settings container should not be null");
        assertEquals(2, settingsContainer.getChildren().size(), "Settings container should contain 2 HBoxes");
        // Act
        HBox firstHBox = (HBox) settingsContainer.getChildren().getFirst();
        Label firstLabel = (Label) firstHBox.getChildren().get(0);
        TextField firstTextField = (TextField) firstHBox.getChildren().get(1);

        // Assert
        assertEquals("Translated Param 1", firstLabel.getText(), "First label should have translated text");
        assertEquals("Test Value", firstTextField.getText(), "First TextField should contain initial value");

        // Act
        HBox secondHBox = (HBox) settingsContainer.getChildren().get(1);
        Label secondLabel = (Label) secondHBox.getChildren().get(0);
        TextField secondTextField = (TextField) secondHBox.getChildren().get(1);
        // Assert
        assertEquals("Translated Param 2", secondLabel.getText(), "Second label should have translated text");
        assertEquals("42", secondTextField.getText(), "Second TextField should contain initial value as string");
    }

    @Test
    void testSaveSettings() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        TestRevisionAlgorithm testAlgorithm = new TestRevisionAlgorithm();
        testAlgorithm.setParam1("Initial Value");
        testAlgorithm.setParam2(42);

        when(mockDeck.getRevisionAlgorithm()).thenReturn((RevisionAlgorithm) testAlgorithm);
        when(mockTranslationService.getTranslation("param1.description")).thenReturn("Translated Param 1");
        when(mockTranslationService.getTranslation("param2.description")).thenReturn("Translated Param 2");

        // Act
        Platform.runLater(() -> controller.loadSettings());
        WaitForAsyncUtils.waitForFxEvents();

        VBox settingsContainer = lookup("#settingsContainer").queryAs(VBox.class);
        // Assert
        assertNotNull(settingsContainer, "Settings container should not be null");

        // Act
        HBox firstHBox = (HBox) settingsContainer.getChildren().getFirst();
        TextField firstTextField = (TextField) firstHBox.getChildren().get(1);
        firstTextField.setText("Updated Value");

        HBox secondHBox = (HBox) settingsContainer.getChildren().get(1);
        TextField secondTextField = (TextField) secondHBox.getChildren().get(1);
        secondTextField.setText("50");

        Button saveButton = lookup("#saveButton").queryAs(Button.class);
        // Assert
        assertNotNull(saveButton, "Save button should not be null");

        // Act
        Platform.runLater(saveButton::fire);
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        assertEquals("Updated Value", testAlgorithm.getParam1(), "Param1 should be updated with the new value");
        assertEquals(50, testAlgorithm.getParam2(), "Param2 should be updated with the new value");

        verify(mockRevisionAlgorithmService, times(2)).saveAlgorithm(testAlgorithm);
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        Button saveButton = lookup("#saveButton").queryAs(Button.class);
        // Assert
        assertNotNull(saveButton, "Save button should be initialized");
        assertEquals("Translated", saveButton.getText(), "Save button text should be translated");

        verify(mockMenuBarController, times(1)).updateTranslation();
        verify(mockMenuDeckController, times(1)).updateTranslation();
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Button saveButton = lookup("#saveButton").queryAs(Button.class);
        // Assert
        assertNotNull(saveButton, "Save button should be initialized");
        assertTrue(saveButton.getStyle().contains("-fx-font-size:"), "Save button style should be updated");

        verify(mockMenuBarController, times(1)).updateSize();
        verify(mockMenuDeckController, times(1)).updateSize();
    }
}
