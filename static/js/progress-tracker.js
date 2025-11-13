/**
 * Python Learning Platform - Progress Tracker
 * Manages user progress using localStorage
 */

class ProgressTracker {
    constructor() {
        this.storageKey = 'python_learning_progress';
        this.progress = this.loadProgress();
    }

    /**
     * Load progress from localStorage
     */
    loadProgress() {
        const stored = localStorage.getItem(this.storageKey);

        if (stored) {
            return JSON.parse(stored);
        }

        // Initialize default progress structure
        return this.initializeProgress();
    }

    /**
     * Initialize default progress structure
     */
    initializeProgress() {
        const progress = {
            version: '1.0.0',
            lastUpdated: new Date().toISOString(),
            totalLessonsCompleted: 0,
            modules: {},
            quizzes: {},
            startedAt: new Date().toISOString()
        };

        // Initialize all modules
        const moduleLessons = [5, 5, 5, 5, 6, 5, 5, 6, 6, 6, 6, 6, 6, 5];

        for (let i = 1; i <= 14; i++) {
            progress.modules[i] = {
                completed: 0,
                total: moduleLessons[i - 1],
                lessons: {}
            };

            // Initialize all lessons in the module
            for (let j = 1; j <= moduleLessons[i - 1]; j++) {
                progress.modules[i].lessons[j] = {
                    completed: false,
                    completedAt: null,
                    attempts: 0
                };
            }

            // Initialize quiz
            progress.quizzes[i] = {
                completed: false,
                attempts: 0,
                bestScore: 0,
                lastAttempt: null
            };
        }

        this.saveProgress(progress);
        return progress;
    }

    /**
     * Save progress to localStorage
     */
    saveProgress(progress = this.progress) {
        progress.lastUpdated = new Date().toISOString();
        localStorage.setItem(this.storageKey, JSON.stringify(progress));
        this.progress = progress;

        // Trigger custom event for progress updates
        window.dispatchEvent(new CustomEvent('progressUpdated', {
            detail: { progress }
        }));
    }

    /**
     * Get complete progress data
     */
    getProgress() {
        return this.progress;
    }

    /**
     * Mark a lesson as complete
     */
    markLessonComplete(moduleId, lessonId) {
        if (!this.progress.modules[moduleId]) {
            console.error(`Module ${moduleId} not found`);
            return false;
        }

        if (!this.progress.modules[moduleId].lessons[lessonId]) {
            console.error(`Lesson ${lessonId} not found in Module ${moduleId}`);
            return false;
        }

        const lesson = this.progress.modules[moduleId].lessons[lessonId];

        // Only increment if not already completed
        if (!lesson.completed) {
            lesson.completed = true;
            lesson.completedAt = new Date().toISOString();
            this.progress.modules[moduleId].completed++;
            this.progress.totalLessonsCompleted++;
        }

        lesson.attempts++;

        this.saveProgress();
        return true;
    }

    /**
     * Mark a lesson as incomplete
     */
    markLessonIncomplete(moduleId, lessonId) {
        if (!this.progress.modules[moduleId]) {
            return false;
        }

        if (!this.progress.modules[moduleId].lessons[lessonId]) {
            return false;
        }

        const lesson = this.progress.modules[moduleId].lessons[lessonId];

        if (lesson.completed) {
            lesson.completed = false;
            lesson.completedAt = null;
            this.progress.modules[moduleId].completed--;
            this.progress.totalLessonsCompleted--;
        }

        this.saveProgress();
        return true;
    }

    /**
     * Check if a lesson is complete
     */
    isLessonComplete(moduleId, lessonId) {
        return this.progress.modules[moduleId]?.lessons[lessonId]?.completed || false;
    }

    /**
     * Get module progress
     */
    getModuleProgress(moduleId) {
        return this.progress.modules[moduleId] || { completed: 0, total: 0 };
    }

    /**
     * Check if a module is complete
     */
    isModuleComplete(moduleId) {
        const module = this.progress.modules[moduleId];
        return module && module.completed === module.total;
    }

    /**
     * Record quiz attempt
     */
    recordQuizAttempt(moduleId, score, totalQuestions) {
        if (!this.progress.quizzes[moduleId]) {
            return false;
        }

        const quiz = this.progress.quizzes[moduleId];
        const percentage = Math.round((score / totalQuestions) * 100);

        quiz.attempts++;
        quiz.lastAttempt = {
            date: new Date().toISOString(),
            score: score,
            total: totalQuestions,
            percentage: percentage
        };

        if (percentage > quiz.bestScore) {
            quiz.bestScore = percentage;
        }

        if (percentage >= 70) {
            quiz.completed = true;
        }

        this.saveProgress();
        return true;
    }

