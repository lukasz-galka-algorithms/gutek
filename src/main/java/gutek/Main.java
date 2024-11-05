package gutek;

import gutek.gui.controllers.MainStageScenes;
import gutek.gui.controllers.MainStage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main class of the application.
 * It starts the Spring Boot application and launches the JavaFX GUI.
 */
@SpringBootApplication
public class Main extends Application{
    /**
     * Spring application context for managing the lifecycle and dependencies of beans within the application.
     */
    private ConfigurableApplicationContext applicationContext;

    /**
     * The main method, serving as the entry point of the application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    /**
     * Initialization method.
     * Called before the start() method.
     */
    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(Main.class).run();
    }

    /**
     * The start method, called after the application has been initialized.
     * Sets up the main stage and displays the user interface.
     *
     * @param stage the main window of the application (Stage)
     */
    @Override
    public void start(Stage stage) {
        MainStage mainStage = applicationContext.getBean(MainStage.class);
        mainStage.initStage(stage);
        mainStage.setScene(MainStageScenes.LANGUAGE_SELECTION_SCENE);
        stage.show();
    }

    /**
     * The stop method, called when the application is closing.
     * Closes the Spring Boot application context.
     *
     */
    @Override
    public void stop() {
        if (applicationContext != null) {
            SpringApplication.exit(applicationContext);
        }
        Platform.exit();
    }
}
