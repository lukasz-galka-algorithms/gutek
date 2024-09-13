package gutek.gui.main;

import gutek.gui.AppView;
import gutek.gui.MainFrame;
import gutek.services.TranslationService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * The `AuthorsView` class represents a view displaying the author and year information.
 * It is used to show the name of the author and the year of creation in a centered layout.
 */
@Component
public class AuthorsView extends AppView {
    /** Label for displaying the author's name. */
    private JLabel authorLabel;
    /** Label for displaying the year. */
    private JLabel yearLabel;
    /**
     * Constructs the `AuthorsView`, initializing the components.
     *
     * @param frame the main frame of the application
     * @param translationService the service used for managing translations
     */
    public AuthorsView(MainFrame frame, TranslationService translationService) {
        super(frame, translationService);

        setLayout(null);

        authorLabel = new JLabel("Łukasz Gałka", SwingConstants.CENTER);
        add(authorLabel);

        yearLabel = new JLabel("Anno Domini 2024", SwingConstants.CENTER);
        add(yearLabel);
    }
    /**
     * Updates the size and layout of the components based on the current window size and scale factor.
     * The labels are centered in the view with dynamic font sizes and positions.
     */
    public void updateSize() {
        super.updateSize();

        Dimension dimensionScaled = frame.getScaledSize();
        double scaleFactor = frame.getScaleFactor();


        this.setPreferredSize(dimensionScaled);

        int labelWidth = (int) (400 * scaleFactor);
        int labelHeight = (int) (50 * scaleFactor);

        authorLabel.setBounds((dimensionScaled.width - labelWidth) / 2, (int) (100 * scaleFactor), labelWidth, labelHeight);
        authorLabel.setFont(new Font("Serif", Font.BOLD, (int) (30 * scaleFactor)));

        yearLabel.setBounds((dimensionScaled.width - labelWidth) / 2, (int) (200 * scaleFactor), labelWidth, labelHeight);
        yearLabel.setFont(new Font("Serif", Font.BOLD, (int) (30 * scaleFactor)));

        revalidate();
        repaint();
    }
    /**
     * Updates the translations for the components. Currently, this view does not have dynamic translations,
     * but this method ensures compatibility with the overall translation structure.
     */
    public void updateTranslation(){
        super.updateTranslation();
    }
}
