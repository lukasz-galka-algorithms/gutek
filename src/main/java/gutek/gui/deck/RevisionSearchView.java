package gutek.gui.deck;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.controls.CardCell;
import gutek.gui.controls.DeckMenuPanel;
import gutek.services.CardService;
import gutek.services.TranslationService;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A view that allows users to search for cards within a deck.
 *
 * This view provides a form for searching cards based on their front or back text and displays the matching results
 * in a list. Each card can be edited or deleted through the provided action buttons.
 */
@Component
public class RevisionSearchView  extends AppView {
    /** The panel containing the deck menu options. */
    private DeckMenuPanel menuBarPanel;
    /** Label for the text field where the user can enter a search phrase for the front of the card. */
    private JLabel frontInCardLabel;
    /** Label for the text field where the user can enter a search phrase for the back of the card. */
    private JLabel backInCardLabel;
    /** Text field for entering the search phrase for the front of the card. */
    private JTextField frontInCardTextField;
    /** Text field for entering the search phrase for the back of the card. */
    private JTextField backInCardTextField;
    /** Button for initiating the search for cards. */
    private JButton searchButton;
    /** List of card cells displaying the search results. */
    private List<CardCell> cardCellList;
    /** Panel for displaying the list of found cards. */
    private JPanel cardListPanel;
    /** Scroll pane that contains the list of found cards. */
    private JScrollPane scrollPane;
    /** Service for managing card-related actions. */
    private final CardService cardService;
    /** View for editing a card. */
    private final RevisionEditCardView revisionEditCardView;
    /** The deck in which the cards are being searched. */
    @Setter
    private DeckBase deck;
    /**
     * Constructs a new RevisionSearchView for searching and managing cards.
     *
     * @param frame the main frame of the application
     * @param translationService the service used for retrieving translations for the UI
     * @param cardService the service used for managing cards
     * @param revisionEditCardView the view used for editing cards
     */
    public RevisionSearchView(MainFrame frame, TranslationService translationService,
                              CardService cardService,
                              RevisionEditCardView revisionEditCardView) {
        super(frame, translationService);
        this.cardService = cardService;
        this.revisionEditCardView = revisionEditCardView;

        setLayout(new BorderLayout());

        menuBarPanel = new DeckMenuPanel(frame, translationService);
        add(menuBarPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        frontInCardLabel = new JLabel();
        backInCardLabel = new JLabel();
        frontInCardTextField = new JTextField();
        backInCardTextField = new JTextField();
        searchButton = new JButton();
        JPanel leftEmptyFront = new JPanel();
        JPanel leftEmptyBack = new JPanel();
        JPanel rightEmptyFront = new JPanel();
        JPanel rightEmptyBack = new JPanel();
        cardCellList = new ArrayList<>();
        cardListPanel = new JPanel();
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(leftEmptyFront, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.EAST;
        searchPanel.add(frontInCardLabel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.25;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(frontInCardTextField, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.25;
        gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(rightEmptyFront, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(leftEmptyBack, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.EAST;
        searchPanel.add(backInCardLabel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(backInCardTextField, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(rightEmptyBack, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(searchButton, gbc);

        add(searchPanel, BorderLayout.CENTER);

        scrollPane = new JScrollPane(cardListPanel);
        add(scrollPane, BorderLayout.SOUTH);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        searchButton.addActionListener(e -> {
            updateView();
            updateTranslation();
        });
    }
    /**
     * Searches for cards based on the user's input and displays the results in the card list panel.
     */
    private void searchCards() {
        String phraseInFront = frontInCardTextField.getText().trim();
        String phraseInBack = backInCardTextField.getText().trim();

        java.util.List<CardBase> cards = cardService.findCardsByUser(phraseInFront, phraseInBack, deck);
        cardListPanel.removeAll();
        cardCellList.clear();

        for (CardBase card : cards) {
            CardCell cardCell = new CardCell(card, frame, translationService, revisionEditCardView, cardService);
            cardCellList.add(cardCell);
            cardListPanel.add(cardCell);
        }

        cardListPanel.revalidate();
        cardListPanel.repaint();
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

        frontInCardLabel.setFont(scaledFont);
        backInCardLabel.setFont(scaledFont);
        frontInCardTextField.setFont(scaledFont);
        backInCardTextField.setFont(scaledFont);
        searchButton.setFont(scaledFont);

        int textFieldWidth = (int) (dimensionScaled.width * 0.2 * scaleFactor);
        int textFieldHeight = (int) (30 * scaleFactor);

        frontInCardLabel.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        backInCardLabel.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        frontInCardTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        backInCardTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));

        int buttonWidth = (int) (100 * scaleFactor);
        int buttonHeight = (int) (40 * scaleFactor);
        searchButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        scrollPane.setPreferredSize(new Dimension(dimensionScaled.width, (int) (dimensionScaled.height / 3 * scaleFactor)));

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

        frontInCardLabel.setText(translationService.getTranslation("deck_view.search_card.front_phase"));
        backInCardLabel.setText(translationService.getTranslation("deck_view.search_card.back_phase"));
        searchButton.setText(translationService.getTranslation("deck_view.search_card.search"));

        for (CardCell cell : cardCellList) {
            cell.updateTranslation();
        }

        cardListPanel.revalidate();
        cardListPanel.repaint();
    }
    /**
     * Updates the view by executing a search based on the user's input.
     */
    @Override
    public void updateView() {
        super.updateView();
        searchCards();
    }
}
