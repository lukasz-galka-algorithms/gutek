package gutek.domain.revisions;

import gutek.entities.cards.CardBase;
import gutek.entities.decks.DeckBase;
import gutek.gui.controllers.MainStageScenes;
import gutek.services.DeckService;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.time.LocalDate;

public class MockRevisionStrategy extends RevisionStrategy<CardBase> {
    public MockRevisionStrategy() {
        super(card -> new Pane(), (button, card) -> true);
    }

    @Override
    public String getRevisionStrategyTranslationKey() {
        return "mock.revision.strategy";
    }

    @Override
    public Color getRevisionStrategyColor() {
        return Color.BLUE;
    }

    @Override
    public MainStageScenes getRevisionStrategyScene() {
        return MainStageScenes.DECKS_SCENE;
    }

    @Override
    public int getRevisionStrategyCardsCount(DeckService deckService, DeckBase deckBase) {
        return 42;
    }

    @Override
    public LocalDate getNextRevisionDate(CardBase card) {
        return LocalDate.now().plusDays(1);
    }
}
