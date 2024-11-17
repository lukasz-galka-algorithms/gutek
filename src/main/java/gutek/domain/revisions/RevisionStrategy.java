package gutek.domain.revisions;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.DeckService;
import gutek.services.DeckStatisticsService;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Represents an abstract revision strategy for handling card revisions in a spaced repetition system.
 * <p>
 * This class defines the structure and behavior common to all revision strategies,
 * such as managing buttons for card revisions, tracking revision statistics,
 * and determining the next revision date.
 * </p>
 *
 * @param <T> the type of {@link CardBase} that this strategy applies to
 */
public abstract class RevisionStrategy<T extends CardBase> {

    /**
     * Returns the translation key for the revision strategy.
     *
     * @return the translation key associated with the revision strategy
     */
    public abstract String getRevisionStrategyTranslationKey();

    /**
     * Returns the color associated with the revision strategy for UI representation.
     *
     * @return the {@link Color} associated with the revision strategy
     */
    public abstract Color getRevisionStrategyColor();

    /**
     * Returns the scene to use for this revision strategy in the application.
     *
     * @return the {@link MainStageScenes} for this revision strategy
     */
    public abstract MainStageScenes getRevisionStrategyScene();

    /**
     * Returns the number of cards available for this revision strategy.
     *
     * @param deckService the {@link DeckService} for accessing deck-related data
     * @param deckBase    the {@link DeckBase} to analyze
     * @return the count of cards available for revision
     */
    public abstract int getRevisionStrategyCardsCount(DeckService deckService, DeckBase deckBase);

    /**
     * Calculates the next revision date for a given card.
     *
     * @param card the {@link CardBase} to calculate the next revision date for
     * @return the {@link LocalDate} of the next revision
     */
    public abstract LocalDate getNextRevisionDate(T card);

    /**
     * Retrieves the revision counts for this strategy from the deck statistics.
     *
     * @param deckStatisticsService the service for accessing deck statistics
     * @param deck                  the deck associated with this strategy
     * @return an array of revision counts for this strategy
     */
    public int[] getRevisionCounts(DeckStatisticsService deckStatisticsService, DeckBase deck){
        int strategyIndex = deck.getRevisionAlgorithm().getRevisionStrategies().indexOf(this);
        return deckStatisticsService.getRevisionCounts(deck.getDeckBaseStatistics().getIdDeckStatistics(), strategyIndex);
    }

    /**
     * A function that generates a {@link Pane} containing buttons for card revision.
     * <p>
     * This function is responsible for creating the user interface components
     * that allow the user to interact with the cards during the revision process.
     * </p>
     */
    private final Function<T, Pane> getButtonsPaneFunction;

    /**
     * A predicate function that performs the card revision and determines whether the revision is complete
     * based on the button clicked during the revision process.
     * <p>
     * The predicate takes a {@link Button} and a {@link CardBase} as inputs, performs the revision logic for the card,
     * and returns {@code true} if the revision for the given card is considered finished. This function encapsulates
     * both the action of revising the card and the evaluation of whether the revision session for the card should end.
     * </p>
     */
    private final BiPredicate<Button, T> reviseCardFunction;

    /**
     * Constructs a new {@code RevisionStrategy} with the specified functions.
     *
     * @param getButtonsPaneFunction a function to generate a pane containing revision buttons
     * @param reviseCardFunction     a function to handle card revision logic
     */
    protected RevisionStrategy(Function<T, Pane> getButtonsPaneFunction, BiPredicate<Button, T> reviseCardFunction) {
        this.getButtonsPaneFunction = getButtonsPaneFunction;
        this.reviseCardFunction = reviseCardFunction;
    }

    /**
     * Generates a pane containing the buttons for this revision strategy.
     *
     * @param card the {@link CardBase} for which to create the buttons
     * @return a {@link Pane} containing the revision buttons
     */
    public Pane getRevisionButtonsPane(T card) {
        return getButtonsPaneFunction.apply(card);
    }

    /**
     * Handles the revision of a card based on a clicked button.
     *
     * @param clickedButton the button that was clicked
     * @param card          the {@link CardBase} being revised
     * @return {@code true} if the card revision is complete; {@code false} otherwise
     */
    public boolean reviseCard(Button clickedButton, T card) {
        return reviseCardFunction.test(clickedButton, card);
    }
}
