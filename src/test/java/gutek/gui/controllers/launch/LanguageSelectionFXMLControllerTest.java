package gutek.gui.controllers.launch;

import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LanguageSelectionFXMLControllerTest extends ApplicationTest {

    private LanguageSelectionFXMLController controller;
    private TranslationService mockTranslationService;
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
        mockStage = mock(MainStage.class);

        when(mockStage.getStageScaleFactor()).thenReturn(1.0);
        when(mockTranslationService.getAvailableLocales()).thenReturn(
                List.of(Locale.of("en", "US"), Locale.of("pl", "PL"), Locale.of("es", "ES"))
        );
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");

        controller = new LanguageSelectionFXMLController(
                mockStage,
                mockFxmlFileLoader,
                mockTranslationService
        );

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/launch/LanguageSelectionView.fxml"));
        fxmlLoader.setControllerFactory(param -> controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/launch/LanguageSelectionView.fxml"), any()))
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
    void testInitWithParams() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        ComboBox<Locale> languageComboBox = lookup("#languageComboBox").queryAs(ComboBox.class);

        // Assert
        assertNotNull(languageComboBox, "languageComboBox should not be null");
        assertEquals(3, languageComboBox.getItems().size(), "languageComboBox should contain 3 locales");
        assertEquals(Locale.of("en", "US"), languageComboBox.getItems().get(0));
        assertEquals(Locale.of("pl", "PL"), languageComboBox.getItems().get(1));
        assertEquals(Locale.of("es", "ES"), languageComboBox.getItems().get(2));
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        Label selectLanguageLabel = lookup("#selectLanguageLabel").queryAs(Label.class);
        Button confirmButton = lookup("#confirmButton").queryAs(Button.class);

        // Assert
        assertNotNull(selectLanguageLabel, "selectLanguageLabel should not be null");
        assertEquals("Translated", selectLanguageLabel.getText(), "selectLanguageLabel text should be translated");

        assertNotNull(confirmButton, "confirmButton should not be null");
        assertEquals("Translated", confirmButton.getText(), "confirmButton text should be translated");
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        Label selectLanguageLabel = lookup("#selectLanguageLabel").queryAs(Label.class);
        ComboBox<?> languageComboBox = lookup("#languageComboBox").queryAs(ComboBox.class);
        Button confirmButton = lookup("#confirmButton").queryAs(Button.class);

        // Assert
        assertNotNull(selectLanguageLabel, "selectLanguageLabel should not be null");
        assertNotNull(selectLanguageLabel.getStyle(), "selectLanguageLabel should have updated style");
        assertTrue(selectLanguageLabel.getStyle().contains("-fx-font-size:"), "selectLanguageLabel should have font size updated");

        assertNotNull(languageComboBox, "languageComboBox should not be null");
        assertNotNull(languageComboBox.getStyle(), "languageComboBox should have updated style");
        assertTrue(selectLanguageLabel.getStyle().contains("-fx-font-size:"), "languageComboBox should have font size updated");

        assertNotNull(confirmButton, "confirmButton should not be null");
        assertNotNull(confirmButton.getStyle(), "confirmButton should have updated style");
        assertTrue(selectLanguageLabel.getStyle().contains("-fx-font-size:"), "confirmButton should have font size updated");
    }

    @Test
    void testUpdateView() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {});

        when(mockTranslationService.getCurrentLocale()).thenReturn(Locale.of("pl", "PL"));

        // Act
        Platform.runLater(() -> controller.updateView());
        WaitForAsyncUtils.waitForFxEvents();

        ComboBox<Locale> languageComboBox = lookup("#languageComboBox").queryAs(ComboBox.class);

        // Assert
        assertNotNull(languageComboBox, "languageComboBox should not be null");
        assertEquals(Locale.of("pl", "PL"), languageComboBox.getValue(), "languageComboBox should reflect current locale");
    }

    @Test
    void testOnConfirmButtonClicked() {
        // Arrange
        Platform.runLater(() -> {
            // Act
            ComboBox<Locale> languageComboBox = lookup("#languageComboBox").queryAs(ComboBox.class);

            languageComboBox.getSelectionModel().select(Locale.of("es", "ES"));

            Button confirmButton = lookup("#confirmButton").queryAs(Button.class);
            confirmButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockTranslationService, times(2)).updateLocale(Locale.of("es", "ES"));
        verify(mockStage, times(1)).setScene(MainStageScenes.LOGIN_SCENE);
    }
}
