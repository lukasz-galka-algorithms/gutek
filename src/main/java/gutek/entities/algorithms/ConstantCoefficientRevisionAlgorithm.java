package gutek.entities.algorithms;

import gutek.domain.revisions.RegularTextModeRevision;
import gutek.domain.algorithms.AlgorithmHiperparameter;
import gutek.domain.revisions.ReverseTextModeRevision;
import gutek.entities.cards.CardConstantCoefficient;
import gutek.utils.ImageUtil;
import gutek.utils.validation.Min;
import gutek.utils.validation.NotEmpty;
import gutek.utils.validation.NotNull;
import jakarta.persistence.*;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * A revision algorithm based on the Constant Coefficients method for spaced repetition.
 * This class defines both normal and reverse revision processes, using the Constant Coefficients algorithm
 * to calculate intervals and adjust the easiness factor based on user performance.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public class ConstantCoefficientRevisionAlgorithm extends RevisionAlgorithm<CardConstantCoefficient>
        implements RegularTextModeRevision<CardConstantCoefficient>,
        ReverseTextModeRevision<CardConstantCoefficient> {
    /**
     * Coefficient used in the normal revision process.
     */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_coeff_1")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double coefficient1;

    /**
     * Coefficient used in the normal revision process.
     */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_coeff_2")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double coefficient2;

    /**
     * Coefficient used in the normal revision process.
     */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_coeff_3")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double coefficient3;

    /**
     * Coefficient used in the normal revision process.
     */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_coeff_4")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double coefficient4;

    /**
     * Threshold for the number of incorrect answers before resetting the card's revision status.
     */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.normal_incorrect")
    @NotNull
    @NotEmpty
    @Min(value = 1)
    private Integer incorrectAnswerThreshold;

    /**
     * UI component for the first button in the normal revision process.
     */
    @Transient
    private Button button1;

    /**
     * Icon for the "button1".
     */
    @Transient
    private ImageView button1Icon;

    /**
     * UI component for the second button in the normal revision process.
     */
    @Transient
    private Button button2;

    /**
     * Icon for the "button2".
     */
    @Transient
    private ImageView button2Icon;

    /**
     * UI component for the third button in the normal revision process.
     */
    @Transient
    private Button button3;

    /**
     * Icon for the "button3".
     */
    @Transient
    private ImageView button3Icon;

    /**
     * UI component for the fourth button in the normal revision process.
     */
    @Transient
    private Button button4;

    /**
     * Icon for the "button4".
     */
    @Transient
    private ImageView button4Icon;

    /**
     * Coefficient used in the reverse revision process.
     */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.reverse_coeff_1")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double reverseCoefficient1;

    /**
     * Coefficient used in the reverse revision process.
     */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.reverse_coeff_2")
    @NotNull
    @NotEmpty
    @Min(value = 0.001)
    private Double reverseCoefficient2;

    /**
     * Threshold for the number of incorrect answers before resetting the card's reverse revision status.
     */
    @AlgorithmHiperparameter(descriptionTranslationKey = "revision_algorithm.const_coeff.reverse_incorrect")
    @NotNull
    @NotEmpty
    @Min(value = 1)
    private Integer reverseIncorrectAnswerThreshold;

    /**
     * UI component for the first button in the reverse revision process.
     */
    @Transient
    private Button reverseButton1;

    /**
     * Icon for the "reverseButton1".
     */
    @Transient
    private ImageView reverseButton1Icon;

    /**
     * UI component for the second button in the reverse revision process.
     */
    @Transient
    private Button reverseButton2;

    /**
     * Icon for the "reverseButton2".
     */
    @Transient
    private ImageView reverseButton2Icon;

    /**
     * Translation key for the algorithm name.
     */
    @Transient
    protected static final String ALGORITHM_NAME_KEY = "revision_algorithm.const_coeff.algorithm_name";

    /**
     * Default constructor that initializes the revision buttons.
     */
    public ConstantCoefficientRevisionAlgorithm() {
        super();
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
     * Initializes the GUI components.
     */
    @Override
    public void initializeGUI() {
        this.button1 = new Button();
        this.button2 = new Button();
        this.button3 = new Button();
        this.button4 = new Button();

        this.reverseButton1 = new Button();
        this.reverseButton2 = new Button();

        initializeIcons();
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
    public void updateSize(double width, double height, double scaleFactor) {
        double buttonFontSize = 12 * scaleFactor;
        String buttonsStyle = "-fx-font-size: " + buttonFontSize + "px;";
        String buttonRadiusStyle = "-fx-background-radius: " + (20 * scaleFactor) + "; -fx-border-radius: " + (20 * scaleFactor) + ";";

        button1.setStyle(buttonsStyle + buttonRadiusStyle);
        button2.setStyle(buttonsStyle + buttonRadiusStyle);
        button3.setStyle(buttonsStyle + buttonRadiusStyle);
        button4.setStyle(buttonsStyle + buttonRadiusStyle);

        reverseButton1.setStyle(buttonsStyle + buttonRadiusStyle);
        reverseButton2.setStyle(buttonsStyle + buttonRadiusStyle);

        button1.setPrefSize(width / 4, height);
        button2.setPrefSize(width / 4, height);
        button3.setPrefSize(width / 4, height);
        button4.setPrefSize(width / 4, height);

        reverseButton1.setPrefSize(width / 2, height);
        reverseButton2.setPrefSize(width / 2, height);

        updateIcons(scaleFactor);
    }

    /**
     * Returns a panel containing the normal revision buttons.
     *
     * @param card the card being revised
     * @return a panel containing the revision buttons
     */
    @Override
    public Pane getRegularRevisionButtonsPane(CardConstantCoefficient card) {
        HBox buttonBox = new HBox(4);
        buttonBox.getChildren().addAll(button1, button2, button3, button4);
        return buttonBox;
    }

    /**
     * Handles the revision logic when a button is clicked in the normal revision process.
     *
     * @param clickedButton the button that was clicked
     * @param card          the card being revised
     * @return true if the revision process for the card is finished, false otherwise
     */
    @Override
    public boolean regularReviseCard(Button clickedButton, CardConstantCoefficient card) {
        boolean cardRevisionFinished = false;
        double baseRevisionTime = card.getBaseRevisionTime();
        int incorrectCounter = card.getIncorrectCounter();
        if (clickedButton == button1) {
            card.setBaseRevisionTime(coefficient1 * baseRevisionTime);
            card.setIncorrectCounter(incorrectCounter + 1);
            if (card.getIncorrectCounter() >= incorrectAnswerThreshold) {
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
     * @param card          the card being revised
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
            if (card.getReverseIncorrectCounter() >= reverseIncorrectAnswerThreshold) {
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
     * @param back  the back text of the card
     * @return a new instance of {@link CardConstantCoefficient}
     */
    @Override
    public CardConstantCoefficient createNewCard(String front, String back) {
        return new CardConstantCoefficient(front, back, null);
    }

    /**
     * Initializes the icons used in the controller's UI components.
     */
    private void initializeIcons() {
        button1Icon = ImageUtil.createImageView("/images/icons/repeat.png");
        button1.setGraphic(button1Icon);
        button2Icon = ImageUtil.createImageView("/images/icons/weak.png");
        button2.setGraphic(button2Icon);
        button3Icon = ImageUtil.createImageView("/images/icons/good.png");
        button3.setGraphic(button3Icon);
        button4Icon = ImageUtil.createImageView("/images/icons/excellent.png");
        button4.setGraphic(button4Icon);
        reverseButton1Icon = ImageUtil.createImageView("/images/icons/repeat.png");
        reverseButton1.setGraphic(reverseButton1Icon);
        reverseButton2Icon = ImageUtil.createImageView("/images/icons/excellent.png");
        reverseButton2.setGraphic(reverseButton2Icon);
    }

    /**
     * Updates the size of each icon according to the given scale factor.
     *
     * @param scaleFactor the scale factor used to adjust the size of each icon.
     */
    private void updateIcons(double scaleFactor) {
        ImageUtil.setImageViewSize(button1Icon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(button2Icon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(button3Icon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(button4Icon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(reverseButton1Icon, 20 * scaleFactor, 20 * scaleFactor);
        ImageUtil.setImageViewSize(reverseButton2Icon, 20 * scaleFactor, 20 * scaleFactor);
    }
}
