package gutek.gui;

import gutek.services.TranslationService;

import javax.swing.*;

/**
 * The `AppView` class serves as an abstract base class for all views (panels) in the application.
 * It provides common methods for updating translations, sizes, and views, which are inherited by all specific views.
 */
public abstract class AppView extends JPanel {
    /** The main application frame that holds this view. */
    protected MainFrame frame;
    /** Service responsible for handling translations in the application. */
    protected TranslationService translationService;
    /**
     * Constructs a new `AppView` object.
     *
     * @param frame the main application frame
     * @param translationService the service responsible for handling translations
     */
    protected AppView(MainFrame frame, TranslationService translationService) {
        this.frame = frame;
        this.translationService = translationService;
    }
    /**
     * Updates the translations of the current view.
     * This method is expected to be overridden by subclasses to handle translations of specific components.
     */
    public void updateTranslation() {
        frame.updateTranslation();
    }
    /**
     * Updates the size of the components in the current view.
     * This method is expected to be overridden by subclasses to handle resizing of specific components.
     */
    public void updateSize() {
        frame.updateSize();
    }
    /**
     * Updates the contents of the current view.
     * Subclasses can override this method to refresh or reload specific data or UI elements.
     */
    public void updateView() {
    }
}
