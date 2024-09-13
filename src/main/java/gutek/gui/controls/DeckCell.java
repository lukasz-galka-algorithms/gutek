package gutek.gui.controls;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.MainFrame;
import gutek.gui.MainFrameViews;
import gutek.gui.deck.*;
import gutek.gui.main.DecksView;
import gutek.services.DeckService;
import gutek.services.TranslationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * A custom JPanel representing a deck in the user interface, displaying deck details and providing options
 * to open, delete, or export the deck.
 *
 * This panel includes labels to display deck statistics, such as the number of regular and reverse revision cards,
 * new cards, and all cards. It also provides buttons to open the deck, delete it, or export its content to a CSV file.
 */
public class DeckCell extends JPanel {
    /** Label for the deck name header. */
    private JLabel deckNameLabel;
    /** Label to display the deck name. */
    private JLabel deckName;
    /** Label for the number of regular revision cards header. */
    private JLabel regularRevisionCardsNumberLabel;
    /** Label to display the number of regular revision cards. */
    private JLabel regularRevisionCardsNumber;
    /** Label for the number of reverse revision cards header. */
    private JLabel reverseRevisionCardsNumberLabel;
    /** Label to display the number of reverse revision cards. */
    private JLabel reverseRevisionCardsNumber;
    /** Label for the number of new cards header. */
    private JLabel newCardsNumberLabel;
    /** Label to display the number of new cards. */
    private JLabel newCardsNumber;
    /** Label for the total number of cards header. */
    private JLabel allCardsNumberLabel;
    /** Label to display the total number of cards. */
    private JLabel allCardsNumber;
    /** Label for the revision algorithm header. */
    private JLabel revisionAlgorithmLabel;
    /** Label to display the name of the revision algorithm used for the deck. */
    private JLabel revisionAlgorithm;
    /** Button to delete the deck. */
    private JButton buttonDelete;
    /** Button to open the deck. */
    private JButton buttonOpen;
    /** Button to export the deck data to a CSV file. */
    private JButton buttonExport;
    /** The translation service used for localizing text. */
    private final TranslationService translationService;
    /** The deck associated with this panel. */
    private final DeckBase deck;
    /** The view containing the list of decks. */
    private final DecksView decksView;
    /** The view for adding new cards to the deck. */
    private final RevisionAddCardView addCardView;
    /** The view for searching cards in the deck. */
    private final RevisionSearchView searchView;
    /** The view for revising cards in the deck. */
    private final RevisionView revisionView;
    /** The view for configuring deck settings. */
    private final RevisionSettingsView settingsView;
    /** The view for displaying deck statistics. */
    private final RevisionStatisticsView statisticsView;
    /** The view for editing cards in the deck. */
    private final RevisionEditCardView revisionEditCardView;
    /** The main frame of the application. */
    private final MainFrame mainFrame;
    /** The service for interacting with deck data. */
    private final DeckService deckService;
    /**
     * Constructs a new DeckCell panel to display deck information and provide actions for deck management.
     *
     * @param deck the deck represented by this panel
     * @param decksView the view containing the list of decks
     * @param addCardView the view for adding new cards
     * @param searchView the view for searching cards in the deck
     * @param revisionView the view for reviewing the deck
     * @param settingsView the view for deck settings
     * @param statisticsView the view for deck statistics
     * @param revisionEditCardView the view for editing cards in the deck
     * @param mainFrame the main frame of the application
     * @param translationService the service used for retrieving translations
     * @param deckService the service used for interacting with deck data
     */
    public DeckCell(DeckBase deck, DecksView decksView,
                    RevisionAddCardView addCardView,
                    RevisionSearchView searchView,
                    RevisionView revisionView,
                    RevisionSettingsView settingsView,
                    RevisionStatisticsView statisticsView,
                    RevisionEditCardView revisionEditCardView,
                    MainFrame mainFrame, TranslationService translationService,
                    DeckService deckService) {
        this.deck = deck;
        this.decksView = decksView;
        this.translationService = translationService;
        this.addCardView = addCardView;
        this.searchView = searchView;
        this.revisionView = revisionView;
        this.settingsView = settingsView;
        this.statisticsView = statisticsView;
        this.revisionEditCardView = revisionEditCardView;
        this.mainFrame = mainFrame;
        this.deckService = deckService;

        this.deck.getRevisionAlgorithm().setTranslationService(translationService);

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        deckNameLabel = new JLabel("", SwingConstants.CENTER);
        deckNameLabel.setForeground(Color.BLACK);
        deckName = new JLabel("", SwingConstants.CENTER);
        deckName.setForeground(Color.BLACK);
        regularRevisionCardsNumberLabel = new JLabel("", SwingConstants.CENTER);
        regularRevisionCardsNumberLabel.setForeground(Color.RED);
        regularRevisionCardsNumber = new JLabel("", SwingConstants.CENTER);
        regularRevisionCardsNumber.setForeground(Color.RED);
        reverseRevisionCardsNumberLabel = new JLabel("", SwingConstants.CENTER);
        reverseRevisionCardsNumberLabel.setForeground(Color.MAGENTA);
        reverseRevisionCardsNumber = new JLabel("", SwingConstants.CENTER);
        reverseRevisionCardsNumber.setForeground(Color.MAGENTA);
        newCardsNumberLabel = new JLabel("", SwingConstants.CENTER);
        newCardsNumberLabel.setForeground(Color.BLUE);
        newCardsNumber = new JLabel("", SwingConstants.CENTER);
        newCardsNumber.setForeground(Color.BLUE);
        allCardsNumberLabel = new JLabel("", SwingConstants.CENTER);
        allCardsNumberLabel.setForeground(new Color(0, 100, 0));
        allCardsNumber = new JLabel("", SwingConstants.CENTER);
        allCardsNumber.setForeground(new Color(0, 100, 0));

        buttonOpen = new JButton("");
        buttonDelete = new JButton("");
        buttonExport = new JButton("");

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(deckNameLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(regularRevisionCardsNumberLabel, gbc);
        gbc.gridx = 2;
        mainPanel.add(reverseRevisionCardsNumberLabel, gbc);
        gbc.gridx = 3;
        mainPanel.add(newCardsNumberLabel, gbc);
        gbc.gridx = 4;
        mainPanel.add(allCardsNumberLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(deckName, gbc);
        gbc.gridx = 1;
        mainPanel.add(regularRevisionCardsNumber, gbc);
        gbc.gridx = 2;
        mainPanel.add(reverseRevisionCardsNumber, gbc);
        gbc.gridx = 3;
        mainPanel.add(newCardsNumber, gbc);
        gbc.gridx = 4;
        mainPanel.add(allCardsNumber, gbc);

        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(buttonOpen, gbc);
        gbc.gridx = 6;
        mainPanel.add(buttonDelete, gbc);

        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(buttonExport, gbc);

        JPanel algorithmPanel = new JPanel(new GridBagLayout());
        revisionAlgorithmLabel = new JLabel("", SwingConstants.CENTER);
        revisionAlgorithmLabel.setForeground(new Color(204, 102, 0));
        revisionAlgorithm = new JLabel("", SwingConstants.CENTER);
        revisionAlgorithm.setForeground(new Color(204, 102, 0));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        algorithmPanel.add(revisionAlgorithmLabel, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        algorithmPanel.add(revisionAlgorithm, gbc);

        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(5, 5, 5, 5)
        ));

        algorithmPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.DARK_GRAY, 2),
                new EmptyBorder(5, 5, 5, 5)
        ));

        add(mainPanel, BorderLayout.CENTER);
        add(algorithmPanel, BorderLayout.NORTH);

        buttonDelete.addActionListener(e -> handleDelete());
        buttonOpen.addActionListener(e -> handleOpen());
        buttonExport.addActionListener(e -> handleExport());
    }
    /**
     * Handles deleting the current deck and refreshing the view.
     */
    private void handleDelete() {
        deckService.deleteDeck(deck);

        decksView.updateView();
        decksView.updateSize();
        decksView.updateTranslation();
    }
    /**
     * Handles opening the deck for revision and sets the current view in the main frame.
     */
    private void handleOpen() {
        addCardView.setDeck(deck);
        searchView.setDeck(deck);
        revisionView.setDeck(deck);
        settingsView.setDeck(deck);
        statisticsView.setDeck(deck);
        revisionEditCardView.setDeck(deck);
        mainFrame.setView(MainFrameViews.REVISION_REVISE_VIEW);
    }
    /**
     * Handles exporting the deck data to a CSV file.
     */
    public void handleExport(){
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int returnValue = fileChooser.showSaveDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                List<CardBase> allCards = deckService.getAllCards(deck);
                writer.write(String.valueOf(allCards.size()));
                for (CardBase card : deckService.getAllCards(deck)){
                    writer.write("\n");
                    writer.write(card.getFront());
                    writer.write("\n");
                    writer.write(card.getBack());
                }
                JOptionPane.showMessageDialog(this, translationService.getTranslation("decks_view.export_success"));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, translationService.getTranslation("decks_view.export_fail"));
            }
        }
    }
    /**
     * Updates the text labels and buttons according to the current language settings.
     */
    public void updateTranslation(){
        deckNameLabel.setText(translationService.getTranslation("decks_view.deck_name"));
        deckName.setText(deck.getName());
        regularRevisionCardsNumberLabel.setText(translationService.getTranslation("decks_view.regular_cards_number"));
        regularRevisionCardsNumber.setText(String.valueOf(deckService.getRegularRevisionCards(deck).size()));
        reverseRevisionCardsNumberLabel.setText(translationService.getTranslation("decks_view.reverse_cards_number"));
        reverseRevisionCardsNumber.setText(String.valueOf(deckService.getReverseRevisionCards(deck).size()));
        newCardsNumberLabel.setText(translationService.getTranslation("decks_view.new_cards_number"));
        newCardsNumber.setText(String.valueOf(deckService.getAllNewCards(deck).size()));
        allCardsNumberLabel.setText(translationService.getTranslation("decks_view.all_cards_number"));
        allCardsNumber.setText(String.valueOf(deckService.getAllCards(deck).size()));
        revisionAlgorithmLabel.setText(translationService.getTranslation("decks_view.revision_algorithm"));
        revisionAlgorithm.setText(deck.getRevisionAlgorithm().getAlgorithmName());
        buttonDelete.setText(translationService.getTranslation("decks_view.delete_button"));
        buttonOpen.setText(translationService.getTranslation("decks_view.open_button"));
        buttonExport.setText(translationService.getTranslation("decks_view.export_button"));
    }
    /**
     * Updates the size of the labels, buttons, and panel borders based on the provided scale factor.
     *
     * @param dimensionScaled the scaled dimension for the panel
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
        regularRevisionCardsNumberLabel.setFont(scaledFont);
        regularRevisionCardsNumber.setFont(scaledFont);
        reverseRevisionCardsNumberLabel.setFont(scaledFont);
        reverseRevisionCardsNumber.setFont(scaledFont);
        newCardsNumberLabel.setFont(scaledFont);
        newCardsNumber.setFont(scaledFont);
        allCardsNumberLabel.setFont(scaledFont);
        allCardsNumber.setFont(scaledFont);
        revisionAlgorithmLabel.setFont(scaledFont);
        revisionAlgorithm.setFont(scaledFont);

        buttonOpen.setFont(scaledFont);
        buttonDelete.setFont(scaledFont);
        buttonExport.setFont(scaledFont);

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
