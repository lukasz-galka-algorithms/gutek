package gutek.domain.revisions;

import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.DeckService;
import javafx.scene.paint.Color;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * The {@code AvailableRevisions} class provides a static map of available revision modes, each associated
 * with a specific revision configuration such as a scene, color, and function to count revision cards.
 */
public class AvailableRevisions {
    /**
     * A map of available revision modes, where each mode is defined by a class and associated with a
     * {@link RevisionInfo} object containing configuration details.
     */
    @Getter
    private static final Map<Class<?>, RevisionInfo> AVAILABLE_REVISIONS = new LinkedHashMap<>();
    static {
        AVAILABLE_REVISIONS.put(RegularTextModeRevision.class,
                new RevisionInfo("regular_text_mode",
                        DeckService::getRegularRevisionCardsCount,
                        MainStageScenes.REVISION_REGULAR_SCENE,
                        Color.MAGENTA));

        AVAILABLE_REVISIONS.put(ReverseTextModeRevision.class,
                new RevisionInfo("reverse_text_mode",
                        DeckService::getReverseRevisionCardsCount,
                        MainStageScenes.REVISION_REVERSE_SCENE,
                        Color.OLIVE));
    }

    /**
     * Record {@code RevisionInfo} holds the configuration details for each revision mode.
     *
     * @param translationKey             The key used for translating the revision mode's name.
     * @param countRevisionCardsFunction A function that returns the count of revision cards for a specific deck.
     * @param scene                      The scene to display for the revision mode.
     * @param color                      The color associated with this revision mode.
     */
    public record RevisionInfo(String translationKey,
                               BiFunction<DeckService, DeckBase, Integer> countRevisionCardsFunction,
                               MainStageScenes scene,
                               Color color) {
    }
}
