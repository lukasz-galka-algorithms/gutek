package gutek.gui.controls;

import javafx.scene.control.TextField;

/**
 * A custom TextField that allows only positive integers to be entered.
 */
public class NumberTextField extends TextField {

    /**
     * Constructs a new `NumberTextField` with a listener that restricts input to digits only.
     */
    public NumberTextField() {
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
}