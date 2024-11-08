package gutek.utils;

import gutek.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Utility class for loading FXML files in the application.
 * <p>
 * This class integrates Spring's {@link ApplicationContext} with JavaFX's {@link FXMLLoader} to
 * allow dependency injection into FXML controllers.
 * </p>
 */
@Component
@AllArgsConstructor
public class FXMLFileLoader {

    /**
     * Spring application context for retrieving Spring-managed beans.
     */
    private final ApplicationContext context;

    /**
     * Loads an FXML file and returns the root element of the resulting UI structure.
     * <p>
     * This method uses the {@link FXMLLoader} with a controller factory set to retrieve
     * Spring-managed beans for dependency injection into FXML controllers.
     * If a custom controller is provided, it is set directly; otherwise, the default
     * Spring context is used to inject dependencies.
     * </p>
     *
     * @param fxmlPath the path to the FXML file (relative to the resources directory, including the ".fxml" extension)
     * @param controller the custom controller to set for the FXML, or {@code null} to use the default controller factory
     * @return the loaded {@link Parent} element representing the UI root, or {@code null} if loading fails
     */
    public Parent loadFXML(String fxmlPath, Object controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlPath));
        if (controller == null) {
            fxmlLoader.setControllerFactory(context::getBean);
        } else {
            fxmlLoader.setController(controller);
        }

        try {
            return fxmlLoader.load();
        } catch (IOException ignored) {
            //ignore
        }
        return null;
    }
}
