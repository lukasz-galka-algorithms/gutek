package gutek.gui.controllers;

import gutek.gui.controllers.main.DecksFXMLController;
import gutek.services.TranslationService;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainStageTest extends ApplicationTest {

    private MainStage mainStage;
    private ConfigurableApplicationContext mockApplicationContext;
    private TranslationService mockTranslationService;
    private Stage mockStage;

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
    void setUp() {
        mockApplicationContext = mock(ConfigurableApplicationContext.class);
        mockTranslationService = mock(TranslationService.class);
        mockStage = mock(Stage.class);

        mainStage = new MainStage(mockApplicationContext, mockTranslationService);
        mainStage.setStage(mockStage);

        when(mockTranslationService.getTranslation("window.title")).thenReturn("Translated Title");
        when(mockTranslationService.getCurrentLocale()).thenReturn(Locale.of("en", "US"));

        SimpleDoubleProperty widthProperty = new SimpleDoubleProperty(800);
        SimpleDoubleProperty heightProperty = new SimpleDoubleProperty(600);

        when(mockStage.widthProperty()).thenReturn(widthProperty);
        when(mockStage.heightProperty()).thenReturn(heightProperty);
        when(mockStage.getWidth()).thenReturn(widthProperty.get());
        when(mockStage.getHeight()).thenReturn(heightProperty.get());
    }

    @Test
    void testInitStage() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Platform.runLater(() -> {
            mainStage.initStage(mockStage);

            // Assert
            verify(mockStage).setWidth(mainStage.getDefaultWidth());
            verify(mockStage).setHeight(mainStage.getDefaultHeight());
            assertNotNull(mockStage.widthProperty());
            assertNotNull(mockStage.heightProperty());
            verify(mockTranslationService).getTranslation("window.title");
        });
    }

    @Test
    void testSetScene() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        DecksFXMLController mockController = mock(DecksFXMLController.class);
        Parent realRoot = new VBox();
        when(mockController.getRoot()).thenReturn(realRoot);
        when(mockApplicationContext.getBean(DecksFXMLController.class)).thenReturn(mockController);

        // Act
        Platform.runLater(() -> {
            mainStage.setScene(MainStageScenes.DECKS_SCENE);

            // Assert
            assertEquals(mockController, mainStage.getCurrentController());
            verify(mockController).loadViewFromFXML();
            verify(mockController).initWithParams();
            verify(mockController).updateView();
            verify(mockController).updateSize();
            verify(mockController).updateTranslation();
        });
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        DecksFXMLController mockController = mock(DecksFXMLController.class);
        mainStage.setCurrentController(mockController);

        // Act
        Platform.runLater(() -> {
            mainStage.updateSize();

            // Assert
            verify(mockController).updateSize();
        });
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Platform.runLater(() -> {
            mainStage.updateTranslation();

            // Assert
            verify(mockStage).setTitle("Translated Title");
        });
    }

    @Test
    void testGetStageScaleFactor() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        when(mockStage.getWidth()).thenReturn(1000.0);
        when(mockStage.getHeight()).thenReturn(700.0);

        double expectedScaleFactor = Math.min(1000.0 / 800, 700.0 / 500);

        // Act
        double mainStageScaleFactor = mainStage.getStageScaleFactor();

        // Assert
        assertEquals(expectedScaleFactor, mainStageScaleFactor, 0.01);
    }
}
