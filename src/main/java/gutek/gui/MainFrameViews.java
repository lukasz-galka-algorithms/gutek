package gutek.gui;

/**
 * Enum representing different views in the main application frame.
 * Each enum constant corresponds to a specific view that can be displayed.
 */
public enum MainFrameViews {

    /**
     * The view for selecting a language.
     */
    LANGUAGE_SELECTION_VIEW,

    /**
     * The login view where users can log in or register.
     */
    LOGIN_VIEW,

    /**
     * The view displaying the list of decks for the logged-in user.
     */
    DECKS_VIEW,

    /**
     * The view for creating a new deck.
     */
    NEW_DECK_VIEW,

    /**
     * The view showing deleted decks (trash).
     */
    TRASH_VIEW,

    /**
     * The view displaying information about the authors of the application.
     */
    AUTHORS_VIEW,

    /**
     * The view corresponding to exiting the application.
     */
    EXIT,

    /**
     * The view for adding a new card to a revision deck.
     */
    REVISION_ADD_NEW_CARD_VIEW,

    /**
     * The view for searching through the cards in a deck.
     */
    REVISION_SEARCH_VIEW,

    /**
     * The view for revising cards in a regular or reverse order.
     */
    REVISION_REVISE_VIEW,

    /**
     * The settings view for configuring revision algorithms.
     */
    REVISION_SETTINGS_VIEW,

    /**
     * The view for displaying statistics related to the revision of cards in a deck.
     */
    REVISION_STATISTICS_VIEW,

    /**
     * The view for editing an existing card in a deck.
     */
    REVISION_EDIT_CARD_VIES,

    /**
     * The view for revising cards in regular order.
     */
    REVISION_REGULAR_VIEW,

    /**
     * The view for revising cards in reverse order.
     */
    REVISION_REVERSE_VIEW
}