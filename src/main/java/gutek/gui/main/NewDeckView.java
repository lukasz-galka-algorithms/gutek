package gutek.gui.main;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.services.CardService;
import gutek.services.DeckService;
import gutek.services.RevisionAlgorithmService;
import gutek.services.TranslationService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * The `NewDeckView` class represents the view for creating a new deck.
 * It allows users to add a new deck with a specified algorithm or import cards from a CSV file.
 */
@Component
public class NewDeckView extends AppView {
    /** Service for managing revision algorithms. */
    private final RevisionAlgorithmService revisionAlgorithmService;
    /** Service for managing decks. */
    private final DeckService deckService;
    /** Service for managing cards. */
    private final CardService cardService;
    /** Label prompting the user to enter the deck name. */
    private JLabel nameLabel;
    /** Text field for the user to input the deck name. */
    private JTextField nameField;
    /** Label prompting the user to select a revision algorithm. */
    private JLabel algorithmLabel;
    /** ComboBox for the user to select a revision algorithm. */
    private JComboBox<String> algorithmComboBox;
    /** Button to add a new deck with the selected name and algorithm. */
    private JButton addButton;
    /** Button to import a deck from a CSV file. */
    private JButton importButton;
    /**
     * Constructs a new `NewDeckView` object.
     *
     * @param frame the main application frame
     * @param translationService the service responsible for handling translations
     * @param revisionAlgorithmService the service providing available revision algorithms
     * @param deckService the service responsible for managing decks
     * @param cardService the service responsible for managing cards
     */
    public NewDeckView(MainFrame frame, TranslationService translationService,
                       RevisionAlgorithmService revisionAlgorithmService,
                       DeckService deckService,
                       CardService cardService) {
        super(frame, translationService);
        this.revisionAlgorithmService = revisionAlgorithmService;
        this.deckService = deckService;
        this.cardService = cardService;

        setLayout(null);

        nameLabel = new JLabel("", SwingConstants.RIGHT);
        nameField = new JTextField();
        algorithmLabel = new JLabel("", SwingConstants.RIGHT);
        algorithmComboBox = new JComboBox<>(revisionAlgorithmService.getAlgorithmNames().toArray(new String[0]));
        addButton = new JButton("");
        importButton = new JButton("");

        add(nameLabel);
        add(nameField);
        add(algorithmLabel);
        add(algorithmComboBox);
        add(addButton);
        add(importButton);

        addButton.addActionListener(e -> handleAddDeck());
        importButton.addActionListener(e -> handleImportDeck());
    }
    /**
     * Updates the size and layout of the UI components according to the current window size and scaling factor.
     */
    @Override
    public void updateSize() {
        super.updateSize();

        Dimension dimensionScaled = frame.getScaledSize();
        double scaleFactor = frame.getScaleFactor();
        this.setPreferredSize(dimensionScaled);

        int labelWidth = (int) (200 * scaleFactor);
        int labelHeight = (int) (30 * scaleFactor);
        int fieldWidth = (int) (300 * scaleFactor);
        int fieldHeight = (int) (30 * scaleFactor);
        int buttonWidth = (int) (200 * scaleFactor);
        int buttonHeight = (int) (40 * scaleFactor);
        int verticalGap = (int) (20 * scaleFactor);

        nameLabel.setBounds((dimensionScaled.width - fieldWidth - labelWidth) / 2, (int) (50 * scaleFactor), labelWidth, labelHeight);
        nameLabel.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));
        nameField.setBounds((dimensionScaled.width - fieldWidth + labelWidth) / 2, (int) (50 * scaleFactor), fieldWidth, fieldHeight);
        nameField.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));

        algorithmLabel.setBounds((dimensionScaled.width - fieldWidth - labelWidth) / 2, (int) (100 * scaleFactor), labelWidth, labelHeight);
        algorithmLabel.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));
        algorithmComboBox.setBounds((dimensionScaled.width - fieldWidth + labelWidth) / 2, (int) (100 * scaleFactor), fieldWidth, fieldHeight);
        algorithmComboBox.setFont(new Font("Serif", Font.BOLD, (int) (10 * scaleFactor)));

        addButton.setBounds((dimensionScaled.width - buttonWidth) / 2, (int) (150 * scaleFactor), buttonWidth, buttonHeight);
        addButton.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));

        importButton.setBounds((dimensionScaled.width - buttonWidth) / 2, (int) (150 * scaleFactor + buttonHeight + verticalGap), buttonWidth, buttonHeight);
        importButton.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));

        revalidate();
        repaint();
    }
    /**
     * Updates the translations for the UI components based on the current locale.
     */
    @Override
    public void updateTranslation() {
        super.updateTranslation();
        nameLabel.setText(translationService.getTranslation("new_deck_view.name_label"));
        algorithmLabel.setText(translationService.getTranslation("new_deck_view.algorithm_label"));
        addButton.setText(translationService.getTranslation("new_deck_view.add_button"));
        importButton.setText(translationService.getTranslation("new_deck_view.import_button"));

        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        algorithmComboBox.removeAllItems();
        for (String algorithmName : revisionAlgorithmService.getAlgorithmNames()) {
            algorithmComboBox.addItem(algorithmName);
        }
        if (selectedAlgorithm != null) {
            algorithmComboBox.setSelectedItem(selectedAlgorithm);
        }
    }
    /**
     * Handles the action of adding a new deck with the specified name and selected algorithm.
     */
    private void handleAddDeck() {
        String deckName = nameField.getText();
        String selectedAlgorithmName = (String) algorithmComboBox.getSelectedItem();

        RevisionAlgorithm<?> algorithm = revisionAlgorithmService.createAlgorithmInstance(selectedAlgorithmName);
        algorithm.setTranslationService(translationService);

        if (deckName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, translationService.getTranslation("new_deck_view.deck_name_empty"));
        } else {
            if (algorithm == null) {
                JOptionPane.showMessageDialog(this, translationService.getTranslation("new_deck_view.algorithm_empty"));
            } else {
                deckService.addNewDeck(frame.getLoggedUser(), algorithm, deckName);

                JOptionPane.showMessageDialog(this, translationService.getTranslation("new_deck_view.deck_added"));
            }
        }
    }
    /**
     * Handles the action of importing a deck from a CSV file and adding it to the system with the selected algorithm.
     */
    private void handleImportDeck() {
        String deckName = nameField.getText();
        String selectedAlgorithmName = (String) algorithmComboBox.getSelectedItem();

        RevisionAlgorithm<?> algorithm = revisionAlgorithmService.createAlgorithmInstance(selectedAlgorithmName);
        algorithm.setTranslationService(translationService);

        if (deckName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, translationService.getTranslation("new_deck_view.deck_name_empty"));
        } else {
            if (algorithm == null) {
                JOptionPane.showMessageDialog(this, translationService.getTranslation("new_deck_view.algorithm_empty"));
            } else {
                JFileChooser fileChooser = new JFileChooser();

                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
                fileChooser.setFileFilter(filter);
                fileChooser.setAcceptAllFileFilterUsed(false);

                int returnValue = fileChooser.showOpenDialog(this);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    DeckBase deckBase = deckService.addNewDeck(frame.getLoggedUser(), algorithm, deckName);

                    try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                        long cardsNumber = Long.parseLong(br.readLine());
                        for (int i = 0; i < cardsNumber; i++) {
                            CardBase card = algorithm.createNewCard(br.readLine(), br.readLine());
                            deckService.addNewCardToDeck(card, deckBase);
                        }

                        JOptionPane.showMessageDialog(this, translationService.getTranslation("new_deck_view.deck_imported"));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, translationService.getTranslation("new_deck_view.import_error"));
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
