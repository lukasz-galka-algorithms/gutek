package gutek.gui.controllers.main;

import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.menu.MenuBarFXMLController;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

class AuthorsFXMLControllerTest extends ApplicationTest {

    private AuthorsFXMLController controller;
    private MenuBarFXMLController mockMenuBarController;
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
        mockStage = mock(MainStage.class);
        mockMenuBarController = mock(MenuBarFXMLController.class);
        TranslationService mockTranslationService = mock(TranslationService.class);
        FXMLFileLoader mockFxmlFileLoader = mock(FXMLFileLoader.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");

        controller = new AuthorsFXMLController(
                mockStage,
                mockFxmlFileLoader,
                mockTranslationService,
                mockMenuBarController
        );

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main/AuthorsView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/main/AuthorsView.fxml"), any()))
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
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Label authorLabel = lookup("#authorLabel").queryAs(Label.class);
        Label yearLabel = lookup("#yearLabel").queryAs(Label.class);

        // Assert
        assertNotNull(authorLabel, "authorLabel should not be null");
        assertNotNull(authorLabel.getStyle(), "authorLabel should have updated style");
        assertTrue(authorLabel.getStyle().contains("-fx-font-size:"), "authorLabel should have font size updated");
        assertTrue(authorLabel.getStyle().contains("-fx-font-family: 'Comic Sans MS';"), "authorLabel should use Comic Sans MS font");

        assertNotNull(yearLabel, "yearLabel should not be null");
        assertNotNull(yearLabel.getStyle(), "yearLabel should have updated style");
        assertTrue(yearLabel.getStyle().contains("-fx-font-size:"), "yearLabel should have font size updated");
        assertTrue(yearLabel.getStyle().contains("-fx-font-family: 'Comic Sans MS';"), "yearLabel should use Comic Sans MS font");

        verify(mockMenuBarController, times(1)).updateSize();
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockMenuBarController, times(1)).updateTranslation();
    }

    @Test
    void testUpdateView() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateView());
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockMenuBarController, times(1)).updateView();
    }
}
