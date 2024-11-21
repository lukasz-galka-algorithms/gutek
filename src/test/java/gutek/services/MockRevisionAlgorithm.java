package gutek.services;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;

public class MockRevisionAlgorithm extends RevisionAlgorithm<CardBase> {
    public MockRevisionAlgorithm() {
        super();
    }

    @Override
    public String getAlgorithmName() {
        return "MockAlgorithm";
    }

    @Override
    public CardBase createNewCard(String frontText, String backText) {
        return null;
    }

    @Override
    public void initializeDefaultHiperparameters() {
        // ignore
    }

    @Override
    public void initializeGUI(double width, double height, double scaleFactor) {
        // ignore
    }

    @Override
    public void updateSize(double width, double height, double scaleFactor) {
        // ignore
    }

    @Override
    public void updateTranslation() {
        // ignore
    }

    @Override
    public void initializeDefaultRevisionStrategies() {
        // ignore
    }
}
