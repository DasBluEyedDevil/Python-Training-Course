package com.pythonlearning.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * SQLite database for tracking user progress through the course.
 * Stores lesson completion, quiz scores, and timestamps.
 */
public class ProgressDatabase {
    private static final Logger logger = LoggerFactory.getLogger(ProgressDatabase.class);
    private static final String DB_NAME = "python_learning_progress.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    private static ProgressDatabase instance;
    private Connection connection;

    /**
     * Private constructor for singleton pattern
     */
    private ProgressDatabase() {
        initializeDatabase();
    }

    /**
     * Get singleton instance
     */
    public static synchronized ProgressDatabase getInstance() {
        if (instance == null) {
            instance = new ProgressDatabase();
        }
        return instance;
    }

    /**
     * Initialize the database and create tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            // Create connection
            connection = DriverManager.getConnection(DB_URL);

            // Create tables
            String createLessonProgressTable = """
                CREATE TABLE IF NOT EXISTS lesson_progress (
                    module_id INTEGER NOT NULL,
                    lesson_id INTEGER NOT NULL,
                    completed BOOLEAN DEFAULT 0,
                    completed_at TIMESTAMP,
                    attempts INTEGER DEFAULT 0,
                    PRIMARY KEY (module_id, lesson_id)
                )
            """;

            String createQuizProgressTable = """
                CREATE TABLE IF NOT EXISTS quiz_progress (
                    module_id INTEGER PRIMARY KEY,
                    completed BOOLEAN DEFAULT 0,
                    score INTEGER DEFAULT 0,
                    total_questions INTEGER,
                    percentage INTEGER,
                    attempts INTEGER DEFAULT 0,
                    best_score INTEGER DEFAULT 0,
                    last_attempt TIMESTAMP
                )
            """;

            String createUserStatsTable = """
                CREATE TABLE IF NOT EXISTS user_stats (
                    id INTEGER PRIMARY KEY CHECK (id = 1),
                    total_lessons_completed INTEGER DEFAULT 0,
                    total_quizzes_completed INTEGER DEFAULT 0,
                    started_at TIMESTAMP,
                    last_activity TIMESTAMP
                )
            """;

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createLessonProgressTable);
                stmt.execute(createQuizProgressTable);
                stmt.execute(createUserStatsTable);

                // Initialize user stats if not exists
                String initStats = """
                    INSERT OR IGNORE INTO user_stats (id, started_at, last_activity)
                    VALUES (1, ?, ?)
                """;
                try (PreparedStatement pstmt = connection.prepareStatement(initStats)) {
                    Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                    pstmt.setTimestamp(1, now);
                    pstmt.setTimestamp(2, now);
                    pstmt.executeUpdate();
                }
            }

            logger.info("Database initialized successfully");

        } catch (SQLException e) {
            logger.error("Error initializing database: {}", e.getMessage(), e);
        }
    }

    /**
     * Mark a lesson as completed
     */
    public void markLessonComplete(int moduleId, int lessonId) {
        String sql = """
            INSERT INTO lesson_progress (module_id, lesson_id, completed, completed_at, attempts)
            VALUES (?, ?, 1, ?, 1)
            ON CONFLICT(module_id, lesson_id) DO UPDATE SET
                completed = 1,
                completed_at = ?,
                attempts = attempts + 1
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            pstmt.setInt(1, moduleId);
            pstmt.setInt(2, lessonId);
            pstmt.setTimestamp(3, now);
            pstmt.setTimestamp(4, now);
            pstmt.executeUpdate();

            updateUserStats();
            logger.info("Marked lesson complete: Module {} Lesson {}", moduleId, lessonId);

        } catch (SQLException e) {
            logger.error("Error marking lesson complete: {}", e.getMessage(), e);
        }
    }

    /**
     * Check if a lesson is completed
     */
    public boolean isLessonComplete(int moduleId, int lessonId) {
        String sql = "SELECT completed FROM lesson_progress WHERE module_id = ? AND lesson_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, moduleId);
            pstmt.setInt(2, lessonId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("completed");
            }
        } catch (SQLException e) {
            logger.error("Error checking lesson completion: {}", e.getMessage(), e);
        }

        return false;
    }

    /**
     * Record a quiz attempt
     */
    public void recordQuizAttempt(int moduleId, int score, int totalQuestions) {
        int percentage = (int) Math.round((double) score / totalQuestions * 100);
        boolean passed = percentage >= 70;

        String sql = """
            INSERT INTO quiz_progress (module_id, completed, score, total_questions, percentage, attempts, best_score, last_attempt)
            VALUES (?, ?, ?, ?, ?, 1, ?, ?)
            ON CONFLICT(module_id) DO UPDATE SET
                completed = CASE WHEN ? = 1 THEN 1 ELSE completed END,
                score = ?,
                percentage = ?,
                attempts = attempts + 1,
                best_score = MAX(best_score, ?),
                last_attempt = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            pstmt.setInt(1, moduleId);
            pstmt.setBoolean(2, passed);
            pstmt.setInt(3, score);
            pstmt.setInt(4, totalQuestions);
            pstmt.setInt(5, percentage);
            pstmt.setInt(6, percentage);
            pstmt.setTimestamp(7, now);
            pstmt.setBoolean(8, passed);
            pstmt.setInt(9, score);
            pstmt.setInt(10, percentage);
            pstmt.setInt(11, percentage);
            pstmt.setTimestamp(12, now);

            pstmt.executeUpdate();
            updateUserStats();

            logger.info("Recorded quiz attempt: Module {} - Score: {}/{} ({}%)",
                    moduleId, score, totalQuestions, percentage);

        } catch (SQLException e) {
            logger.error("Error recording quiz attempt: {}", e.getMessage(), e);
        }
    }

    /**
     * Get module progress (percentage of lessons completed)
     */
    public int getModuleProgress(int moduleId, int totalLessons) {
        String sql = "SELECT COUNT(*) as completed FROM lesson_progress WHERE module_id = ? AND completed = 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, moduleId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int completed = rs.getInt("completed");
                return (int) Math.round((double) completed / totalLessons * 100);
            }
        } catch (SQLException e) {
            logger.error("Error getting module progress: {}", e.getMessage(), e);
        }

        return 0;
    }

    /**
     * Get overall course progress
     */
    public int getOverallProgress(int totalLessons) {
        String sql = "SELECT COUNT(*) as completed FROM lesson_progress WHERE completed = 1";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int completed = rs.getInt("completed");
                return (int) Math.round((double) completed / totalLessons * 100);
            }
        } catch (SQLException e) {
            logger.error("Error getting overall progress: {}", e.getMessage(), e);
        }

        return 0;
    }

    /**
     * Get all progress data for display
     */
    public Map<String, Object> getAllProgressData() {
        Map<String, Object> data = new HashMap<>();

        try {
            // Get user stats
            String statsSql = "SELECT * FROM user_stats WHERE id = 1";
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(statsSql);
                if (rs.next()) {
                    data.put("totalLessonsCompleted", rs.getInt("total_lessons_completed"));
                    data.put("totalQuizzesCompleted", rs.getInt("total_quizzes_completed"));
                    data.put("startedAt", rs.getTimestamp("started_at"));
                    data.put("lastActivity", rs.getTimestamp("last_activity"));
                }
            }

            // Get lesson completion count
            String lessonSql = "SELECT COUNT(*) as count FROM lesson_progress WHERE completed = 1";
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(lessonSql);
                if (rs.next()) {
                    data.put("completedLessons", rs.getInt("count"));
                }
            }

            // Get quiz completion count
            String quizSql = "SELECT COUNT(*) as count FROM quiz_progress WHERE completed = 1";
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(quizSql);
                if (rs.next()) {
                    data.put("completedQuizzes", rs.getInt("count"));
                }
            }

        } catch (SQLException e) {
            logger.error("Error getting progress data: {}", e.getMessage(), e);
        }

        return data;
    }

    /**
     * Update user statistics
     */
    private void updateUserStats() {
        String sql = """
            UPDATE user_stats SET
                total_lessons_completed = (SELECT COUNT(*) FROM lesson_progress WHERE completed = 1),
                total_quizzes_completed = (SELECT COUNT(*) FROM quiz_progress WHERE completed = 1),
                last_activity = ?
            WHERE id = 1
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating user stats: {}", e.getMessage(), e);
        }
    }

    /**
     * Reset all progress (for testing or starting fresh)
     */
    public void resetProgress() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM lesson_progress");
            stmt.execute("DELETE FROM quiz_progress");
            stmt.execute("UPDATE user_stats SET total_lessons_completed = 0, total_quizzes_completed = 0");

            logger.info("Progress reset successfully");
        } catch (SQLException e) {
            logger.error("Error resetting progress: {}", e.getMessage(), e);
        }
    }

    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection: {}", e.getMessage(), e);
        }
    }
}