    /**
     * Get quiz progress
     */
    getQuizProgress(moduleId) {
        return this.progress.quizzes[moduleId] || null;
    }

    /**
     * Get overall completion percentage
     */
    getOverallPercentage() {
        const totalLessons = 73;
        return Math.round((this.progress.totalLessonsCompleted / totalLessons) * 100);
    }

    /**
     * Get learning statistics
     */
    getStatistics() {
        const stats = {
            totalLessons: 73,
            completedLessons: this.progress.totalLessonsCompleted,
            overallPercentage: this.getOverallPercentage(),
            modulesCompleted: 0,
            quizzesCompleted: 0,
            startedAt: this.progress.startedAt,
            lastUpdated: this.progress.lastUpdated,
            daysActive: 0
        };

        // Count completed modules
        for (let i = 1; i <= 14; i++) {
            if (this.isModuleComplete(i)) {
                stats.modulesCompleted++;
            }
        }

        // Count completed quizzes
        stats.quizzesCompleted = Object.values(this.progress.quizzes)
            .filter(quiz => quiz.completed).length;

        // Calculate days active
        const start = new Date(this.progress.startedAt);
        const now = new Date();
        stats.daysActive = Math.floor((now - start) / (1000 * 60 * 60 * 24));

        return stats;
    }

    /**
     * Reset all progress
     */
    resetProgress() {
        if (confirm('Are you sure you want to reset all progress? This cannot be undone.')) {
            this.progress = this.initializeProgress();
            this.saveProgress();
            window.location.reload();
        }
    }

    /**
     * Export progress data
     */
    exportProgress() {
        const dataStr = JSON.stringify(this.progress, null, 2);
        const blob = new Blob([dataStr], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `python-learning-progress-${new Date().toISOString().split('T')[0]}.json`;
        link.click();
        URL.revokeObjectURL(url);
    }

    /**
     * Import progress data
     */
    importProgress(file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            try {
                const imported = JSON.parse(e.target.result);
                if (imported.version && imported.modules) {
                    this.progress = imported;
                    this.saveProgress();
                    alert('Progress imported successfully!');
                    window.location.reload();
                } else {
                    alert('Invalid progress file format.');
                }
            } catch (error) {
                alert('Error importing progress file.');
                console.error('Import error:', error);
            }
        };
        reader.readAsText(file);
    }

    /**
     * Get next incomplete lesson
     */
    getNextLesson() {
        for (let moduleId = 1; moduleId <= 14; moduleId++) {
            const module = this.progress.modules[moduleId];
            for (let lessonId = 1; lessonId <= module.total; lessonId++) {
                if (!module.lessons[lessonId].completed) {
                    return { moduleId, lessonId };
                }
            }
        }
        return null; // All lessons completed!
    }

    /**
     * Get achievements/milestones
     */
    getAchievements() {
        const achievements = [];
        const stats = this.getStatistics();

        // First lesson
        if (stats.completedLessons >= 1) {
            achievements.push({
                title: 'First Steps',
                description: 'Completed your first lesson',
                icon: '🎯',
                unlocked: true
            });
        }

        // First module
        if (stats.modulesCompleted >= 1) {
            achievements.push({
                title: 'Module Master',
                description: 'Completed your first module',
                icon: '🏆',
                unlocked: true
            });
        }

        // Halfway there
        if (stats.overallPercentage >= 50) {
            achievements.push({
                title: 'Halfway There',
                description: 'Completed 50% of the course',
                icon: '⚡',
                unlocked: true
            });
        }

        // Course complete
        if (stats.overallPercentage === 100) {
            achievements.push({
                title: 'Full Stack Developer',
                description: 'Completed the entire course!',
                icon: '🚀',
                unlocked: true
            });
        }

        // Quiz master
        if (stats.quizzesCompleted >= 10) {
            achievements.push({
                title: 'Quiz Master',
                description: 'Passed 10 module quizzes',
                icon: '📝',
                unlocked: true
            });
        }

        // Perfect scorer
        const perfectQuizzes = Object.values(this.progress.quizzes)
            .filter(quiz => quiz.bestScore === 100).length;

        if (perfectQuizzes >= 5) {
            achievements.push({
                title: 'Perfect Scholar',
                description: 'Scored 100% on 5 quizzes',
                icon: '💯',
                unlocked: true
            });
        }

        return achievements;
    }
}

// Make ProgressTracker available globally
window.ProgressTracker = ProgressTracker;
