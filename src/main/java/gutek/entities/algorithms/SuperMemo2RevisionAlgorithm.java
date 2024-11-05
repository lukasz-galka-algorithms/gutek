package gutek.entities.algorithms;

import gutek.entities.cards.CardSuperMemo2;
import gutek.utils.validation.Min;
import gutek.utils.validation.NotEmpty;
import gutek.utils.validation.NotNull;
import jakarta.persistence.*;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import java.time.LocalDate;

/**
 * A revision algorithm based on the SuperMemo2 method for spaced repetition.
 * This class defines both normal and reverse revision processes, using the SuperMemo2 algorithm
 * to calculate intervals and adjust the easiness factor based on user performance.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SuperMemo2RevisionAlgorithm extends RevisionAlgorithm<CardSuperMemo2> {

    /** Initial easiness factor for the normal revision process. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.supermemo2.easiness_factor")
    @NotNull
    @NotEmpty
    @Min(value = 1.3)
    private Double initialEasinessFactor;

    /** Threshold for incorrect answers in the normal revision process. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.supermemo2.incorrect_threshold")
    @NotNull
    @NotEmpty
    @Min(value = 1)
    private Integer incorrectAnswerThreshold;

    /** UI component representing grade button for the normal revision process. */
    @Transient
    private final Button buttonGrade1;

    /** UI component representing grade button for the normal revision process. */
    @Transient
    private final Button buttonGrade2;

    /** UI component representing grade button for the normal revision process. */
    @Transient
    private final Button buttonGrade3;

    /** UI component representing grade button for the normal revision process. */
    @Transient
    private final Button buttonGrade4;

    /** UI component representing grade button for the normal revision process. */
    @Transient
    private final Button buttonGrade5;

    /** Initial easiness factor for the reverse revision process. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.supermemo2.reverse_easiness_factor")
    @NotNull
    @NotEmpty
    @Min(value = 1.3)
    private Double reverseInitialEasinessFactor;

    /** Threshold for incorrect answers in the reverse revision process. */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.supermemo2.reverse_incorrect_threshold")
    @NotNull
    @NotEmpty
    @Min(value = 1)
    private Integer reverseIncorrectAnswerThreshold;

    /** UI component representing grade button for the reverse revision process. */
    @Transient
    private final Button reverseButtonGrade1;

    /** UI component representing grade button for the reverse revision process. */
    @Transient
    private final Button reverseButtonGrade2;

    /** UI component representing grade button for the reverse revision process. */
    @Transient
    private final Button reverseButtonGrade3;

    /** UI component representing grade button for the reverse revision process. */
    @Transient
    private final Button reverseButtonGrade4;

    /** UI component representing grade button for the reverse revision process. */
    @Transient
    private final Button reverseButtonGrade5;

    /** Translation key for the algorithm's name. */
    @Transient
    protected static final String algorithmNameKey = "revision_algorithm.supermemo2.algorithm_name";

    /**
     * Default constructor that initializes the buttons for normal and reverse revision.
     */
    public SuperMemo2RevisionAlgorithm() {
        super();
        this.buttonGrade1 = new Button();
        this.buttonGrade2 = new Button();
        this.buttonGrade3 = new Button();
        this.buttonGrade4 = new Button();
        this.buttonGrade5 = new Button();

        this.reverseButtonGrade1 = new Button();
        this.reverseButtonGrade2 = new Button();
        this.reverseButtonGrade3 = new Button();
        this.reverseButtonGrade4 = new Button();
        this.reverseButtonGrade5 = new Button();
    }

    /**
     * Initializes default values for the algorithm's hyperparameters.
     */
    public void initializeDefaultHiperparameters() {
        this.initialEasinessFactor = 2.5;
        this.incorrectAnswerThreshold = 3;

        this.reverseInitialEasinessFactor = 2.5;
        this.reverseIncorrectAnswerThreshold = 3;
    }

    /**
     * Updates the text for the UI components based on the current translations.
     */
    @Override
    public void updateTranslation() {
        buttonGrade1.setText(translationService.getTranslation("revision_algorithm.supermemo2.normal_button_1"));
        buttonGrade2.setText(translationService.getTranslation("revision_algorithm.supermemo2.normal_button_2"));
        buttonGrade3.setText(translationService.getTranslation("revision_algorithm.supermemo2.normal_button_3"));
        buttonGrade4.setText(translationService.getTranslation("revision_algorithm.supermemo2.normal_button_4"));
        buttonGrade5.setText(translationService.getTranslation("revision_algorithm.supermemo2.normal_button_5"));

        reverseButtonGrade1.setText(translationService.getTranslation("revision_algorithm.supermemo2.reverse_button_1"));
        reverseButtonGrade2.setText(translationService.getTranslation("revision_algorithm.supermemo2.reverse_button_2"));
        reverseButtonGrade3.setText(translationService.getTranslation("revision_algorithm.supermemo2.reverse_button_3"));
        reverseButtonGrade4.setText(translationService.getTranslation("revision_algorithm.supermemo2.reverse_button_4"));
        reverseButtonGrade5.setText(translationService.getTranslation("revision_algorithm.supermemo2.reverse_button_5"));
    }

    /**
     * Updates the size and style for the UI components.
     */
    @Override
    public void updateSize(double width, double height, double scaleFactor) {
        double buttonFontSize = 12 * scaleFactor;
        buttonGrade1.setStyle("-fx-font-size: " + buttonFontSize + "px;");
        buttonGrade2.setStyle("-fx-font-size: " + buttonFontSize + "px;");
        buttonGrade3.setStyle("-fx-font-size: " + buttonFontSize + "px;");
        buttonGrade4.setStyle("-fx-font-size: " + buttonFontSize + "px;");
        buttonGrade5.setStyle("-fx-font-size: " + buttonFontSize + "px;");

        reverseButtonGrade1.setStyle("-fx-font-size: " + buttonFontSize + "px;");
        reverseButtonGrade2.setStyle("-fx-font-size: " + buttonFontSize + "px;");
        reverseButtonGrade3.setStyle("-fx-font-size: " + buttonFontSize + "px;");
        reverseButtonGrade4.setStyle("-fx-font-size: " + buttonFontSize + "px;");
        reverseButtonGrade5.setStyle("-fx-font-size: " + buttonFontSize + "px;");

        buttonGrade1.setPrefSize(width / 5, height);
        buttonGrade2.setPrefSize(width / 5, height);
        buttonGrade3.setPrefSize(width / 5, height);
        buttonGrade4.setPrefSize(width / 5, height);
        buttonGrade5.setPrefSize(width / 5, height);

        reverseButtonGrade1.setPrefSize(width / 5, height);
        reverseButtonGrade2.setPrefSize(width / 5, height);
        reverseButtonGrade3.setPrefSize(width / 5, height);
        reverseButtonGrade4.setPrefSize(width / 5, height);
        reverseButtonGrade5.setPrefSize(width / 5, height);
    }

    /**
     * Returns a panel containing the buttons for the normal revision process.
     *
     * @param card the card being revised
     * @return a panel with the grade buttons for the normal revision
     */
    @Override
    public Pane getRevisionButtonsPane(CardSuperMemo2 card) {
        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(buttonGrade1, buttonGrade2, buttonGrade3, buttonGrade4, buttonGrade5);
        return buttonBox;
    }

    /**
     * Handles the logic for the normal revision process based on the clicked button.
     *
     * @param clickedButton the button that was clicked
     * @param card the card being revised
     * @return true if the revision process is complete, false otherwise
     */
    @Override
    public boolean reviseCard(Button clickedButton, CardSuperMemo2 card) {
        boolean cardRevisionFinished;
        int grade = 0;

        if (clickedButton == buttonGrade1) {
            grade = 1;
        } else if (clickedButton == buttonGrade2) {
            grade = 2;
        } else if (clickedButton == buttonGrade3) {
            grade = 3;
        } else if (clickedButton == buttonGrade4) {
            grade = 4;
        } else if (clickedButton == buttonGrade5) {
            grade = 5;
        }

        if (grade >= 3) {
            card.setRepetition(card.getRepetition() + 1);

            if (card.getRepetition() == 1) {
                card.setInterval(1);
            } else if (card.getRepetition() == 2) {
                card.setInterval(6);
            } else {
                double newInterval = card.getInterval() * card.getEasinessFactor();
                card.setInterval((int) Math.round(newInterval));
            }

            double newEasinessFactor = card.getEasinessFactor() + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02));
            card.setEasinessFactor(newEasinessFactor);

            card.setNextRegularRevisionDate(LocalDate.now().plusDays(card.getInterval()));
            cardRevisionFinished = true;
        } else {
            card.setIncorrectCounter(card.getIncorrectCounter() + 1);

            if(card.getIncorrectCounter() >= incorrectAnswerThreshold){
                card.setRevisionDefault(initialEasinessFactor);
            }

            card.setNextRegularRevisionDate(LocalDate.now().plusDays(1));
            cardRevisionFinished = true;
        }

        return cardRevisionFinished;
    }

    /**
     * Returns a panel containing the buttons for the reverse revision process.
     *
     * @param card the card being revised
     * @return a panel with the grade buttons for the reverse revision
     */
    @Override
    public Pane getReverseRevisionButtonsPane(CardSuperMemo2 card) {
        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(reverseButtonGrade1, reverseButtonGrade2, reverseButtonGrade3, reverseButtonGrade4, reverseButtonGrade5);
        return buttonBox;
    }

    /**
     * Handles the logic for the reverse revision process based on the clicked button.
     *
     * @param clickedButton the button that was clicked
     * @param card the card being revised
     * @return true if the reverse revision process is complete, false otherwise
     */
    @Override
    public boolean reverseReviseCard(Button clickedButton, CardSuperMemo2 card) {
        boolean cardRevisionFinished;
        int grade = 0;

        if (clickedButton == buttonGrade1) {
            grade = 1;
        } else if (clickedButton == buttonGrade2) {
            grade = 2;
        } else if (clickedButton == buttonGrade3) {
            grade = 3;
        } else if (clickedButton == buttonGrade4) {
            grade = 4;
        } else if (clickedButton == buttonGrade5) {
            grade = 5;
        }

        if (grade >= 3) {
            card.setReverseRepetition(card.getReverseRepetition() + 1);

            if (card.getReverseRepetition() == 1) {
                card.setReverseInterval(1);
            } else if (card.getReverseRepetition() == 2) {
                card.setReverseInterval(6);
            } else {
                double newInterval = card.getReverseInterval() * card.getReverseEasinessFactor();
                card.setReverseInterval((int) Math.round(newInterval));
            }

            double newEasinessFactor = card.getReverseEasinessFactor() + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02));
            card.setReverseEasinessFactor(newEasinessFactor);

            card.setNextReverseRevisionDate(LocalDate.now().plusDays(card.getReverseInterval()));
            cardRevisionFinished = true;
        } else {
            card.setReverseIncorrectCounter(card.getReverseIncorrectCounter() + 1);

            if(card.getReverseIncorrectCounter() >= reverseIncorrectAnswerThreshold){
                card.setReverseRevisionDefault(reverseInitialEasinessFactor);
            }

            card.setNextReverseRevisionDate(LocalDate.now().plusDays(1));
            cardRevisionFinished = true;
        }

        return cardRevisionFinished;
    }

    /**
     * Returns the name of the algorithm based on the current translation.
     *
     * @return the translated name of the algorithm
     */
    @Override
    public String getAlgorithmName() {
        return translationService.getTranslation(algorithmNameKey);
    }

    /**
     * Creates a new card for the SuperMemo2 algorithm with the given front and back content.
     *
     * @param front the front content of the card
     * @param back the back content of the card
     * @return a new {@link CardSuperMemo2} instance
     */
    @Override
    public CardSuperMemo2 createNewCard(String front, String back) {
        return new CardSuperMemo2(front, back, initialEasinessFactor, reverseInitialEasinessFactor, null);
    }
}