package gutek.gui.controls;

import gutek.entities.decks.DeckBase;
import gutek.gui.main.TrashView;
import gutek.services.DeckService;
import gutek.services.TranslationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * A custom JPanel representing a deleted deck in the trash view, displaying the deck's name
 * and providing buttons to restore or permanently delete the deck.
 *
 * This panel is part of the trash view, where deleted decks are listed with options for restoration or permanent removal.
 */
public class TrashDeckCell extends JPanel {
    /** Label for displaying the name of the deck. */
    private JLabel deckNameLabel;
    /** Label to show the deck name. */
    private JLabel deckName;
    /** Button to restore the deck from the trash. */
    private JButton buttonRestore;
    /** Button to permanently delete the deck. */
    private JButton buttonDelete;
    /** The service used for translating text labels and buttons. */
    private final TranslationService translationService;
    /** The service used for interacting with deck data, such as restoring or deleting a deck. */
    private final DeckService deckService;
    /** The deck associated with this panel. */
    private final DeckBase deck;
    /** The view displaying the list of deleted decks (trash view). */
    private final TrashView trashView;
    /**
     * Constructs a new TrashDeckCell panel to display information about a deleted deck
     * and provide actions to restore or permanently delete it.
     *
     * @param deck the deck represented by this panel
     * @param trashView the trash view where deleted decks are displayed
     * @param translationService the service used for retrieving translations for labels and buttons
     * @param deckService the service used for managing deck data, such as restoring or deleting a deck
     */
    public TrashDeckCell(DeckBase deck,
                         TrashView trashView,
                         TranslationService translationService,
                         DeckService deckService) {
        this.deck = deck;
        this.trashView = trashView;
        this.translationService = translationService;
        this.deckService = deckService;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 15, 5, 15); // Top, left, bottom, right padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER; // Center align components

        deckNameLabel = new JLabel("",SwingConstants.CENTER);
        deckNameLabel.setForeground(Color.BLACK);
        deckName = new JLabel("",SwingConstants.CENTER);
        deckName.setForeground(Color.BLACK);

        buttonRestore = new JButton("");
        buttonDelete = new JButton("");

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(deckNameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(deckName, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(buttonRestore, gbc);
        gbc.gridx = 2;
        add(buttonDelete, gbc);

        buttonRestore.addActionListener(e -> handleRestore());
        buttonDelete.addActionListener(e -> handleDelete());
    }
    /**
     * Restores the deck from the trash back to the main deck view.
     * Updates the trash view after the deck is restored.
     */
    private void handleRestore() {
        deckService.restoreDeck(deck);

        trashView.updateView();
        trashView.updateSize();
        trashView.updateTranslation();
    }
    /**
     * Permanently deletes the deck from the system.
     * Updates the trash view after the deck is deleted.
     */
    private void handleDelete() {
        deckService.removeDeck(deck);

        trashView.updateView();
        trashView.updateSize();
        trashView.updateTranslation();
    }
    /**
     * Updates the text of the labels and buttons based on the current language settings using the translation service.
     */
    public void updateTranslation(){
        deckNameLabel.setText(translationService.getTranslation("trash_decks_view.deck_name"));
        deckName.setText(deck.getName());
        buttonRestore.setText(translationService.getTranslation("trash_decks_view.restore_button"));
        buttonDelete.setText(translationService.getTranslation("trash_decks_view.delete_button"));
    }
    /**
     * Updates the size of the labels, buttons, and panel borders based on the provided scale factor.
     *
     * @param dimensionScaled the scaled dimensions for the panel
     * @param scaleFactor the factor by which to scale the font and border sizes
     */
    public void updateSize(Dimension dimensionScaled, double scaleFactor) {
        Font scaledFont = new Font("Serif", Font.BOLD, (int) (12 * scaleFactor));
        int borderInner = (int) (10 * scaleFactor);
        int borderUpperDownScaled = (int) (10 * scaleFactor);
        int borderLeftRightScaled = (int) (50 * scaleFactor);
        int borderThicknessScaled = (int) (5 * scaleFactor);

        deckNameLabel.setFont(scaledFont);
        deckName.setFont(scaledFont);

        buttonRestore.setFont(scaledFont);
        buttonDelete.setFont(scaledFont);

        setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(borderUpperDownScaled, borderLeftRightScaled, borderUpperDownScaled, borderLeftRightScaled),
                BorderFactory.createCompoundBorder(
                        new LineBorder(Color.BLACK, borderThicknessScaled),
                        new EmptyBorder(borderInner, borderInner, borderInner, borderInner)
                )
        ));

        revalidate();
        repaint();
    }
}
