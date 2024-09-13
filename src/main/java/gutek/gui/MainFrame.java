package gutek.gui;

import gutek.entities.users.AppUser;
import gutek.gui.deck.*;
import gutek.gui.launch.LanguageSelectionView;
import gutek.gui.launch.LoginView;
import gutek.gui.main.AuthorsView;
import gutek.gui.main.DecksView;
import gutek.gui.main.NewDeckView;
import gutek.gui.main.TrashView;
import gutek.services.TranslationService;
import gutek.utils.SpringContext;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Locale;

/**
 * Main application frame which manages the display of views and the application menu.
 * It handles the creation of views and interaction between different components of the GUI.
 */
@Component
@Data
public class MainFrame extends JFrame {
    /** Default width of the application window. */
    private static final int WIDTH = 800;
    /** Default height of the application window. */
    private static final int HEIGHT = 500;
    /** Scaling factor for UI elements. */
    private static final double SCALE = 0.75;
    /** Spring context for managing beans and dependencies. */
    private final SpringContext springContext;
    /** Service for managing translations across the application. */
    private final TranslationService translationService;
    /** The user currently logged into the application. */
    private AppUser loggedUser;
    /** The scaled size of the window. */
    private Dimension scaledSize;
    /** The scale factor used to adjust UI components based on screen size. */
    private double scaleFactor;
    /** Main application menu bar. */
    private JMenuBar appMenuBar;
    /** Menu for file-related actions. */
    private JMenu fileMenu;
    /** Menu item to navigate to decks view. */
    private JMenuItem decksMenuItem;
    /** Menu item to navigate to the new deck view. */
    private JMenuItem newMenuItem;
    /** Menu item to navigate to the trash view. */
    private JMenuItem trashMenuItem;
    /** Menu item to display author information. */
    private JMenuItem authorsMenuItem;
    /** Menu item to exit the application. */
    private JMenuItem exitMenuItem;
    /** Menu for changing the application's language. */
    private JMenu languageMenu;
    /** Menu for user session-related actions. */
    private JMenu logoutMenu;
    /** Menu item to log out the current user. */
    private JMenuItem logoutMenuItem;
    /** The currently active view in the application. */
    private AppView currentView;
    /**
     * Constructs the MainFrame with the given Spring context and translation service.
     * Initializes the main frame, window size, and menu bar.
     *
     * @param springContext       the Spring context for managing beans
     * @param translationService  the service for handling translations in the application
     */
    public MainFrame(SpringContext springContext, TranslationService translationService) {
        this.translationService = translationService;
        this.springContext = springContext;

        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
        initializeMenuBar();

        updateSize();
        translationService.updateLocale(null);
        updateTranslation();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    /**
     * Initializes the application menu bar, including the file, language, and session menus.
     */
    private void initializeMenuBar() {
        appMenuBar = new JMenuBar();

        fileMenu = new JMenu("");
        decksMenuItem = new JMenuItem("");
        decksMenuItem.addActionListener(
                e -> this.setView(MainFrameViews.DECKS_VIEW)
        );
        newMenuItem = new JMenuItem("");
        newMenuItem.addActionListener(
                e -> this.setView(MainFrameViews.NEW_DECK_VIEW)
        );
        trashMenuItem = new JMenuItem("");
        trashMenuItem.addActionListener(
                e -> this.setView(MainFrameViews.TRASH_VIEW)
        );
        authorsMenuItem = new JMenuItem("");
        authorsMenuItem.addActionListener(
                e -> this.setView(MainFrameViews.AUTHORS_VIEW)
        );
        exitMenuItem = new JMenuItem("");
        exitMenuItem.addActionListener(
                e -> this.setView(MainFrameViews.EXIT)
        );

        fileMenu.add(decksMenuItem);
        fileMenu.add(newMenuItem);
        fileMenu.add(trashMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(authorsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        appMenuBar.add(fileMenu);

        languageMenu = new JMenu("");
        List<Locale> availableLocales = translationService.getAvailableLocales();
        for (Locale locale : availableLocales) {
            String name = locale.getDisplayLanguage(locale);
            String capitalizedLanguageName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

            JMenuItem langItem = new JMenuItem(capitalizedLanguageName);
            langItem.addActionListener(e -> {
                translationService.updateLocale(locale);
                updateTranslation();
                if (currentView != null) {
                    currentView.updateTranslation();
                }
            });
            languageMenu.add(langItem);
        }

        appMenuBar.add(languageMenu);

        logoutMenu = new JMenu("");
        logoutMenuItem = new JMenuItem("");
        logoutMenuItem.addActionListener(
                e -> {
                    this.setLoggedUser(null);
                    this.setView(MainFrameViews.LOGIN_VIEW);
                }
        );
        logoutMenu.add(logoutMenuItem);
        appMenuBar.add(logoutMenu);

        this.updateSize();
        appMenuBar.setVisible(false);
        setJMenuBar(appMenuBar);
    }
    /**
     * Sets the view in the application based on the provided view enum.
     * It retrieves the required view using the Spring context and updates the frame.
     *
     * @param nextView the view to display next
     */
    public void setView(MainFrameViews nextView) {
        AppView view = null;
        if (nextView == MainFrameViews.LANGUAGE_SELECTION_VIEW) {
            appMenuBar.setVisible(false);
            view = springContext.getBean(LanguageSelectionView.class);
        } else if (nextView == MainFrameViews.LOGIN_VIEW) {
            appMenuBar.setVisible(false);
            view = springContext.getBean(LoginView.class);
        } else if (nextView == MainFrameViews.DECKS_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(DecksView.class);
        } else if (nextView == MainFrameViews.NEW_DECK_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(NewDeckView.class);
        } else if (nextView == MainFrameViews.TRASH_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(TrashView.class);
        } else if (nextView == MainFrameViews.AUTHORS_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(AuthorsView.class);
        } else if (nextView == MainFrameViews.EXIT) {
            shutdown();
        } else if (nextView == MainFrameViews.REVISION_ADD_NEW_CARD_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(RevisionAddCardView.class);
        } else if (nextView == MainFrameViews.REVISION_SEARCH_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(RevisionSearchView.class);
        } else if (nextView == MainFrameViews.REVISION_REVISE_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(RevisionView.class);
        } else if (nextView == MainFrameViews.REVISION_SETTINGS_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(RevisionSettingsView.class);
        } else if (nextView == MainFrameViews.REVISION_STATISTICS_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(RevisionStatisticsView.class);
        } else if (nextView == MainFrameViews.REVISION_EDIT_CARD_VIES) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(RevisionEditCardView.class);
        } else if (nextView == MainFrameViews.REVISION_REGULAR_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(RevisionRegularView.class);
        } else if (nextView == MainFrameViews.REVISION_REVERSE_VIEW) {
            appMenuBar.setVisible(true);
            view = springContext.getBean(RevisionReverseView.class);
        }

        if (view != null) {
            this.currentView = view;
            view.updateView();
            view.updateSize();
            view.updateTranslation();
            this.getContentPane().removeAll();
            this.getContentPane().add(view);
            this.revalidate();
            this.repaint();
        }
    }
    /**
     * Updates the size and scaling of the application window.
     * It adjusts the dimensions and scale factor based on the screen size.
     */
    public void updateSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double widthScaleFactor = (double) screenSize.width / WIDTH;
        double heightScaleFactor = (double) screenSize.height / HEIGHT;
        scaleFactor = Math.min(widthScaleFactor, heightScaleFactor) * SCALE;
        int width = (int) (WIDTH * scaleFactor);
        int height = (int) (HEIGHT * scaleFactor);
        this.setSize(width, height);
        scaledSize = new Dimension(width, height);

        int menuBarHeight = (int) (30 * scaleFactor);
        appMenuBar.setBounds(0, 0, scaledSize.width, menuBarHeight);

        for (int i = 0; i < appMenuBar.getMenuCount(); i++) {
            JMenu menu = appMenuBar.getMenu(i);
            menu.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));
            for (int j = 0; j < menu.getItemCount(); j++) {
                JMenuItem item = menu.getItem(j);
                if (item != null) {
                    item.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));
                }
            }
        }
    }
    /**
     * Updates the text of all components in the application based on the selected locale.
     * This includes updating the title and menu items with their corresponding translations.
     */
    public void updateTranslation() {
        this.setTitle(translationService.getTranslation("window.title"));

        fileMenu.setText(translationService.getTranslation("menu_bar.file"));
        decksMenuItem.setText(translationService.getTranslation("menu_bar.file.decks"));
        newMenuItem.setText(translationService.getTranslation("menu_bar.file.new"));
        trashMenuItem.setText(translationService.getTranslation("menu_bar.file.trash"));
        authorsMenuItem.setText(translationService.getTranslation("menu_bar.file.authors"));
        exitMenuItem.setText(translationService.getTranslation("menu_bar.file.exit"));

        languageMenu.setText(translationService.getTranslation("menu_bar.language"));
        logoutMenu.setText(translationService.getTranslation("menu_bar.session"));
        logoutMenuItem.setText(translationService.getTranslation("menu_bar.logout"));
    }
    /**
     * Shuts down the application.
     */
    private void shutdown() {
        System.exit(0);
    }
}
