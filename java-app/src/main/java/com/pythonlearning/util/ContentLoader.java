package com.pythonlearning.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pythonlearning.model.Lesson;
import com.pythonlearning.model.Module;
import com.pythonlearning.model.Quiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading lesson and quiz content from JSON files.
 * This class handles all content parsing and provides access to course materials.
 */
public class ContentLoader {
    private static final Logger logger = LoggerFactory.getLogger(ContentLoader.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Course structure matching app.py
    private static final List<Module> COURSE_MODULES = new ArrayList<>();

    static {
        // Initialize the course structure
        COURSE_MODULES.add(new Module(1, "The Absolute Basics", "The 'What'", 5, "🎯"));
        COURSE_MODULES.add(new Module(2, "Storing & Using Information", "The 'Boxes'", 5, "📦"));
        COURSE_MODULES.add(new Module(3, "Making Decisions", "The 'Forks in the Road'", 5, "🔀"));
        COURSE_MODULES.add(new Module(4, "Repeating Actions", "The 'Loops'", 5, "🔁"));
        COURSE_MODULES.add(new Module(5, "Grouping Information", "The 'Containers'", 6, "🗂️"));
        COURSE_MODULES.add(new Module(6, "Creating Reusable Tools", "The 'Recipes'", 5, "🧰"));
        COURSE_MODULES.add(new Module(7, "Handling Mistakes", "The 'Safety Nets'", 5, "🛡️"));
        COURSE_MODULES.add(new Module(8, "Blueprints for Code", "Object-Oriented Programming", 6, "🏗️"));
        COURSE_MODULES.add(new Module(9, "Working with the Real World", "Files & Libraries", 6, "🌍"));
        COURSE_MODULES.add(new Module(10, "Building for the Web", "Back-End", 6, "🌐"));
        COURSE_MODULES.add(new Module(11, "Storing Data", "Databases", 6, "💾"));
        COURSE_MODULES.add(new Module(12, "Building for the User", "Front-End Basics", 6, "🎨"));
        COURSE_MODULES.add(new Module(13, "Tying It All Together", "Full Stack", 6, "🚀"));
        COURSE_MODULES.add(new Module(14, "Sharing Your Work", "Deployment & Tools", 5, "📤"));
    }

    /**
     * Get the complete list of course modules
     */
    public static List<Module> getAllModules() {
        return new ArrayList<>(COURSE_MODULES);
    }

    /**
     * Get a specific module by ID
     */
    public static Module getModule(int moduleId) {
        if (moduleId < 1 || moduleId > COURSE_MODULES.size()) {
            return null;
        }
        return COURSE_MODULES.get(moduleId - 1);
    }

    /**
     * Load a lesson from JSON file
     *
     * @param moduleId Module number (1-14)
     * @param lessonId Lesson number (1-N)
     * @return Lesson object or null if not found
     */
    public static Lesson loadLesson(int moduleId, int lessonId) {
        String path = String.format("/content/modules/module_%02d/lesson_%02d.json", moduleId, lessonId);

        try (InputStream is = ContentLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                logger.warn("Lesson not found: {}", path);
                return null;
            }

            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Lesson lesson = gson.fromJson(reader, Lesson.class);

            logger.info("Loaded lesson: Module {} Lesson {} - {}", moduleId, lessonId, lesson.getTitle());
            return lesson;

        } catch (Exception e) {
            logger.error("Error loading lesson from {}: {}", path, e.getMessage());
            return null;
        }
    }

    /**
     * Load a quiz from JSON file
     *
     * @param moduleId Module number (1-14)
     * @return Quiz object or null if not found
     */
    public static Quiz loadQuiz(int moduleId) {
        String path = String.format("/content/quizzes/quiz_%02d.json", moduleId);

        try (InputStream is = ContentLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                logger.warn("Quiz not found: {}", path);
                return null;
            }

            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Quiz quiz = gson.fromJson(reader, Quiz.class);

            logger.info("Loaded quiz: Module {} - {}", moduleId, quiz.getTitle());
            return quiz;

        } catch (Exception e) {
            logger.error("Error loading quiz from {}: {}", path, e.getMessage());
            return null;
        }
    }

    /**
     * Check if a lesson exists
     */
    public static boolean lessonExists(int moduleId, int lessonId) {
        String path = String.format("/content/modules/module_%02d/lesson_%02d.json", moduleId, lessonId);
        return ContentLoader.class.getResource(path) != null;
    }

    /**
     * Check if a quiz exists
     */
    public static boolean quizExists(int moduleId) {
        String path = String.format("/content/quizzes/quiz_%02d.json", moduleId);
        return ContentLoader.class.getResource(path) != null;
    }

    /**
     * Get the total number of lessons across all modules
     */
    public static int getTotalLessons() {
        return COURSE_MODULES.stream()
                .mapToInt(Module::getLessons)
                .sum();
    }

    /**
     * Get lesson title without loading full content
     */
    public static String getLessonTitle(int moduleId, int lessonId) {
        Lesson lesson = loadLesson(moduleId, lessonId);
        return lesson != null ? lesson.getTitle() : "Unknown Lesson";
    }
}
