package gutek.gui.controls;

import gutek.gui.MainFrame;
import gutek.gui.MainFrameViews;
import gutek.services.TranslationService;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JPanel representing a menu for deck-related actions, including adding cards, browsing the deck,
 * revising cards, viewing settings, viewing statistics, and closing the menu.
 *
 * The panel provides buttons for each action and updates dynamically based on the current language and screen size.
 */
public class DeckMenuPanel extends JPanel {
    /** Button to add a new card to the deck. */
    private JButton addCardButton;
    /** Button to browse the cards in the deck. */
    private JButton browseDeckButton;
    /** Button to start the card revision process. */
    private JButton revisionButton;
    /** Button to open the settings for the deck. */
    private JButton settingsButton;
    /** Button to view the deck's statistics. */
    private JButton statsButton;
    /** Button to close the menu and return to the deck view. */
    private JButton closeButton;
    /** The service used for retrieving translations for the button labels. */
    private final TranslationService translationService;
    /** The main frame of the application, used to switch between views. */
    private final MainFrame mainFrame;
    /**
     * Constructs a new DeckMenuPanel with buttons for interacting with the deck.
     *
     * @param mainFrame the main frame of the application, used to switch views
     * @param translationService the service used for retrieving translations for the button labels
     */
    public DeckMenuPanel(MainFrame mainFrame, TranslationService translationService) {
        this.mainFrame = mainFrame;
        this.translationService = translationService;

        setLayout(new GridLayout(1, 6, 10, 10)); // 10 to odstępy między przyciskami

        addCardButton = new JButton();
        browseDeckButton = new JButton();
        revisionButton = new JButton();
        settingsButton = new JButton();
        statsButton = new JButton();
        closeButton = new JButton();

        add(addCardButton);
        add(browseDeckButton);
        add(revisionButton);
        add(settingsButton);
        add(statsButton);
        add(closeButton);

        addCardButton.addActionListener(e -> mainFrame.setView(MainFrameViews.REVISION_ADD_NEW_CARD_VIEW));
        browseDeckButton.addActionListener(e -> mainFrame.setView(MainFrameViews.REVISION_SEARCH_VIEW));
        revisionButton.addActionListener(e -> mainFrame.setView(MainFrameViews.REVISION_REVISE_VIEW));
        settingsButton.addActionListener(e -> mainFrame.setView(MainFrameViews.REVISION_SETTINGS_VIEW));
        statsButton.addActionListener(e -> mainFrame.setView(MainFrameViews.REVISION_STATISTICS_VIEW));
        closeButton.addActionListener(e -> mainFrame.setView(MainFrameViews.DECKS_VIEW));
    }
    /**
     * Updates the text of the buttons based on the current language settings using the translation service.
     */
    public void updateTranslation() {
        addCardButton.setText(translationService.getTranslation("deck_view.menu.add_card"));
        browseDeckButton.setText(translationService.getTranslation("deck_view.menu.browse"));
        revisionButton.setText(translationService.getTranslation("deck_view.menu.revision"));
        settingsButton.setText(translationService.getTranslation("deck_view.menu.settings"));
        statsButton.setText(translationService.getTranslation("deck_view.menu.statistics"));
        closeButton.setText(translationService.getTranslation("deck_view.menu.close"));
    }
    /**
     * Updates the size of the buttons based on the provided scaled dimensions and scale factor.
     *
     * @param dimensionScaled the scaled dimensions for the buttons
     * @param scaleFactor the factor by which to scale the font and button sizes
     */
    public void updateSize(Dimension dimensionScaled, double scaleFactor){
        Font scaledFont = new Font("Serif", Font.BOLD, (int) (12 * scaleFactor));
        int buttonWidth = dimensionScaled.width / 6;
        int buttonHeight = (int) (60 * scaleFactor);

        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);

        addCardButton.setPreferredSize(buttonSize);
        addCardButton.setFont(scaledFont);
        browseDeckButton.setPreferredSize(buttonSize);
        browseDeckButton.setFont(scaledFont);
        revisionButton.setPreferredSize(buttonSize);
        revisionButton.setFont(scaledFont);
        settingsButton.setPreferredSize(buttonSize);
        settingsButton.setFont(scaledFont);
        statsButton.setPreferredSize(buttonSize);
        statsButton.setFont(scaledFont);
        closeButton.setPreferredSize(buttonSize);
        closeButton.setFont(scaledFont);

        revalidate();
        repaint();
    }
}