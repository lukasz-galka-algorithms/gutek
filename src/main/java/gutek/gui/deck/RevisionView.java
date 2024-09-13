package gutek.gui.deck;

import gutek.entities.decks.DeckBase;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.controls.DeckMenuPanel;
import gutek.services.DeckService;
import gutek.services.DeckStatisticsService;
import gutek.services.TranslationService;
import gutek.utils.NumberTextField;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import static gutek.gui.MainFrameViews.REVISION_REGULAR_VIEW;
import static gutek.gui.MainFrameViews.REVISION_REVERSE_VIEW;

/**
 * The view responsible for managing card revisions.
 * It provides the ability to start regular or reverse revisions,
 * and allows setting the number of new cards to review per day.
 */
@Component
public class RevisionView extends AppView {
    /** The panel that contains the deck menu options. */
    private DeckMenuPanel menuBarPanel;
    /** The deck for which the revision is being managed. */
    @Setter
    private DeckBase deck;
    /** Label for the number of new cards per day input. */
    private JLabel newCardsPerDayLabel;
    /** Text field to set the number of new cards to revise each day. */
    private NumberTextField newCardsPerDayTextField;
    /** Button to start the regular revision process. */
    private JButton regularRevisionButton;
    /** Button to start the reverse revision process. */
    private JButton reverseRevisionButton;
    /** Label for the number of new regular revision cards. */
    private JLabel regularNewCardsLabel;
    /** Label for the number of old regular revision cards. */
    private JLabel regularOldCardsLabel;
    /** Label for the number of new reverse revision cards. */
    private JLabel reverseNewCardsLabel;
    /** Label for the number of old reverse revision cards. */
    private JLabel reverseOldCardsLabel;
    /** Service for managing the statistics of the deck. */
    private final DeckStatisticsService deckStatisticsService;
    /** Service for managing deck operations. */
    private final DeckService deckService;
    /**
     * Constructs a new `RevisionView` for managing card revisions.
     *
     * @param frame the main frame of the application
     * @param translationService the service used for retrieving translations for the UI
     * @param deckStatisticsService the service used for managing deck statistics
     * @param deckService the service used for managing decks
     * @param revisionRegularView the view for regular card revision
     * @param revisionReverseView the view for reverse card revision
     */
    protected RevisionView(MainFrame frame, TranslationService translationService,
                           DeckStatisticsService deckStatisticsService,
                           DeckService deckService,
                           RevisionRegularView revisionRegularView,
                           RevisionReverseView revisionReverseView) {
        super(frame, translationService);
        this.deckStatisticsService = deckStatisticsService;
        this.deckService = deckService;

        setLayout(new BorderLayout());

        menuBarPanel = new DeckMenuPanel(frame, translationService);
        add(menuBarPanel, BorderLayout.NORTH);

        newCardsPerDayLabel = new JLabel();
        newCardsPerDayTextField = new NumberTextField();
        newCardsPerDayTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateNewCardsPerDay();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateNewCardsPerDay();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateNewCardsPerDay();
            }
        });
        regularRevisionButton = new JButton();
        reverseRevisionButton = new JButton();
        regularNewCardsLabel = new JLabel();
        regularOldCardsLabel = new JLabel();
        reverseNewCardsLabel = new JLabel();
        reverseOldCardsLabel = new JLabel();
        Color newCardsColor = new Color(0, 0, 255);
        Color oldCardsColor = new Color(255, 0, 0);
        regularNewCardsLabel.setForeground(newCardsColor);
        regularOldCardsLabel.setForeground(oldCardsColor);
        reverseNewCardsLabel.setForeground(newCardsColor);
        reverseOldCardsLabel.setForeground(oldCardsColor);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        centerPanel.add(newCardsPerDayLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(newCardsPerDayTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(regularNewCardsLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(regularOldCardsLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        centerPanel.add(regularRevisionButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(reverseNewCardsLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        centerPanel.add(reverseOldCardsLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        centerPanel.add(reverseRevisionButton, gbc);

        add(centerPanel, BorderLayout.CENTER);

        regularRevisionButton.addActionListener(e->{
            revisionRegularView.setOldCardsList(deckService.getRegularRevisionCards(deck));
            revisionRegularView.setNewCardsList(deckService.getNewCardsForTodayRevision(deck, deckStatisticsService.getNewCardsForToday(deck.getDeckBaseStatistics().getIdDeckStatistics())));

            frame.setView(REVISION_REGULAR_VIEW);
        });
        reverseRevisionButton.addActionListener(e->{
            revisionReverseView.setOldCardsList(deckService.getReverseRevisionCards(deck));
            revisionReverseView.setNewCardsList(deckService.getNewCardsForTodayRevision(deck, deckStatisticsService.getNewCardsForToday(deck.getDeckBaseStatistics().getIdDeckStatistics())));

            frame.setView(REVISION_REVERSE_VIEW);
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
        newCardsPerDayLabel.setFont(scaledFont);
        regularNewCardsLabel.setFont(scaledFont);
        regularOldCardsLabel.setFont(scaledFont);
        reverseNewCardsLabel.setFont(scaledFont);
        reverseOldCardsLabel.setFont(scaledFont);
        newCardsPerDayTextField.setFont(scaledFont);
        regularRevisionButton.setFont(scaledFont);
        reverseRevisionButton.setFont(scaledFont);

        int textFieldWidth = (int) (dimensionScaled.width * 0.2 * scaleFactor);
        int textFieldHeight = (int) (30 * scaleFactor);
        newCardsPerDayTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));

        int buttonWidth = (int) (100 * scaleFactor);
        int buttonHeight = (int) (40 * scaleFactor);
        regularRevisionButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        reverseRevisionButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

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

        newCardsPerDayLabel.setText(translationService.getTranslation("deck_view.revise.new_cards_per_day"));

        regularNewCardsLabel.setText(translationService.getTranslation("deck_view.revise.new_cards_regular")
                + ": " + deckStatisticsService.getNewCardsForToday(deck.getDeckBaseStatistics().getIdDeckStatistics()));
        regularOldCardsLabel.setText(translationService.getTranslation("deck_view.revise.old_cards_regular")
                + ": " + deckService.getRegularRevisionCards(deck).size());
        regularRevisionButton.setText(translationService.getTranslation("deck_view.revise.regular_button"));

        reverseNewCardsLabel.setText(translationService.getTranslation("deck_view.revise.new_cards_reverse")
                + ": " + deckStatisticsService.getNewCardsForToday(deck.getDeckBaseStatistics().getIdDeckStatistics()));
        reverseOldCardsLabel.setText(translationService.getTranslation("deck_view.revise.old_cards_reverse")
                + ": " + deckService.getReverseRevisionCards(deck).size());
        reverseRevisionButton.setText(translationService.getTranslation("deck_view.revise.reverse_button"));
    }
    /**
     * Sets the current deck and updates the new cards per day field with the value from the deck's statistics.
     *
     * @param deck the deck to be revised
     */
    public void setDeck(DeckBase deck) {
        this.deck = deck;

        if (deck != null && deck.getDeckBaseStatistics() != null) {
            newCardsPerDayTextField.setText(deck.getDeckBaseStatistics().getNewCardsPerDay().toString());
        }
    }
    /**
     * Updates the number of new cards per day for the current deck.
     * Saves the new value to the deck's statistics.
     */
    private void updateNewCardsPerDay() {
        try {
            int newCardsPerDay = Integer.parseInt(newCardsPerDayTextField.getText());
            deck.getDeckBaseStatistics().setNewCardsPerDay(newCardsPerDay);
            deckStatisticsService.saveDeckStatistics(deck.getDeckBaseStatistics());
            updateTranslation();
        } catch (NumberFormatException ex) {
        }
    }
}
