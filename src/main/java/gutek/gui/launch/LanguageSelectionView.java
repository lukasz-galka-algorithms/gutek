package gutek.gui.launch;

import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.gui.MainFrameViews;
import gutek.services.TranslationService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The view responsible for selecting the application's language.
 * Provides a dropdown list of available languages and a button to confirm the selection.
 */
@Component
public class LanguageSelectionView extends AppView {
    /** Label for selecting the language. */
    JLabel selectLanguageLabel;
    /** ComboBox for displaying available languages. */
    JComboBox<String> languageComboBox;
    /** Button to confirm the selected language. */
    JButton confirmButton;
    /**
     * Constructs a new `LanguageSelectionView` for selecting the application's language.
     *
     * @param frame the main frame of the application
     * @param translationService the service used for managing translations and available locales
     */
    public LanguageSelectionView(MainFrame frame, TranslationService translationService) {
        super(frame, translationService);
        List<Locale> availableLocales = translationService.getAvailableLocales();

        setLayout(null);

        selectLanguageLabel = new JLabel("", SwingConstants.CENTER);
        add(selectLanguageLabel);

        String[] languageNames = availableLocales.stream()
                .map(locale -> locale.getDisplayLanguage(locale))
                .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1))
                .toArray(String[]::new);

        languageComboBox = new JComboBox<>(languageNames);
        Locale currentLocale = translationService.getCurrentLocale();
        int index = availableLocales.indexOf(currentLocale);
        if (index >= 0) {
            languageComboBox.setSelectedIndex(index);
        }
        add(languageComboBox);

        confirmButton = new JButton("");
        add(confirmButton);

        confirmButton.addActionListener(e -> {
            int selectedIndex = languageComboBox.getSelectedIndex();
            Locale selectedLocale = availableLocales.get(selectedIndex);
            translationService.updateLocale(selectedLocale);
            frame.setView(MainFrameViews.LOGIN_VIEW);
        });

        languageComboBox.addActionListener(e -> {
            int selectedIndex = languageComboBox.getSelectedIndex();
            Locale selectedLocale = availableLocales.get(selectedIndex);
            translationService.updateLocale(selectedLocale);
            updateTranslation();
        });
    }
    /**
     * Updates the size and layout of the components based on the current window size and scale factor.
     */
    public void updateSize() {
        super.updateSize();

        Dimension dimensionScaled = frame.getScaledSize();
        double scaleFactor = frame.getScaleFactor();

        this.setPreferredSize(dimensionScaled);

        int labelWidth = (int) (300 * scaleFactor);
        int labelHeight = (int) (50 * scaleFactor);
        selectLanguageLabel.setBounds((dimensionScaled.width - labelWidth) / 2, (int) (20 * scaleFactor), labelWidth, labelHeight);
        selectLanguageLabel.setFont(new Font("Serif", Font.BOLD, (int) (20 * scaleFactor)));

        int comboBoxWidth = (int) (200 * scaleFactor);
        int comboBoxHeight = (int) (30 * scaleFactor);
        languageComboBox.setBounds((dimensionScaled.width - comboBoxWidth) / 2, (int) (100 * scaleFactor), comboBoxWidth, comboBoxHeight);
        languageComboBox.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));

        int buttonWidth = (int) (100 * scaleFactor);
        int buttonHeight = (int) (40 * scaleFactor);
        confirmButton.setBounds((dimensionScaled.width - buttonWidth) / 2, (int) (150 * scaleFactor), buttonWidth, buttonHeight);
        confirmButton.setFont(new Font("Serif", Font.BOLD, (int) (15 * scaleFactor)));

        revalidate();
        repaint();
    }
    /**
     * Updates the text in the view components based on the current language settings.
     */
    public void updateTranslation(){
        super.updateTranslation();

        selectLanguageLabel.setText(translationService.getTranslation("language_selection.select_language"));
        confirmButton.setText(translationService.getTranslation("window.ok_button"));
    }
}
