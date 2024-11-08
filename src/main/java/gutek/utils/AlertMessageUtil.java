package gutek.utils;

import gutek.gui.controllers.MainStage;
import gutek.services.TranslationService;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

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
     * Field to store translation key for the ok button
     */
    private static final String OK_BUTTON_TRANSLATION_KEY = "alert.button.ok";

    /**
     * Displays an informational alert dialog with translated text.
     *
     * @param translatedMessage the translated message to display in the alert
     * @param translationService the service for retrieving translations
     * @param mainStage the main stage of the application, used for scaling and positioning
     */
    public static void showInfoAlert(String translatedMessage, TranslationService translationService, MainStage mainStage) {
        Alert alert = createAlert(Alert.AlertType.INFORMATION, "alert.info.title", "alert.info.header", translatedMessage, translationService, mainStage);
        styleOKButton(alert, translationService.getTranslation(OK_BUTTON_TRANSLATION_KEY), "/images/icons/success.png", mainStage.getStageScaleFactor(), "green");
        alert.showAndWait();
    }

    /**
     * Displays a warning alert dialog with translated text.
     *
     * @param translatedMessage the translated message to display in the alert
     * @param translationService the service for retrieving translations
     * @param mainStage the main stage of the application, used for scaling and positioning
     */
    public static void showWarningAlert(String translatedMessage, TranslationService translationService, MainStage mainStage) {
        Alert alert = createAlert(Alert.AlertType.WARNING, "alert.warning.title", "alert.warning.header", translatedMessage, translationService, mainStage);
        styleOKButton(alert, translationService.getTranslation(OK_BUTTON_TRANSLATION_KEY), "/images/icons/warning.png", mainStage.getStageScaleFactor(), "orange");
        alert.showAndWait();
    }

    /**
     * Displays an error alert dialog with translated text.
     *
     * @param translatedMessage the translated message to display in the alert
     * @param translationService the service for retrieving translations
     * @param mainStage the main stage of the application, used for scaling and positioning
     */
    public static void showErrorAlert(String translatedMessage, TranslationService translationService, MainStage mainStage) {
        Alert alert = createAlert(Alert.AlertType.ERROR, "alert.error.title", "alert.error.header", translatedMessage, translationService, mainStage);
        styleOKButton(alert, translationService.getTranslation(OK_BUTTON_TRANSLATION_KEY), "/images/icons/error.png", mainStage.getStageScaleFactor(), "red");
        alert.showAndWait();
    }

    /**
     * Creates and configures an Alert dialog with a given type, title, and content.
     *
     * @param alertType the type of alert (e.g., INFORMATION, WARNING, ERROR)
     * @param titleKey the translation key for the alert title
     * @param headerKey the translation key for the alert header
     * @param contentText the main message content for the alert
     * @param translationService the service for retrieving translations
     * @param mainStage the main stage of the application, used for scaling and positioning
     * @return a configured Alert dialog ready to be displayed
     */
    private static Alert createAlert(Alert.AlertType alertType, String titleKey, String headerKey, String contentText, TranslationService translationService, MainStage mainStage) {
        Alert alert = new Alert(alertType);
        alert.setTitle(translationService.getTranslation(titleKey));
        alert.setHeaderText(translationService.getTranslation(headerKey));
        alert.setContentText(contentText);

        styleDialogText(alert, mainStage.getStageScaleFactor());
        setDialogSizeAndPosition(alert, mainStage);

        return alert;
    }

    /**
     * Sets the size and center position of the dialog based on the main stage size and scale factor.
     *
     * @param alert the Alert dialog to be sized and positioned
     * @param mainStage the main stage of the application, used for scaling and positioning
     */
    private static void setDialogSizeAndPosition(Alert alert, MainStage mainStage) {
        double scaleFactor = mainStage.getStageScaleFactor();
        double minDialogWidth = 350;
        double minDialogHeight = 200;
        double dialogWidth = Math.max(400 * scaleFactor, minDialogWidth);
        double dialogHeight = Math.max(250 * scaleFactor, minDialogHeight);

        alert.setWidth(dialogWidth);
        alert.setHeight(dialogHeight);

        Platform.runLater(() -> {
            alert.setX(mainStage.getStage().getX() + (mainStage.getStage().getWidth() - dialogWidth) / 2);
            alert.setY(mainStage.getStage().getY() + (mainStage.getStage().getHeight() - dialogHeight) / 2);
        });
    }

    /**
     * Styles the text size for the header and content areas of the dialog.
     *
     * @param alert the Alert dialog whose text will be styled
     * @param scaleFactor the scale factor to adjust the text size
     */
    private static void styleDialogText(Alert alert, double scaleFactor) {
        String fontSizeTemplate = "-fx-font-size: {size}px;";

        String fontSizeStyle = fontSizeTemplate.replace("{size}", String.valueOf(12 * scaleFactor));
        String fontSizeLargeStyle = fontSizeTemplate.replace("{size}", String.valueOf(14 * scaleFactor));

        Node headerLabel = alert.getDialogPane().lookup(".header-panel .label");
        if (headerLabel instanceof Label label) {
            label.setStyle(fontSizeLargeStyle);
        }
        Node contentNode = alert.getDialogPane().lookup(".content");
        if (contentNode != null) {
            contentNode.setStyle(fontSizeStyle);
        }
        alert.getDialogPane().setStyle(fontSizeStyle);
    }

    /**
     * Styles the OK button with specified text, icon, and color.
     *
     * @param alert the Alert dialog containing the OK button
     * @param buttonText the translated text for the OK button
     * @param iconPath the path to the icon image for the OK button
     * @param scaleFactor the scale factor to adjust the button's size and font
     * @param backgroundColor the background color for the button
     */
    private static void styleOKButton(Alert alert, String buttonText, String iconPath, double scaleFactor, String backgroundColor) {
        String fontSizeStyle = "-fx-font-size: " + (12 * scaleFactor) + "px;";
        String radiusStyle = "-fx-background-radius: " + (20 * scaleFactor) + "; -fx-border-radius: " + (20 * scaleFactor) + ";";
        String colorStyle = "-fx-background-color: " + backgroundColor + "; -fx-text-fill: white;";

        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText(buttonText);
        okButton.setStyle(fontSizeStyle + radiusStyle + colorStyle);
        okButton.setPrefSize(200 * scaleFactor, 40 * scaleFactor);

        ImageView buttonIcon = ImageUtil.createImageView(iconPath);
        ImageUtil.setImageViewSize(buttonIcon, 20 * scaleFactor, 20 * scaleFactor);
        okButton.setGraphic(buttonIcon);
    }
}
