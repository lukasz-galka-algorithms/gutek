package gutek.gui.deck;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.MainFrameViews;
import gutek.gui.controls.DeckMenuPanel;
import gutek.services.CardService;
import gutek.services.TranslationService;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * A view that allows users to edit existing cards in a deck.
 *
 * This class provides an interface for updating the front and back text of a card, ensuring that
 * the front text is unique within the deck. It also provides a menu for navigating other deck-related actions.
 */
@Component
public class RevisionEditCardView extends AppView {
    /** The panel containing the deck menu options. */
    private DeckMenuPanel menuBarPanel;
    /** Label for the front side of the card. */
    private JLabel frontLabel;
    /** Label for the back side of the card. */
    private JLabel backLabel;
    /** Text field for entering the front text of the card. */
    private JTextField frontTextField;
    /** Text field for entering the back text of the card. */
    private JTextField backTextField;
    /** Button for saving changes to the card. */
    private JButton saveButton;
    /** Service for managing cards in the application. */
    private final CardService cardService;
    /** The deck that contains the card being edited. */
    @Setter
    private DeckBase deck;
    /** The card that is currently being edited. */
    private CardBase cardToEdit;
    /**
     * Constructs a new RevisionEditCardView for editing an existing card.
     *
     * @param frame the main frame of the application
     * @param translationService the service used for translating text in the view
     * @param cardService the service used for managing cards
     */
    public RevisionEditCardView(MainFrame frame, TranslationService translationService,
                                CardService cardService) {
        super(frame, translationService);
        this.cardService = cardService;

        setLayout(new BorderLayout());

        menuBarPanel = new DeckMenuPanel(frame, translationService);
        add(menuBarPanel, BorderLayout.NORTH);

        JPanel cardFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        frontLabel = new JLabel();
        backLabel = new JLabel();
        frontTextField = new JTextField();
        backTextField = new JTextField();
        saveButton = new JButton();

        gbc.gridx = 0;
        gbc.gridy = 0;
        cardFormPanel.add(frontLabel, gbc);

        gbc.gridx = 1;
        cardFormPanel.add(frontTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        cardFormPanel.add(backLabel, gbc);

        gbc.gridx = 1;
        cardFormPanel.add(backTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cardFormPanel.add(saveButton, gbc);

        add(cardFormPanel, BorderLayout.CENTER);

        saveButton.addActionListener(e -> {
            String frontText = frontTextField.getText().trim();
            String backText = backTextField.getText().trim();

            if (frontText.isEmpty() || backText.isEmpty()) {
                JOptionPane.showMessageDialog(this, translationService.getTranslation("deck_view.edit_card.empty_text"));
                return;
            }

            Optional<CardBase> existingCard = cardService.findCardByFrontAndDeck(frontText, deck);
            if (existingCard.isPresent() && !existingCard.get().getIdCard().equals(cardToEdit.getIdCard())) {
                JOptionPane.showMessageDialog(this, translationService.getTranslation("deck_view.edit_card.front_unique"));
                return;
            }

            cardToEdit.setFront(frontText);
            cardToEdit.setBack(backText);
            cardService.saveCard(cardToEdit);
            JOptionPane.showMessageDialog(this, translationService.getTranslation("deck_view.edit_card.edit_success"));

            frame.setView(MainFrameViews.REVISION_SEARCH_VIEW);
        });
    }
    /**
     * Updates the size of the view components based on the window size and scale factor.
     */
    @Override
    public void updateSize() {
        super.updateSize();

        Dimension dimensionScaled = frame.getScaledSize();
        double scaleFactor = frame.getScaleFactor();

        this.setPreferredSize(dimensionScaled);
        menuBarPanel.updateSize(dimensionScaled, scaleFactor);

        Font scaledFont = new Font("Serif", Font.BOLD, (int) (12 * scaleFactor));
        frontLabel.setFont(scaledFont);
        backLabel.setFont(scaledFont);
        frontTextField.setFont(scaledFont);
        backTextField.setFont(scaledFont);
        saveButton.setFont(scaledFont);

        int textFieldWidth = (int) (dimensionScaled.width * 0.3 * scaleFactor);
        int textFieldHeight = (int) (30 * scaleFactor);

        frontTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        backTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));

        int buttonWidth = (int) (100 * scaleFactor);
        int buttonHeight = (int) (40 * scaleFactor);
        saveButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        revalidate();
        repaint();
    }
    /**
     * Updates the text in the view components based on the current language settings.
     */
    @Override
    public void updateTranslation() {
        super.updateTranslation();
        menuBarPanel.updateTranslation();

        frontLabel.setText(translationService.getTranslation("deck_view.edit_card.front"));
        backLabel.setText(translationService.getTranslation("deck_view.edit_card.back"));
        saveButton.setText(translationService.getTranslation("deck_view.edit_card.save_button"));

        setCardToEdit(cardToEdit);
    }
    /**
     * Sets the card to be edited and populates the text fields with the card's existing front and back text.
     *
     * @param card the card to edit
     */
    public void setCardToEdit(CardBase card) {
        this.cardToEdit = card;
        frontTextField.setText(card.getFront());
        backTextField.setText(card.getBack());
    }
}
