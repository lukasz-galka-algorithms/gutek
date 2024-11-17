package gutek.domain.revisions;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.DeckService;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Represents the regular text mode revision strategy.
 * <p>
 * This strategy focuses on revising cards in regular order,
 * using the word (front side) as the prompt.
 * </p>
 *
 * @param <T> the type of {@link CardBase} that this strategy applies to
 */
public class RegularTextModeRevisionStrategy<T extends CardBase> extends RevisionStrategy<T>{

    /**
     * Constructs a new {@code RegularTextModeRevisionStrategy} with the specified functions.
     *
     * @param getButtonsPaneFunction a function to generate a pane containing revision buttons
     * @param reviseCardFunction     a function to handle card revision logic
     */
    public RegularTextModeRevisionStrategy(Function<T, Pane> getButtonsPaneFunction, BiPredicate<Button, T> reviseCardFunction) {
        super(getButtonsPaneFunction, reviseCardFunction);
    }

    /**
     * Returns the translation key for the revision strategy.
     *
     * @return the translation key associated with the revision strategy
     */
    @Override
    public String getRevisionStrategyTranslationKey() {
        return "regular_text_mode";
    }

    /**
     * Returns the color associated with the revision strategy for UI representation.
     *
     * @return the {@link Color} associated with the revision strategy
     */
    @Override
    public Color getRevisionStrategyColor() {
        return Color.MAGENTA;
    }

    /**
     * Returns the scene to use for this revision strategy in the application.
     *
     * @return the {@link MainStageScenes} for this revision strategy
     */
    @Override
    public MainStageScenes getRevisionStrategyScene() {
        return MainStageScenes.REVISION_REGULAR_SCENE;
    }

    /**
     * Returns the number of cards available for this revision strategy.
     *
     * @param deckService the {@link DeckService} for accessing deck-related data
     * @param deckBase    the {@link DeckBase} to analyze
     * @return the count of cards available for revision
     */
    @Override
    public int getRevisionStrategyCardsCount(DeckService deckService, DeckBase deckBase) {
        return deckService.getRegularRevisionCardsCount(deckBase);
    }

    /**
     * Calculates the next revision date for a given card.
     *
     * @param card the {@link CardBase} to calculate the next revision date for
     * @return the {@link LocalDate} of the next revision
     */
    @Override
    public LocalDate getNextRevisionDate(T card) {
        return card.getNextRegularRevisionDate();
    }
}
