package gutek.utils;

import javax.swing.*;
import javax.swing.text.*;

/**
 * A custom JTextField that allows only positive integers to be entered.
 * This class extends JTextField and uses a custom document to filter out non-numeric inputs.
 */
public class NumberTextField extends JTextField {
    /**
     * Creates a new NumberTextField that only accepts positive integer input.
     */
    public NumberTextField() {
        super();
        setDocument(new PositiveIntegerDocument());
    }
    /**
     * Custom document that only allows positive integers to be inserted into the text field.
     */
    private class PositiveIntegerDocument extends PlainDocument {
        /**
         * Inserts a string into the document if the string represents a positive integer.
         *
         * @param offset the position in the document to insert the content
         * @param str the string to insert
         * @param attr the attributes to associate with the inserted content
         * @throws BadLocationException if the insert position is invalid
         */
        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) {
                return;
            }

            if (isPositiveInteger(str)) {
                super.insertString(offset, str, attr);
            }
        }
        /**
         * Checks if the given string represents a positive integer.
         *
         * @param str the string to check
         * @return true if the string represents a positive integer, false otherwise
         */
        private boolean isPositiveInteger(String str) {
            try {
                int value = Integer.parseInt(str);
                return value >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
