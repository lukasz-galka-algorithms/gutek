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

    /**
     * Returns the integer value from the text field, or zero if the field is empty.
     *
     * @return the integer value of the field's text, or zero if the field is empty
     */
    public int getInteger() {
        String text = getText();
        if (text.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(text);
    }
}