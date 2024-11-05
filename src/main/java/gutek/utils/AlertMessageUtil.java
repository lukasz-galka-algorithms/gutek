package gutek.utils;

import javafx.scene.control.Alert;

/**
 * Utility class for displaying alert dialogs in the application.
 * <p>
 * This class provides a simple interface for showing alerts with various alert types,
 * such as warnings, informational messages, and error messages.
 */
public class AlertMessageUtil {
    /**
     * Private constructor for hide the public one
     */
    private AlertMessageUtil(){}

    /**
     * Displays an alert dialog.
     */
    public static void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
