package gutek.gui.deck;

import gutek.entities.algorithms.AlgorithmHiperparameter;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.decks.DeckBase;
import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.controls.DeckMenuPanel;
import gutek.services.TranslationService;
import gutek.utils.validation.FieldValueValidator;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * A view for adjusting and saving the hyperparameters of a deck's revision algorithm.
 *
 * This view dynamically loads the hyperparameters of the revision algorithm associated with the deck.
 * It allows users to modify and save the settings of the algorithm using a graphical interface.
 */
@Component
public class RevisionSettingsView extends AppView {
    /** The panel containing the deck menu options. */
    private DeckMenuPanel menuBarPanel;
    /** The panel displaying the hyperparameter settings. */
    private JPanel settingsPanel;
    /** Scroll pane to hold the settings panel, allowing scrolling if there are many hyperparameters. */
    private JScrollPane scrollPane;
    /** Button to save the modified settings. */
    private JButton saveButton;
    /** A map that holds the text fields for each hyperparameter, keyed by their field names. */
    private Map<String, JTextField> hiperparameterFields;
    /** A map that holds the labels for each hyperparameter, keyed by their translation keys. */
    private Map<String, JLabel> hiperparameterLabels;
    /** The deck whose algorithm's hyperparameters are being modified. */
    @Setter
    private DeckBase deck;
    /**
     * Constructs a new `RevisionSettingsView` for modifying the settings of a revision algorithm.
     *
     * @param frame the main frame of the application
     * @param translationService the service used for retrieving translations for the UI
     */
    public RevisionSettingsView(MainFrame frame, TranslationService translationService) {
        super(frame, translationService);

        setLayout(new BorderLayout());

        menuBarPanel = new DeckMenuPanel(frame, translationService);
        add(menuBarPanel, BorderLayout.NORTH);

        settingsPanel = new JPanel(new GridBagLayout());
        scrollPane = new JScrollPane(settingsPanel);
        add(scrollPane, BorderLayout.CENTER);

        saveButton = new JButton();
        add(saveButton, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveSettings());
    }
    /**
     * Loads the settings of the revision algorithm for display and modification.
     *
     * This method reflects on the fields of the revision algorithm to extract the annotated
     * hyperparameters and dynamically create corresponding labels and text fields.
     */
    public void loadSettings() {
        settingsPanel.removeAll();
        hiperparameterFields = new HashMap<>();
        hiperparameterLabels = new HashMap<>();

        RevisionAlgorithm<?> algorithm = deck.getRevisionAlgorithm();
        Map<String, Object> hiperparameters = algorithm.getAlgorithmHiperparameters();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        Field[] fields = algorithm.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(AlgorithmHiperparameter.class)) {
                field.setAccessible(true);

                AlgorithmHiperparameter annotation = field.getAnnotation(AlgorithmHiperparameter.class);
                String translationKey = annotation.descriptionTranslationKey();

                JLabel label = new JLabel();
                settingsPanel.add(label, gbc);

                gbc.gridx = 1;
                Object value = hiperparameters.get(field.getName());
                JTextField textField = new JTextField(value != null ? value.toString() : "", 20);
                settingsPanel.add(textField, gbc);

                hiperparameterFields.put(field.getName(), textField);
                hiperparameterLabels.put(translationKey, label);

                gbc.gridx = 0;
                gbc.gridy++;
            }
        }

        settingsPanel.revalidate();
        settingsPanel.repaint();

        updateTranslation();
    }
    /**
     * Saves the modified settings back to the revision algorithm.
     *
     * This method reflects on the fields of the algorithm to apply the values entered by the user.
     * If any value is invalid, an error message is shown, and the saving process is aborted.
     */
    private void saveSettings() {
        Map<String, Object> updatedHiperparameters = new HashMap<>();

        RevisionAlgorithm<?> algorithm = deck.getRevisionAlgorithm();

        Field[] fields = algorithm.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(AlgorithmHiperparameter.class)) {
                field.setAccessible(true);

                String hiperparameterName = field.getName();
                JTextField textField = hiperparameterFields.get(hiperparameterName);
                String valueString = textField.getText();

                try {
                    Object convertedValue = FieldValueValidator.validateAndReturnConverted(algorithm, field.getName(), valueString, translationService);
                    field.set(algorithm, convertedValue);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this,
                            translationService.getTranslation("deck_view.settings.invalid_input")
                            + ": " + translationService.getTranslation(field.getAnnotation(AlgorithmHiperparameter.class).descriptionTranslationKey())
                            + "\n" + e.getMessage()
                    );
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(this, translationService.getTranslation("deck_view.settings.save_success"));
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

        Font scaledFont = new Font("Serif", Font.BOLD, (int) (10 * scaleFactor));

        saveButton.setFont(scaledFont);
        saveButton.setPreferredSize(new Dimension((int) (100 * scaleFactor), (int) (40 * scaleFactor)));

        for (JLabel label : hiperparameterLabels.values()){
            label.setFont(scaledFont);
        }
        for (JTextField textField : hiperparameterFields.values()) {
            textField.setFont(scaledFont);
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

        saveButton.setText(translationService.getTranslation("deck_view.settings.save_button"));

        for (Map.Entry<String, JLabel> entry : hiperparameterLabels.entrySet()) {
            String translationKey = entry.getKey();
            JLabel label = entry.getValue();
            label.setText(translationService.getTranslation(translationKey)); // Set translated text
        }

        settingsPanel.revalidate();
        settingsPanel.repaint();
    }
    /**
     * Updates the view by loading the settings and initializing the view components.
     */
    @Override
    public void updateView(){
        super.updateView();
        loadSettings();
    }
}
