package gutek.gui.deck;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.controls.DeckMenuPanel;
import gutek.services.CardService;
import gutek.services.DeckService;
import gutek.services.TranslationService;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Optional;

/**
 * A view allowing users to add new cards to a deck manually or by importing from a CSV file.
 *
 * The class provides an interface for adding cards to the deck through a form with text fields for the front and back of the card.
 * Users can also import multiple cards from a CSV file.
 */
@Component
public class RevisionAddCardView extends AppView {
    /** The panel containing the menu with options related to the deck. */
    private DeckMenuPanel menuBarPanel;
    /** Label for the front side of the card. */
    private JLabel frontLabel;
    /** Label for the back side of the card. */
    private JLabel backLabel;
    /** Text field for entering the front side of the card. */
    private JTextField frontTextField;
    /** Text field for entering the back side of the card. */
    private JTextField backTextField;
    /** Button for adding the new card to the deck. */
    private JButton addButton;
    /** Button for importing multiple cards from a CSV file. */
    private JButton importButton;
    /** Service for managing cards in the application. */
    private final CardService cardService;
    /** Service for managing decks in the application. */
    private final DeckService deckService;
    /** The current deck where cards are being added. */
    @Setter
    private DeckBase deck;
    /**
     * Constructs a new RevisionAddCardView, providing options to add cards manually or import them from a file.
     *
     * @param frame the main frame of the application
     * @param translationService the service used for translating text in the view
     * @param cardService the service used for managing cards
     * @param deckService the service used for managing decks
     */
    public RevisionAddCardView(MainFrame frame, TranslationService translationService,
                               CardService cardService,
                               DeckService deckService) {
        super(frame, translationService);
        this.cardService = cardService;
        this.deckService = deckService;

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
        addButton = new JButton();
        importButton = new JButton("");

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
        cardFormPanel.add(addButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cardFormPanel.add(importButton, gbc);

        add(cardFormPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> {
            String frontText = frontTextField.getText().trim();
            String backText = backTextField.getText().trim();

            if (frontText.isEmpty() || backText.isEmpty()) {
                JOptionPane.showMessageDialog(this, translationService.getTranslation("deck_view.add_card.empty_text"));
                return;
            }

            Optional<CardBase> existingCard = cardService.findCardByFrontAndDeck(frontText, deck);
            if (existingCard.isPresent()) {
                JOptionPane.showMessageDialog(this, translationService.getTranslation("deck_view.add_card.front_unique"));
                return;
            }

            cardService.addNewCard(frontText, backText, deck);

            JOptionPane.showMessageDialog(this, translationService.getTranslation("deck_view.add_card.add_success"));

            frontTextField.setText("");
            backTextField.setText("");
                }
        );

        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);

            int returnValue = fileChooser.showOpenDialog(this);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                    long cardsNumber = Long.parseLong(br.readLine());
                    for (int i = 0; i < cardsNumber; i++) {
                        CardBase card = deck.getRevisionAlgorithm().createNewCard(br.readLine(), br.readLine());
                        deckService.addNewCardToDeck(card, deck);
                    }

                    JOptionPane.showMessageDialog(this, translationService.getTranslation("new_deck_view.deck_imported"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, translationService.getTranslation("new_deck_view.import_error"));
                    ex.printStackTrace();
                }
            }
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
        addButton.setFont(scaledFont);
        importButton.setFont(scaledFont);

        int textFieldWidth = (int) (dimensionScaled.width * 0.3 * scaleFactor);
        int textFieldHeight = (int) (30 * scaleFactor);

        frontTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        backTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));

        int buttonWidth = (int) (100 * scaleFactor);
        int buttonHeight = (int) (40 * scaleFactor);
        addButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        importButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

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

        frontLabel.setText(translationService.getTranslation("deck_view.add_card.front"));
        backLabel.setText(translationService.getTranslation("deck_view.add_card.back"));
        addButton.setText(translationService.getTranslation("deck_view.add_card.add"));
        importButton.setText(translationService.getTranslation("deck_view.add_card.import"));
    }
}
