package com.pythonlearning.controller;

import com.pythonlearning.database.ProgressDatabase;
import com.pythonlearning.model.Lesson;
import com.pythonlearning.model.Module;
import com.pythonlearning.model.Quiz;
import com.pythonlearning.util.ContentLoader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main Window Controller for the Python Learning Platform.
 * Handles navigation, lesson display, and progress tracking.
 */
public class MainWindowController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainWindowController.class);

    @FXML private BorderPane mainContainer;
    @FXML private ListView<Module> moduleListView;
    @FXML private Label moduleTitle;
    @FXML private Label lessonTitle;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private VBox contentArea;
    @FXML private Button prevLessonBtn;
    @FXML private Button nextLessonBtn;
    @FXML private Button markCompleteBtn;

    private Module currentModule;
    private int currentLessonId = 1;
    private Lesson currentLesson;
    private ProgressDatabase database;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing Main Window Controller");

        database = ProgressDatabase.getInstance();

        setupModuleList();
        setupProgressDisplay();
        setupNavigationButtons();

        // Load first module and lesson by default
        loadModule(1);
        loadLesson(1, 1);
    }

    /**
     * Setup the module list view
     */
    private void setupModuleList() {
        List<Module> modules = ContentLoader.getAllModules();

        moduleListView.getItems().addAll(modules);

        // Custom cell factory to display module with icon and progress
        moduleListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Module module, boolean empty) {
                super.updateItem(module, empty);

                if (empty || module == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5);
                    box.setPadding(new Insets(5));

                    Label titleLabel = new Label(module.getIcon() + " Module " + module.getId() + ": " + module.getTitle());
                    titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

                    Label subtitleLabel = new Label(module.getSubtitle());
                    subtitleLabel.setFont(Font.font("System", 12));
                    subtitleLabel.setStyle("-fx-text-fill: #666;");

                    // Progress indicator
                    int progress = database.getModuleProgress(module.getId(), module.getLessons());
                    Label progressLabel = new Label(progress + "% Complete");
                    progressLabel.setFont(Font.font("System", 10));

                    if (progress == 100) {
                        progressLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if (progress > 0) {
                        progressLabel.setStyle("-fx-text-fill: orange;");
                    }

                    box.getChildren().addAll(titleLabel, subtitleLabel, progressLabel);
                    setGraphic(box);
                }
            }
        });

        // Handle module selection
        moduleListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loadModule(newValue.getId());
                        loadLesson(newValue.getId(), 1);
                    }
                }
        );
    }

    /**
     * Setup progress display
     */
    private void setupProgressDisplay() {
        updateProgressDisplay();
    }

    /**
     * Setup navigation buttons
     */
    private void setupNavigationButtons() {
        prevLessonBtn.setOnAction(event -> navigateToPreviousLesson());
        nextLessonBtn.setOnAction(event -> navigateToNextLesson());
        markCompleteBtn.setOnAction(event -> markCurrentLessonComplete());
    }

    /**
     * Load a specific module
     */
    private void loadModule(int moduleId) {
        currentModule = ContentLoader.getModule(moduleId);

        if (currentModule != null) {
            moduleTitle.setText(currentModule.getIcon() + " Module " + currentModule.getId() +
                    ": " + currentModule.getTitle());
            logger.info("Loaded module: {}", currentModule);
        } else {
            logger.warn("Module {} not found", moduleId);
        }
    }

    /**
     * Load a specific lesson
     */
    private void loadLesson(int moduleId, int lessonId) {
        currentLessonId = lessonId;
        currentLesson = ContentLoader.loadLesson(moduleId, lessonId);

        if (currentLesson != null) {
            displayLesson(currentLesson);
            updateNavigationButtons();
            updateProgressDisplay();
        } else {
            logger.warn("Lesson not found: Module {} Lesson {}", moduleId, lessonId);
            showError("Lesson not found", "This lesson has not been created yet.");
        }
    }

    /**
     * Display a lesson in the content area
     */
    private void displayLesson(Lesson lesson) {
        contentArea.getChildren().clear();
        lessonTitle.setText(lesson.getTitle());

        // Estimated time
        Label timeLabel = new Label("⏱️ " + lesson.getEstimatedTime());
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        contentArea.getChildren().add(timeLabel);

        // Add separator
        contentArea.getChildren().add(new Separator());

        // Concept section
        addSectionHeader("🧩 The Concept", "The Simplifier");
        WebView conceptView = createWebView(lesson.getConcept());
        contentArea.getChildren().add(conceptView);

        // Code example section
        if (lesson.getCodeExample() != null) {
            addSectionHeader("💻 Code Example", "The Coder");

            CodeArea codeArea = new CodeArea(lesson.getCodeExample().getCode());
            codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
            codeArea.setEditable(false);
            codeArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
            codeArea.setPrefHeight(300);

            VBox codeBox = new VBox(5, codeArea);
            codeBox.setPadding(new Insets(10));
            codeBox.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 5;");

            // Output
            if (lesson.getCodeExample().getOutput() != null && !lesson.getCodeExample().getOutput().isEmpty()) {
                Label outputLabel = new Label("Output:");
                outputLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

                TextArea outputArea = new TextArea(lesson.getCodeExample().getOutput());
                outputArea.setEditable(false);
                outputArea.setPrefHeight(100);
                outputArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

                codeBox.getChildren().addAll(outputLabel, outputArea);
            }

            contentArea.getChildren().add(codeBox);
        }

        // Syntax breakdown
        addSectionHeader("🔍 Syntax Breakdown", "The Simplifier");
        WebView breakdownView = createWebView(lesson.getSyntaxBreakdown());
        contentArea.getChildren().add(breakdownView);

        // Exercise section
        if (lesson.getExercise() != null) {
            addSectionHeader("✏️ Interactive Exercise", "The Coder");
            WebView exerciseView = createWebView(lesson.getExercise().getInstructions());
            contentArea.getChildren().add(exerciseView);

            CodeArea exerciseArea = new CodeArea(lesson.getExercise().getStarterCode());
            exerciseArea.setParagraphGraphicFactory(LineNumberFactory.get(exerciseArea));
            exerciseArea.setPrefHeight(300);

            VBox exerciseBox = new VBox(5, exerciseArea);
            exerciseBox.setPadding(new Insets(10));
            exerciseBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");

            Button showHintBtn = new Button("💡 Show Hint");
            Button showSolutionBtn = new Button("✅ Show Solution");

            HBox buttonBox = new HBox(10, showHintBtn, showSolutionBtn);
            buttonBox.setAlignment(Pos.CENTER_LEFT);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));

            exerciseBox.getChildren().add(buttonBox);
            contentArea.getChildren().add(exerciseBox);

            // Hint button handler
            showHintBtn.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Hint");
                alert.setHeaderText("Need some help?");
                alert.setContentText(stripHtml(lesson.getExercise().getHint()));
                alert.showAndWait();
            });

            // Solution button handler
            showSolutionBtn.setOnAction(e -> showSolutionDialog(lesson.getSolution()));
        }

        // Key takeaways
        addSectionHeader("🎯 Key Takeaways", null);
        WebView takeawaysView = createWebView(lesson.getKeyTakeaways());
        contentArea.getChildren().add(takeawaysView);
    }

    /**
     * Add a section header to the content area
     */
    private void addSectionHeader(String title, String agentBadge) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 0, 10, 0));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        header.getChildren().add(titleLabel);

        if (agentBadge != null) {
            Label badge = new Label(agentBadge);
            badge.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                    "-fx-padding: 3 8 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");
            header.getChildren().add(badge);
        }

        contentArea.getChildren().add(header);
    }

    /**
     * Create a WebView for HTML content
     */
    private WebView createWebView(String htmlContent) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        String styledHtml = "<html><head><style>" +
                "body { font-family: 'Segoe UI', Arial, sans-serif; font-size: 14px; line-height: 1.6; }" +
                "code { background-color: #f4f4f4; padding: 2px 6px; border-radius: 3px; font-family: 'Courier New', monospace; }" +
                "pre { background-color: #f4f4f4; padding: 10px; border-radius: 5px; overflow-x: auto; }" +
                "ul, ol { margin-left: 20px; }" +
                "</style></head><body>" + htmlContent + "</body></html>";

        engine.loadContent(styledHtml);
        webView.setPrefHeight(200);
        webView.setMaxHeight(Double.MAX_VALUE);

        return webView;
    }

    /**
     * Show solution dialog
     */
    private void showSolutionDialog(Lesson.Solution solution) {
        if (solution == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Solution");
        alert.setHeaderText("Here's the solution:");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        CodeArea solutionCode = new CodeArea(solution.getCode());
        solutionCode.setParagraphGraphicFactory(LineNumberFactory.get(solutionCode));
        solutionCode.setEditable(false);
        solutionCode.setPrefHeight(200);

        content.getChildren().add(new Label("Code:"));
        content.getChildren().add(solutionCode);
        content.getChildren().add(new Label("\nExplanation:"));
        content.getChildren().add(new Label(stripHtml(solution.getExplanation())));

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefSize(600, 500);
        alert.showAndWait();
    }

    /**
     * Strip HTML tags from content
     */
    private String stripHtml(String html) {
        return html != null ? html.replaceAll("<[^>]*>", "").trim() : "";
    }

    /**
     * Navigate to previous lesson
     */
    private void navigateToPreviousLesson() {
        if (currentModule == null) return;

        if (currentLessonId > 1) {
            loadLesson(currentModule.getId(), currentLessonId - 1);
        } else if (currentModule.getId() > 1) {
            Module prevModule = ContentLoader.getModule(currentModule.getId() - 1);
            if (prevModule != null) {
                loadModule(prevModule.getId());
                loadLesson(prevModule.getId(), prevModule.getLessons());
            }
        }
    }

    /**
     * Navigate to next lesson
     */
    private void navigateToNextLesson() {
        if (currentModule == null) return;

        if (currentLessonId < currentModule.getLessons()) {
            loadLesson(currentModule.getId(), currentLessonId + 1);
        } else if (currentModule.getId() < 14) {
            loadModule(currentModule.getId() + 1);
            loadLesson(currentModule.getId() + 1, 1);
        }
    }

    /**
     * Mark current lesson as complete
     */
    private void markCurrentLessonComplete() {
        if (currentModule == null || currentLesson == null) return;

        database.markLessonComplete(currentModule.getId(), currentLessonId);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lesson Complete");
        alert.setHeaderText("Great job!");
        alert.setContentText("Lesson marked as complete. Keep up the good work!");
        alert.showAndWait();

        updateProgressDisplay();
        moduleListView.refresh(); // Refresh to show updated progress
    }

    /**
     * Update navigation buttons based on current position
     */
    private void updateNavigationButtons() {
        boolean hasPrev = currentLessonId > 1 || (currentModule != null && currentModule.getId() > 1);
        boolean hasNext = (currentModule != null && currentLessonId < currentModule.getLessons()) ||
                (currentModule != null && currentModule.getId() < 14);

        prevLessonBtn.setDisable(!hasPrev);
        nextLessonBtn.setDisable(!hasNext);
    }

    /**
     * Update progress display
     */
    private void updateProgressDisplay() {
        int totalLessons = ContentLoader.getTotalLessons();
        int overallProgress = database.getOverallProgress(totalLessons);

        progressBar.setProgress(overallProgress / 100.0);
        progressLabel.setText(overallProgress + "% Complete");
    }

    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
