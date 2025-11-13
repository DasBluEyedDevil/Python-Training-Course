package com.pythonlearning.app;

import com.pythonlearning.database.ProgressDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main JavaFX Application for Python Learning Platform
 *
 * A self-contained desktop application that delivers comprehensive Python
 * education from absolute beginner to full-stack developer.
 *
 * Philosophy: "Concept First, Jargon Last"
 * Built with JavaFX 21 LTS for Java 21 LTS
 */
public class PythonLearningApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(PythonLearningApp.class);

    private static final String APP_TITLE = "Python Learning Platform - Zero to Full-Stack";
    private static final int WINDOW_WIDTH = 1400;
    private static final int WINDOW_HEIGHT = 900;

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting Python Learning Platform...");

            // Load main window FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
            Parent root = loader.load();

            // Create scene with stylesheet
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

            // Configure primary stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);

            // Set application icon (Python logo)
            try {
                Image icon = new Image(getClass().getResourceAsStream("/images/python-icon.png"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                logger.warn("Could not load application icon: {}", e.getMessage());
            }

            // Set minimum window size
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);

            // Center on screen
            primaryStage.centerOnScreen();

            // Handle window close event
            primaryStage.setOnCloseRequest(event -> {
                logger.info("Application closing...");
                shutdown();
            });

            // Show the window
            primaryStage.show();

            logger.info("Application started successfully");

        } catch (Exception e) {
            logger.error("Error starting application: {}", e.getMessage(), e);
            showErrorAndExit("Failed to start application", e.getMessage());
        }
    }

    @Override
    public void stop() {
        logger.info("Application stop requested");
        shutdown();
    }

    /**
     * Cleanup resources before application shutdown
     */
    private void shutdown() {
        try {
            // Close database connection
            ProgressDatabase.getInstance().close();
            logger.info("Resources cleaned up successfully");
        } catch (Exception e) {
            logger.error("Error during shutdown: {}", e.getMessage(), e);
        }
    }

    /**
     * Show error dialog and exit
     */
    private void showErrorAndExit(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        System.exit(1);
    }

    /**
     * Application entry point
     */
    public static void main(String[] args) {
        logger.info("==========================================");
        logger.info("Python Learning Platform - Desktop Edition");
        logger.info("From Zero to Full-Stack Developer");
        logger.info("Built with JavaFX 21 LTS | Java 21 LTS");
        logger.info("==========================================");

        // Initialize database on startup
        try {
            ProgressDatabase.getInstance();
            logger.info("Database initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize database: {}", e.getMessage(), e);
        }

        // Launch JavaFX application
        launch(args);
    }
}
