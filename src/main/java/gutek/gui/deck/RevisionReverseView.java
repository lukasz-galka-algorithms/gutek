package gutek.gui.deck;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.MainFrameViews;
import gutek.gui.controls.DeckMenuPanel;
import gutek.services.CardRevisionService;
import gutek.services.CardService;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

/**
 * A view that facilitates the reverse revision of cards from a deck.
 *
 * This view handles the logic for presenting the back (translation) of a card first, allowing the user to guess the word
 * (front side), and providing revision options based on the deck's revision algorithm.
 */
@Component
public class RevisionReverseView extends AppView {
    /** Panel containing the deck menu. */
    private DeckMenuPanel menuBarPanel;
    /** Service for managing card-related actions. */
    private final CardService cardService;
    /** Service for managing deck statistics. */
    private final DeckStatisticsService deckStatisticsService;
    /** Service for handling card revision processes. */
    private final CardRevisionService cardRevisionService;
    /** Random generator for selecting cards. */
    private final Random random = new Random();
    /** List of old cards that need to be revised. */
    @Setter
    private List<CardBase> oldCardsList;
    /** List of new cards that need to be revised. */
    @Setter
    private List<CardBase> newCardsList;
    /** The current card being revised. */
    private CardBase currentCard;
    /** Panel for displaying the back (translation) of the card. */
    private JPanel translationPanel;
    /** Panel for displaying the front (word) of the card. */
    private JPanel wordPanel;
    /** Panel for displaying action buttons. */
    private JPanel buttonsPanel;
    /** Panel for algorithm-specific buttons. */
    private JPanel algorithmButtonPanel;
    /** Button for revealing the word (front) of the current card. */
    private JButton showButton;
    /** Button for ending the revision session. */
    private JButton endRevisionButton;
    /** Label for displaying the back (translation) of the card. */
    private JLabel translationLabel;
    /** Label for displaying the front (word) of the card. */
    private JLabel wordLabel;
    /** Text field for entering the word (front) of the card. */
    private JTextField wordTextField;
    /**
     * Constructs a new RevisionReverseView to facilitate the reverse revision of cards.
     *
     * @param frame the main frame of the application
     * @param translationService the service used for retrieving translations for the UI
     * @param cardService the service used for managing cards
     * @param deckStatisticsService the service used for managing deck statistics
     * @param cardRevisionService the service used for handling card revisions
     */
    public RevisionReverseView(MainFrame frame, TranslationService translationService, CardService cardService,
                               DeckStatisticsService deckStatisticsService,
                               CardRevisionService cardRevisionService) {
        super(frame, translationService);
        this.cardService = cardService;
        this.deckStatisticsService = deckStatisticsService;
        this.cardRevisionService =cardRevisionService;

        setLayout(new BorderLayout());

        menuBarPanel = new DeckMenuPanel(frame, translationService);
        add(menuBarPanel, BorderLayout.NORTH);

        translationPanel = new JPanel();
        wordPanel = new JPanel();
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 1));

        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 5);
        Border marginBorder = new EmptyBorder(10, 10, 10, 10);
        Border compoundBorder = new CompoundBorder(marginBorder, lineBorder);
        wordPanel.setBorder(compoundBorder);
        translationPanel.setBorder(compoundBorder);
        buttonsPanel.setBorder(compoundBorder);

        wordPanel.setLayout(new GridLayout(1, 2));
        translationPanel.setLayout(new BorderLayout());

        translationLabel = new JLabel("", JLabel.CENTER);
        translationPanel.add(translationLabel, BorderLayout.CENTER);
        wordLabel = new JLabel("", JLabel.CENTER);
        wordTextField = new JTextField();
        wordPanel.add(wordLabel);
        wordPanel.add(wordTextField);

        showButton = new JButton("");
        showButton.addActionListener(e -> showWord());
        endRevisionButton = new JButton("");
        endRevisionButton.addActionListener(e -> {
            frame.setView(MainFrameViews.REVISION_REVISE_VIEW);
        });

        JPanel mainPanel = new JPanel(new GridLayout(3, 1));
        mainPanel.add(translationPanel);
        mainPanel.add(wordPanel);
        mainPanel.add(buttonsPanel);
        add(mainPanel, BorderLayout.CENTER);
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

        translationLabel.setFont(scaledFont);
        wordLabel.setFont(scaledFont);
        wordTextField.setFont(scaledFont);
        showButton.setFont(scaledFont);
        endRevisionButton.setFont(scaledFont);
        if (currentCard != null) {
            currentCard.getDeck().getRevisionAlgorithm().updateSize(dimensionScaled, scaleFactor);
        }

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

        showButton.setText(translationService.getTranslation("deck_view.reverse_revision.show_button"));
        endRevisionButton.setText(translationService.getTranslation("deck_view.reverse_revision.end_button"));

        if (currentCard != null) {
            currentCard.getDeck().getRevisionAlgorithm().updateTranslation();
        }else {
            translationLabel.setText(translationService.getTranslation("deck_view.reverse_revision.end_title"));
            wordLabel.setText(translationService.getTranslation("deck_view.reverse_revision.end_message"));
        }
    }
    /**
     * Updates the view by loading the next card and setting up the revision buttons.
     */
    @Override
    public void updateView() {
        super.updateView();
        handleNextCard();
        if (currentCard != null) {
            currentCard.getDeck().getRevisionAlgorithm().setTranslationService(translationService);
        }
    }
    /**
     * Displays the word (front) of the current card.
     */
    private void showWord() {
        translationLabel.setText(currentCard.getBack());
        wordLabel.setText(currentCard.getFront());

        wordPanel.removeAll();
        wordPanel.add(wordLabel);
        wordPanel.add(wordTextField);
        buttonsPanel.removeAll();
        buttonsPanel.add(algorithmButtonPanel);

        revalidate();
        repaint();
    }
    /**
     * Displays the message indicating the end of the revision session.
     */
    private void showRevisionEnd() {
        translationLabel.setText(translationService.getTranslation("deck_view.reverse_revision.end_title"));
        wordLabel.setText(translationService.getTranslation("deck_view.reverse_revision.end_message"));

        wordPanel.removeAll();
        wordPanel.add(wordLabel);
        buttonsPanel.removeAll();
        buttonsPanel.add(endRevisionButton);

        revalidate();
        repaint();
    }
    /**
     * Displays the translation (back) of the card and prepares the button to reveal the word (front).
     */
    private void showTranslation() {
        translationLabel.setText(currentCard.getBack());
        wordLabel.setText("");
        wordTextField.setText("");

        wordPanel.removeAll();
        wordPanel.add(wordLabel);
        wordPanel.add(wordTextField);
        buttonsPanel.removeAll();
        buttonsPanel.add(showButton);

        revalidate();
        repaint();
    }
    /**
     * Loads the next card to be revised from the list of old or new cards.
     */
    public void loadNextCard() {
        if (oldCardsList.isEmpty() && newCardsList.isEmpty()) {
            currentCard = null;
            return;
        }

        int oldCardsSize = oldCardsList.size();
        int newCardsSize = newCardsList.size();
        int totalSize = oldCardsSize + newCardsSize;

        int randomIndex = random.nextInt(totalSize);
        if (randomIndex < oldCardsSize) {
            currentCard = oldCardsList.get(randomIndex);
        } else {
            currentCard = newCardsList.get(randomIndex - oldCardsSize);
        }

        algorithmButtonPanel = loadButtonsPanel();
    }
    /**
     * Handles loading the next card and displaying it in the view.
     */
    private void handleNextCard() {
        loadNextCard();
        if (currentCard != null) {
            showTranslation();
        } else {
            showRevisionEnd();
        }
    }
    /**
     * Loads the algorithm-specific buttons for revising the current card.
     *
     * @param <T> the type of card being revised
     * @return the panel containing the algorithm-specific buttons
     */
    private <T extends CardBase> JPanel loadButtonsPanel() {
        RevisionAlgorithm<T> algorithm = (RevisionAlgorithm<T>) currentCard.getDeck().getRevisionAlgorithm();
        JPanel panel = algorithm.getReverseRevisionButtonsPanel((T) currentCard);

        java.awt.Component[] components = panel.getComponents();

        for (int i = 0; i < components.length; i++) {
            java.awt.Component comp = components[i];
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
                int pressedButtonIndex = i;
                button.addActionListener(e -> {
                    buttonsPanel.removeAll();
                    cardRevisionService.reviseReverse(currentCard, pressedButtonIndex);
                    if (currentCard.isNewCard()) {
                        deckStatisticsService.newCardRevised(currentCard.getDeck().getDeckBaseStatistics().getIdDeckStatistics());
                    }
                    boolean cardRevisionFinished = algorithm.reversReviseCard(button, (T) currentCard);
                    if (cardRevisionFinished) {
                        newCardsList.remove(currentCard);
                        oldCardsList.remove(currentCard);
                        deckStatisticsService.cardRevisedReverse(currentCard.getDeck().getDeckBaseStatistics().getIdDeckStatistics());
                    }
                    currentCard.setNewCard(false);
                    cardService.saveCard(currentCard);
                    handleNextCard();
                });
            }
        }

        return panel;
    }
}
