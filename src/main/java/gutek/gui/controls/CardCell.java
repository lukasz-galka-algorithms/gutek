package gutek.gui.controls;

import gutek.entities.cards.CardBase;
import gutek.gui.MainFrame;
import gutek.gui.MainFrameViews;
import gutek.gui.deck.RevisionEditCardView;
import gutek.services.CardService;
import gutek.services.TranslationService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A custom JPanel representing a card in a deck, displaying the front and back of the card,
 * along with buttons to edit or delete the card.
 *
 * This panel allows for editing or deleting a card through actions performed on the edit and delete buttons.
 * The panel dynamically updates its content based on the current language and resizing.
 */
public class CardCell extends JPanel {
    /** Label to display the front content of the card. */
    private JLabel frontLabel;
    /** Label to display the back content of the card. */
    private JLabel backLabel;
    /** Button for editing the card. */
    private JButton editButton;
    /** Button for deleting the card. */
    private JButton deleteButton;
    /** The card associated with this cell. */
    private final CardBase card;
    /** Service for translation, used to localize text on the panel. */
    private final TranslationService translationService;
    /** Service for handling card operations such as deletion. */
    private final CardService cardService;
    /**
     * Constructs a new CardCell for displaying and interacting with a card.
     *
     * @param card the card represented by this panel
     * @param mainFrame the main frame of the application, used for switching views
     * @param translationService the service used for retrieving translations
     * @param revisionEditCardView the view used for editing cards
     * @param cardService the service used for card-related operations, such as deletion
     */
    public CardCell(CardBase card, MainFrame mainFrame, TranslationService translationService, RevisionEditCardView revisionEditCardView,
                    CardService cardService) {
        this.translationService = translationService;
        this.card = card;
        this.cardService = cardService;

        frontLabel = new JLabel();
        frontLabel.setForeground(new Color(0, 100, 0));
        backLabel = new JLabel();
        backLabel.setForeground(Color.BLUE);

        editButton = new JButton();
        editButton.setBackground(new Color(70, 130, 180));
        editButton.setForeground(Color.WHITE);

        deleteButton = new JButton();
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(frontLabel, gbc);

        gbc.gridx = 1;
        add(backLabel, gbc);

        gbc.gridx = 2;
        add(editButton, gbc);

        gbc.gridx = 3;
        add(deleteButton, gbc);

        setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        editButton.addActionListener(e->{
            revisionEditCardView.setCardToEdit(card);
            mainFrame.setView(MainFrameViews.REVISION_EDIT_CARD_VIES);
        });

        deleteButton.addActionListener(e->{
            handleDelete();
        });
    }
    /**
     * Handles the deletion of the card, asking for confirmation before proceeding.
     */
    private void handleDelete(){
        int confirm = JOptionPane.showConfirmDialog(
                this,
                translationService.getTranslation("deck_view.search_card.delete_confirm"),
                translationService.getTranslation("deck_view.search_card.delete_title"),
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            cardService.removeCard(card);
            JOptionPane.showMessageDialog(this, translationService.getTranslation("deck_view.search_card.delete_success"));

            refreshParentUI();
        }
    }
    /**
     * Refreshes the UI by removing this card cell from its parent container.
     */
    private void refreshParentUI() {
        Container parent = this.getParent();
        if (parent != null) {
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        }
    }
    /**
     * Updates the size of the labels and buttons based on the provided scale factor.
     *
     * @param scaleFactor the factor by which to scale the font and button sizes
     */
    public void updateSize(double scaleFactor) {
        Font scaledFont = new Font("Serif", Font.BOLD, (int) (10 * scaleFactor));

        frontLabel.setFont(scaledFont);
        backLabel.setFont(scaledFont);
        editButton.setFont(scaledFont);
        deleteButton.setFont(scaledFont);

        int buttonWidth = (int) (60 * scaleFactor);
        int buttonHeight = (int) (20 * scaleFactor);

        editButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        deleteButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        revalidate();
        repaint();
    }
    /**
     * Updates the labels and button text based on the current language using the translation service.
     */
    public void updateTranslation() {
        frontLabel.setText(translationService.getTranslation("deck_view.search_card.front") + ": " + card.getFront());
        backLabel.setText(translationService.getTranslation("deck_view.search_card.back") + ": " + card.getBack());
        editButton.setText(translationService.getTranslation("deck_view.search_card.edit"));
        deleteButton.setText(translationService.getTranslation("deck_view.search_card.delete"));
    }
}