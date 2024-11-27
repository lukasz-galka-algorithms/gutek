package gutek.gui.controllers.deck;

import gutek.domain.algorithms.AlgorithmHiperparameter;
import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestRevisionAlgorithm extends RevisionAlgorithm<CardBase> {
    @AlgorithmHiperparameter(descriptionTranslationKey = "param1.description")
    private String param1;

    @AlgorithmHiperparameter(descriptionTranslationKey = "param2.description")
    private int param2;

    private String nonAnnotatedField;

    @Override
    public String getAlgorithmName() {
        return "";
    }

    @Override
    public CardBase createNewCard(String front, String back) {
        return null;
    }

    @Override
    public void initializeDefaultHiperparameters() {
        // do nothing
    }

    @Override
    public void initializeGUI(double width, double height, double scaleFactor) {
        // do nothing
    }

    @Override
    public void updateSize(double width, double height, double scaleFactor) {
        // do nothing
    }

    @Override
    public void updateTranslation() {
        // do nothing
    }

    @Override
    public void initializeDefaultRevisionStrategies() {
        // do nothing
    }
}
