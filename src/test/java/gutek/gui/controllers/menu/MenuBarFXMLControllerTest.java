package gutek.gui.controllers.menu;

import gutek.gui.controllers.MainStage;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.TranslationService;
import gutek.utils.FXMLFileLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
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

class MenuBarFXMLControllerTest extends ApplicationTest {

    private MenuBarFXMLController controller;
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
        when(mockTranslationService.getAvailableLocales()).thenReturn(List.of(
                Locale.of("en", "US"),
                Locale.of("pl", "PL"),
                Locale.of("es", "ES")
        ));
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated");
        when(mockTranslationService.getCurrentLocale()).thenReturn(Locale.of("en", "US"));

        controller = new MenuBarFXMLController(mockStage, mockFxmlFileLoader, mockTranslationService);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/menu/MenuBarView.fxml"));
        fxmlLoader.setController(controller);
        Parent mockRoot = fxmlLoader.load();

        when(mockFxmlFileLoader.loadFXML(eq("/fxml/menu/MenuBarView.fxml"), any()))
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
    void testInitWithParams() throws Exception {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });
        // Act
        MenuBar menuBar = lookup(".menu-bar").queryAs(MenuBar.class);
        // Assert
        assertNotNull(menuBar, "MenuBar should not be null");

        assertEquals(3, menuBar.getMenus().size());
        verify(mockStage, never()).setScene(any());
    }

    @Test
    void testUpdateTranslation() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Platform.runLater(() -> controller.updateTranslation());
        WaitForAsyncUtils.waitForFxEvents();

        MenuBar menuBar = lookup(".menu-bar").queryAs(MenuBar.class);
        // Assert
        assertNotNull(menuBar, "MenuBar should not be null");

        menuBar.getMenus().forEach(menu -> {
            assertEquals("Translated", menu.getText(), "Menu text should be translated");
            menu.getItems().forEach(menuItem -> {
                assertNotNull(menuItem, "MenuItem should not be null");
                if (!(menuItem instanceof SeparatorMenuItem)) {
                    assertEquals("Translated", menuItem.getText(), "MenuItem text should be translated");
                }
            });
        });
    }

    @Test
    void testUpdateSize() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });
        // Act
        Platform.runLater(() -> controller.updateSize());
        WaitForAsyncUtils.waitForFxEvents();

        MenuBar menuBar = lookup(".menu-bar").queryAs(MenuBar.class);
        // Assert
        assertNotNull(menuBar, "MenuBar should not be null");

        menuBar.getMenus().forEach(menu -> {
            assertNotNull(menu.getStyle(), "Menu text should have style");
            assertTrue(menu.getStyle().contains("-fx-font-size:"), "Menu should have font size updated");
            menu.getItems().forEach(menuItem -> {
                assertNotNull(menuItem, "MenuItem should not be null");
                if (!(menuItem instanceof SeparatorMenuItem)) {
                    assertNotNull(menuItem.getStyle(), "MenuItem text should have style");
                    assertTrue(menuItem.getStyle().contains("-fx-font-size:"), "menuItem should have font size updated");
                }
            });
        });
    }

    @Test
    void testUpdateView() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Platform.runLater(() -> controller.updateView());
        WaitForAsyncUtils.waitForFxEvents();

        MenuBar menuBar = lookup(".menu-bar").queryAs(MenuBar.class);
        // Assert
        assertNotNull(menuBar, "MenuBar should not be null");

        // Act
        Menu languageMenu = menuBar.getMenus().get(1);
        // Assert
        assertNotNull(languageMenu, "languageMenu should not be null");
        assertEquals(3, languageMenu.getItems().size(), "languageMenu should contain 3 items");

        // Act
        List<MenuItem> languageItems = languageMenu.getItems();
        // Assert
        assertEquals("English", languageItems.get(0).getText(), "First language menu item should be English");
        assertEquals("Polski", languageItems.get(1).getText(), "Second language menu item should be Polish");
        assertEquals("Español", languageItems.get(2).getText(), "Third language menu item should be Spanish");
    }

    @Test
    void testMenuItemActions() throws TimeoutException {
        // Arrange
        FxToolkit.setupFixture(() -> {
        });

        // Act
        Platform.runLater(() -> controller.updateView());
        WaitForAsyncUtils.waitForFxEvents();

        MenuBar menuBar = lookup(".menu-bar").queryAs(MenuBar.class);
        // Assert
        assertNotNull(menuBar, "MenuBar should not be null");

        // Act
        Menu fileMenu = menuBar.getMenus().getFirst();
        // Assert
        assertNotNull(fileMenu, "fileMenu should not be null");

        // Act
        MenuItem decksMenuItem = fileMenu.getItems().getFirst();
        // Assert
        assertNotNull(decksMenuItem, "decksMenuItem should not be null");

        // Act
        Platform.runLater(decksMenuItem::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.DECKS_SCENE);

        // Act
        MenuItem newMenuItem = fileMenu.getItems().get(1);
        // Assert
        assertNotNull(newMenuItem, "newMenuItem should not be null");

        // Act
        Platform.runLater(newMenuItem::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.NEW_DECK_SCENE);

        // Act
        MenuItem trashMenuItem = fileMenu.getItems().get(2);
        // Assert
        assertNotNull(trashMenuItem, "trashMenuItem should not be null");

        // Act
        Platform.runLater(trashMenuItem::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.TRASH_SCENE);

        // Act
        MenuItem authorsMenuItem = fileMenu.getItems().get(4);
        // Assert
        assertNotNull(authorsMenuItem, "authorsMenuItem should not be null");

        // Act
        Platform.runLater(authorsMenuItem::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setScene(MainStageScenes.AUTHORS_SCENE);

        // Act
        Menu languageMenu = menuBar.getMenus().get(1);
        // Assert
        assertNotNull(languageMenu, "languageMenu should not be null");

        // Act
        List<MenuItem> languageItems = languageMenu.getItems();
        // Assert
        assertEquals(3, languageItems.size(), "languageMenu should contain 3 items");

        // Act
        Platform.runLater(() -> languageItems.getFirst().fire());
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockTranslationService, times(1)).updateLocale(Locale.of("en", "US"));

        // Act
        Platform.runLater(() -> languageItems.get(1).fire());
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockTranslationService, times(1)).updateLocale(Locale.of("pl", "PL"));

        // Act
        Platform.runLater(() -> languageItems.get(2).fire());
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockTranslationService, times(1)).updateLocale(Locale.of("es", "ES"));

        // Act
        Menu logoutMenu = menuBar.getMenus().get(2);
        // Assert
        assertNotNull(logoutMenu, "logoutMenu should not be null");

        // Act
        MenuItem logoutMenuItem = logoutMenu.getItems().getFirst();
        // Assert
        assertNotNull(logoutMenuItem, "logoutMenuItem should not be null");

        // Act
        Platform.runLater(logoutMenuItem::fire);
        WaitForAsyncUtils.waitForFxEvents();
        // Assert
        verify(mockStage, times(1)).setLoggedUser(null);
        verify(mockStage, times(1)).setScene(MainStageScenes.LOGIN_SCENE);
    }
}
