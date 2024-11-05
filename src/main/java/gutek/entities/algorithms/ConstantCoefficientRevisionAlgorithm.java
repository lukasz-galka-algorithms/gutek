package gutek.entities.algorithms;

import gutek.entities.cards.CardConstantCoefficient;
import gutek.utils.validation.Min;
import gutek.utils.validation.NotEmpty;
import gutek.utils.validation.NotNull;
import jakarta.persistence.*;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Represents a revision algorithm that uses constant coefficients to adjust
 * the revision time and track the number of incorrect answers.
 * This class is annotated as a JPA entity and uses the {@link InheritanceType#TABLE_PER_CLASS}
 * strategy for inheritance. It handles both normal and reverse revision processes.
 * The coefficients are used to calculate the next revision time based on the current
 * base revision time and the number of incorrect answers.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public class ConstantCoefficientRevisionAlgorithm extends RevisionAlgorithm<CardConstantCoefficient>{
    /** Coefficient used in the normal revision process. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_coeff_1")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double coefficient1;

    /** Coefficient used in the normal revision process. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_coeff_2")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double coefficient2;

    /** Coefficient used in the normal revision process. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_coeff_3")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double coefficient3;

    /** Coefficient used in the normal revision process. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_coeff_4")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double coefficient4;

    /** Threshold for the number of incorrect answers before resetting the card's revision status. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_incorrect")
    @NotNull
    @NotEmpty
    @Min(value = 1)
    private Integer incorrectAnswerThreshold;

    /** UI component for the first button in the normal revision process. */
    @Transient
    private final Button button1;

    /** UI component for the second button in the normal revision process. */
    @Transient
    private final Button button2;

    /** UI component for the third button in the normal revision process. */
    @Transient
    private final Button button3;

    /** UI component for the fourth button in the normal revision process. */
    @Transient
    private final Button button4;

    /** Coefficient used in the reverse revision process. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.reverse_coeff_1")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double reverseCoefficient1;

    /** Coefficient used in the reverse revision process. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.reverse_coeff_2")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double reverseCoefficient2;

    /** Threshold for the number of incorrect answers before resetting the card's reverse revision status. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.reverse_incorrect")
    @NotNull
    @NotEmpty
    @Min(value = 1)
    private Integer reverseIncorrectAnswerThreshold;

    /** UI component for the first button in the reverse revision process. */
    @Transient
    private final Button reverseButton1;

    /** UI component for the second button in the reverse revision process. */
    @Transient
    private final Button reverseButton2;

    /** Translation key for the algorithm name. */
    @Transient
    protected static final String ALGORITHM_NAME_KEY = "revision_algorithm.const_coeff.algorithm_name";

    /**
     * Default constructor that initializes the revision buttons.
     */
    public ConstantCoefficientRevisionAlgorithm() {
        super();
        this.button1 = new Button();
        this.button2 = new Button();
        this.button3 = new Button();
        this.button4 = new Button();

        this.reverseButton1 = new Button();
        this.reverseButton2 = new Button();
    }

    /**
     * Initializes the default values for the algorithm's hyperparameters.
     */
    public void initializeDefaultHiperparameters() {
        this.coefficient1 = 0.25;
        this.coefficient2 = 0.5;
        this.coefficient3 = 1.0;
        this.coefficient4 = 1.5;
        this.incorrectAnswerThreshold = 5;

        this.reverseCoefficient1 = 0.25;
        this.reverseCoefficient2 = 1.5;
        this.reverseIncorrectAnswerThreshold = 5;
    }

    /**
     * Updates the button text based on the current translations.
     */
    @Override
    public void updateTranslation() {
        button1.setText(translationService.getTranslation("revision_algorithm.const_coeff.normal_button_1"));
        button2.setText(translationService.getTranslation("revision_algorithm.const_coeff.normal_button_2"));
        button3.setText(translationService.getTranslation("revision_algorithm.const_coeff.normal_button_3"));
        button4.setText(translationService.getTranslation("revision_algorithm.const_coeff.normal_button_4"));
        reverseButton1.setText(translationService.getTranslation("revision_algorithm.const_coeff.reverse_button_1"));
        reverseButton2.setText(translationService.getTranslation("revision_algorithm.const_coeff.reverse_button_2"));
    }

    /**
     * Updates the button size and style.
     */
    @Override
    public void updateSize(double width, double height, double scaleFactor){
        double buttonFontSize = 12 * scaleFactor;
        String buttonsStyle = "-fx-font-size: " + buttonFontSize + "px;";

        button1.setStyle(buttonsStyle);
        button2.setStyle(buttonsStyle);
        button3.setStyle(buttonsStyle);
        button4.setStyle(buttonsStyle);

        reverseButton1.setStyle(buttonsStyle);
        reverseButton2.setStyle(buttonsStyle);

        button1.setPrefSize(width / 4, height);
        button2.setPrefSize(width / 4, height);
        button3.setPrefSize(width / 4, height);
        button4.setPrefSize(width / 4, height);

        reverseButton1.setPrefSize(width / 2, height);
        reverseButton2.setPrefSize(width / 2, height);
    }

    /**
     * Returns a panel containing the normal revision buttons.
     *
     * @param card the card being revised
     * @return a panel containing the revision buttons
     */
    @Override
    public Pane getRevisionButtonsPane(CardConstantCoefficient card) {
        HBox buttonBox = new HBox(4);
        buttonBox.getChildren().addAll(button1, button2, button3, button4);
        return buttonBox;
    }

    /**
     * Handles the revision logic when a button is clicked in the normal revision process.
     *
     * @param clickedButton the button that was clicked
     * @param card the card being revised
     * @return true if the revision process for the card is finished, false otherwise
     */
    @Override
    public boolean reviseCard(Button clickedButton, CardConstantCoefficient card) {
        boolean cardRevisionFinished = false;
        double baseRevisionTime = card.getBaseRevisionTime();
        int incorrectCounter = card.getIncorrectCounter();
        if (clickedButton == button1) {
            card.setBaseRevisionTime(coefficient1 * baseRevisionTime);
            card.setIncorrectCounter(incorrectCounter + 1);
            if(card.getIncorrectCounter() >= incorrectAnswerThreshold){
                card.setRevisionDefault();
            }
            card.setNextRegularRevisionDate(LocalDate.now());
        } else if (clickedButton == button2) {
            card.setBaseRevisionTime(coefficient2 * baseRevisionTime);
            LocalDate nextRevisionDate = LocalDate.now().plusDays((long) Math.max(card.getBaseRevisionTime(), 1));
            card.setNextRegularRevisionDate(nextRevisionDate);
            cardRevisionFinished = true;
        } else if (clickedButton == button3) {
            card.setBaseRevisionTime(coefficient3 * baseRevisionTime);
            LocalDate nextRevisionDate = LocalDate.now().plusDays((long) Math.max(card.getBaseRevisionTime(), 1));
            card.setNextRegularRevisionDate(nextRevisionDate);
            cardRevisionFinished = true;
        } else if (clickedButton == button4) {
            card.setBaseRevisionTime(coefficient4 * baseRevisionTime);
            LocalDate nextRevisionDate = LocalDate.now().plusDays((long) Math.max(card.getBaseRevisionTime(), 1));
            card.setNextRegularRevisionDate(nextRevisionDate);
            cardRevisionFinished = true;
        }
        return cardRevisionFinished;
    }

    /**
     * Returns a panel containing the reverse revision buttons.
     *
     * @param card the card being revised
     * @return a panel containing the reverse revision buttons
     */
    @Override
    public Pane getReverseRevisionButtonsPane(CardConstantCoefficient card) {
        HBox buttonBox = new HBox(2);
        buttonBox.getChildren().addAll(reverseButton1, reverseButton2);
        return buttonBox;
    }

    /**
     * Handles the revision logic when a button is clicked in the reverse revision process.
     *
     * @param clickedButton the button that was clicked
     * @param card the card being revised
     * @return true if the revision process for the card is finished, false otherwise
     */
    @Override
    public boolean reverseReviseCard(Button clickedButton, CardConstantCoefficient card) {
        boolean cardRevisionFinished = false;
        double baseReverseRevisionTime = card.getBaseReverseRevisionTime();
        int reverseIncorrectCounter = card.getReverseIncorrectCounter();
        if (clickedButton == reverseButton1) {
            card.setBaseReverseRevisionTime(reverseCoefficient1 * baseReverseRevisionTime);
            card.setReverseIncorrectCounter(reverseIncorrectCounter + 1);
            if(card.getReverseIncorrectCounter() >= reverseIncorrectAnswerThreshold){
                card.setReverseRevisionDefault();
            }
            card.setNextReverseRevisionDate(LocalDate.now());
        } else if (clickedButton == reverseButton2) {
            card.setBaseReverseRevisionTime(reverseCoefficient2 * baseReverseRevisionTime);
            LocalDate nextReverseRevisionDate = LocalDate.now().plusDays((long) Math.max(card.getBaseReverseRevisionTime(), 1));
            card.setNextReverseRevisionDate(nextReverseRevisionDate);
            cardRevisionFinished = true;
        }
        return cardRevisionFinished;
    }

    /**
     * Returns the name of the algorithm based on the current translation.
     *
     * @return the translated algorithm name
     */
    @Override
    public String getAlgorithmName() {
        return translationService.getTranslation(ALGORITHM_NAME_KEY);
    }

    /**
     * Creates a new card with the given front and back values.
     *
     * @param front the front text of the card
     * @param back the back text of the card
     * @return a new instance of {@link CardConstantCoefficient}
     */
    @Override
    public CardConstantCoefficient createNewCard(String front, String back) {
        return new CardConstantCoefficient(front, back, null);
    }
}
