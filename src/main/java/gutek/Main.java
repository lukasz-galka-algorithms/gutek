package gutek;

import gutek.gui.MainFrame;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

import static gutek.gui.MainFrameViews.LANGUAGE_SELECTION_VIEW;

/**
 * The main entry point of the application.
 * It starts the Spring Boot application and launches the GUI.
 */
@SpringBootApplication
@AllArgsConstructor
public class Main implements CommandLineRunner {
    /**
     * The main frame of the application, which controls the user interface views.
     */
    private final MainFrame mainFrame;
    /**
     * The main method, serving as the entry point of the application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(Main.class, args);
    }
    /**
     * This method is called after the Spring Boot application has started.
     * It initializes the graphical user interface by displaying the language selection view.
     *
     * @param args command line arguments passed to the application
     */
    @Override
    public void run(String... args) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setView(LANGUAGE_SELECTION_VIEW);
        });
    }
}
