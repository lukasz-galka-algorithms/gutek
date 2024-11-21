package gutek.entities;

import gutek.domain.revisions.MockRevisionStrategy;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import lombok.Getter;


@Getter
public class ConcreteRevisionAlgorithm extends RevisionAlgorithm<CardBase> {

    public ConcreteRevisionAlgorithm() {
        super();
    }

    @Override
    public String getAlgorithmName() {
        return "Concrete Revision Algorithm";
    }

    @Override
    public CardBase createNewCard(String front, String back) {
        CardBase card = new CardBase();
        card.setFront(front);
        card.setBack(back);
        return card;
    }

    @Override
    public void initializeDefaultHiperparameters() {
        // Mock hiperparameters initialization
    }

    @Override
    public void initializeGUI(double width, double height, double scaleFactor) {
        // Mock GUI initialization
    }

    @Override
    public void updateSize(double width, double height, double scaleFactor) {
        // Mock update size logic
    }

    @Override
    public void updateTranslation() {
        translationService.getTranslation("test");
    }

    @Override
    public void initializeDefaultRevisionStrategies() {
        revisionStrategies.clear();
        revisionStrategies.add(new MockRevisionStrategy());
    }

}