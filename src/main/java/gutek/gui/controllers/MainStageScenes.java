package gutek.gui.controllers;

/**
 * Enum representing different JavaFX scenes in the main application stage.
 * Each enum constant corresponds to a specific scene that can be displayed.
 */
public enum MainStageScenes {

    /**
     * The scene for selecting a language.
     */
    LANGUAGE_SELECTION_SCENE,

    /**
     * The login scene where users can log in or register.
     */
    LOGIN_SCENE,

    /**
     * The scene displaying the list of decks for the logged-in user.
     */
    DECKS_SCENE,

    /**
     * The scene for creating a new deck.
     */
    NEW_DECK_SCENE,

    /**
     * The scene showing deleted decks (trash).
     */
    TRASH_SCENE,

    /**
     * The scene displaying information about the authors of the application.
     */
    AUTHORS_SCENE,

    /**
     * The scene corresponding to exiting the application.
     */
    EXIT,

    /**
     * The scene for adding a new card to a revision deck.
     */
    REVISION_ADD_NEW_CARD_SCENE,

    /**
     * The scene for searching through the cards in a deck.
     */
    REVISION_SEARCH_SCENE,

    /**
     * The scene for revising cards in a regular or reverse order.
     */
    REVISION_REVISE_SCENE,

    /**
     * The settings scene for configuring revision algorithms.
     */
    REVISION_SETTINGS_SCENE,

    /**
     * The scene for displaying statistics related to the revision of cards in a deck.
     */
    REVISION_STATISTICS_SCENE,

    /**
     * The scene for editing an existing card in a deck.
     */
    REVISION_EDIT_CARD_SCENE,

    /**
     * The scene for revising cards in regular order.
     */
    REVISION_REGULAR_SCENE,

    /**
     * The scene for revising cards in reverse order.
     */
    REVISION_REVERSE_SCENE
}