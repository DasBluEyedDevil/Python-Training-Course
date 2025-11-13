/**
 * Python Learning Platform - Quiz Engine
 * Handles quiz functionality, scoring, and feedback
 */

class QuizEngine {
    constructor(quizData, moduleId) {
        this.quizData = quizData;
        this.moduleId = moduleId;
        this.currentQuestion = 0;
        this.answers = {};
        this.score = 0;
        this.quizSubmitted = false;

        this.init();
    }

    init() {
        this.setupNavigation();
        this.setupSubmit();
        this.setupReview();
        this.updateProgress();
    }

    /**
     * Setup question navigation
     */
    setupNavigation() {
        // Next button handlers
        document.querySelectorAll('.next-question').forEach(button => {
            button.addEventListener('click', (e) => {
                const questionNum = parseInt(e.target.getAttribute('data-question'));
                this.nextQuestion(questionNum);
            });
        });

        // Previous button handlers
        document.querySelectorAll('.prev-question').forEach(button => {
            button.addEventListener('click', (e) => {
                const questionNum = parseInt(e.target.getAttribute('data-question'));
                this.previousQuestion(questionNum);
            });
        });

        // Track answer selections
        document.querySelectorAll('.option-input').forEach(input => {
            input.addEventListener('change', (e) => {
                const questionCard = e.target.closest('.question-card');
                const questionId = questionCard.id.replace('question-', '');
                this.answers[questionId] = e.target.value;
            });
        });
    }

    /**
     * Setup quiz submission
     */
    setupSubmit() {
        const submitButton = document.getElementById('submit-quiz');
        if (submitButton) {
            submitButton.addEventListener('click', () => {
                this.submitQuiz();
            });
        }
    }

    /**
     * Setup review functionality
     */
    setupReview() {
        const reviewButton = document.getElementById('review-answers');
        const retakeButton = document.getElementById('retake-quiz');

        if (reviewButton) {
            reviewButton.addEventListener('click', () => {
                this.reviewAnswers();
            });
        }

        if (retakeButton) {
            retakeButton.addEventListener('click', () => {
                this.retakeQuiz();
            });
        }
    }

    /**
     * Navigate to next question
     */
    nextQuestion(currentQuestionNum) {
        const currentCard = document.getElementById(`question-${currentQuestionNum}`);
        const nextCard = document.getElementById(`question-${currentQuestionNum + 1}`);

        if (currentCard && nextCard) {
            currentCard.style.display = 'none';
            nextCard.style.display = 'block';
            this.currentQuestion = currentQuestionNum;
            this.updateProgress();
            this.scrollToTop();
        }
    }

    /**
     * Navigate to previous question
     */
    previousQuestion(currentQuestionNum) {
        const currentCard = document.getElementById(`question-${currentQuestionNum}`);
        const prevCard = document.getElementById(`question-${currentQuestionNum - 1}`);

        if (currentCard && prevCard) {
            currentCard.style.display = 'none';
            prevCard.style.display = 'block';
            this.currentQuestion = currentQuestionNum - 2;
            this.updateProgress();
            this.scrollToTop();
        }
    }

    /**
     * Update progress bar
     */
    updateProgress() {
        const totalQuestions = this.quizData.questions.length;
        const currentNum = this.currentQuestion + 1;
        const percentage = (currentNum / totalQuestions) * 100;

        const progressFill = document.getElementById('quiz-progress-fill');
        const currentQuestionSpan = document.getElementById('current-question');

        if (progressFill) {
            progressFill.style.width = `${percentage}%`;
        }

        if (currentQuestionSpan) {
            currentQuestionSpan.textContent = currentNum;
        }
    }

    /**
     * Submit quiz and calculate score
     */
    submitQuiz() {
        // Validate all questions are answered
        const totalQuestions = this.quizData.questions.length;
        const answeredQuestions = Object.keys(this.answers).length;

        if (answeredQuestions < totalQuestions) {
            const unanswered = totalQuestions - answeredQuestions;
            if (!confirm(`You have ${unanswered} unanswered question(s). Submit anyway?`)) {
                return;
            }
        }

        // Calculate score
        this.score = 0;
        this.quizData.questions.forEach((question, index) => {
            const questionNum = index + 1;
            const userAnswer = this.answers[questionNum];
            const correctAnswer = question.correct_answer;

            if (this.checkAnswer(userAnswer, correctAnswer, question.type)) {
                this.score++;
            }
        });

        const percentage = Math.round((this.score / totalQuestions) * 100);

        // Save to progress tracker
        const progressTracker = new ProgressTracker();
        progressTracker.recordQuizAttempt(this.moduleId, this.score, totalQuestions);

        // Show results
        this.showResults(percentage);
        this.quizSubmitted = true;
    }

    /**
     * Check if an answer is correct
     */
    checkAnswer(userAnswer, correctAnswer, questionType) {
        if (questionType === 'true_false') {
            return userAnswer === correctAnswer.toString();
        }
        return userAnswer === correctAnswer.toString();
    }

    /**
     * Show quiz results
     */
    showResults(percentage) {
        const quizContent = document.getElementById('quiz-content');
        const quizResults = document.getElementById('quiz-results');

        if (quizContent) quizContent.style.display = 'none';
        if (quizResults) quizResults.style.display = 'block';

        // Update score display
        const scoreValue = document.getElementById('score-value');
        if (scoreValue) {
            scoreValue.textContent = percentage;
        }

        // Update counts
        const totalQuestions = this.quizData.questions.length;
        const correctCount = document.getElementById('correct-count');
        const incorrectCount = document.getElementById('incorrect-count');

        if (correctCount) correctCount.textContent = this.score;
        if (incorrectCount) incorrectCount.textContent = totalQuestions - this.score;

        // Update message and icon based on performance
        const resultsMessage = document.getElementById('results-message');
        const resultsIcon = document.getElementById('results-icon');
        const resultsTitle = document.getElementById('results-title');

        let message = '';
        let icon = '';
        let title = '';

        if (percentage >= 90) {
            title = 'Outstanding! 🎉';
            icon = '🌟';
            message = 'Excellent work! You have a strong grasp of this module\'s concepts.';
        } else if (percentage >= 70) {
            title = 'Well Done! 👏';
            icon = '✅';
            message = 'Great job! You passed the quiz and can move on to the next module.';
        } else if (percentage >= 50) {
            title = 'Not Bad! 💪';
            icon = '📚';
            message = 'You\'re getting there! Review the lessons and try again to improve your score.';
        } else {
            title = 'Keep Trying! 🎯';
            icon = '📖';
            message = 'Don\'t give up! Review the lessons carefully and retake the quiz.';
        }

        if (resultsMessage) resultsMessage.textContent = message;
        if (resultsIcon) resultsIcon.textContent = icon;
        if (resultsTitle) resultsTitle.textContent = title;

        this.scrollToTop();
    }

    /**
     * Review answers with explanations
     */
    reviewAnswers() {
        const quizResults = document.getElementById('quiz-results');
        const quizContent = document.getElementById('quiz-content');

        if (quizResults) quizResults.style.display = 'none';
        if (quizContent) quizContent.style.display = 'block';

        // Show all questions
        document.querySelectorAll('.question-card').forEach((card, index) => {
            card.style.display = 'block';
        });

        // Hide navigation buttons
        document.querySelectorAll('.question-navigation').forEach(nav => {
            nav.style.display = 'none';
        });

        // Mark correct and incorrect answers
        this.quizData.questions.forEach((question, index) => {
            const questionNum = index + 1;
            const userAnswer = this.answers[questionNum];
            const correctAnswer = question.correct_answer;

            // Highlight the user's answer
            const questionCard = document.getElementById(`question-${questionNum}`);
            if (!questionCard) return;

            const options = questionCard.querySelectorAll('.option-label');
            options.forEach((option, optionIndex) => {
                const input = option.querySelector('.option-input');
                const inputValue = input.value;

                // Mark correct answer
                if (this.checkAnswer(inputValue, correctAnswer, question.type)) {
                    option.classList.add('correct');
                }

                // Mark user's incorrect answer
                if (inputValue === userAnswer &&
                    !this.checkAnswer(userAnswer, correctAnswer, question.type)) {
                    option.classList.add('incorrect');
                }
            });

            // Show explanation
            const explanation = document.getElementById(`explanation-${questionNum}`);
            if (explanation) {
                explanation.style.display = 'block';
            }
        });

        this.scrollToTop();
    }

    /**
     * Retake quiz
     */
    retakeQuiz() {
        if (confirm('Are you sure you want to retake the quiz? Your current score will be saved, but you\'ll start fresh.')) {
            window.location.reload();
        }
    }

    /**
     * Scroll to top of page
     */
    scrollToTop() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    }

    /**
     * Get quiz statistics
     */
    getStatistics() {
        return {
            totalQuestions: this.quizData.questions.length,
            answeredQuestions: Object.keys(this.answers).length,
            score: this.score,
            percentage: Math.round((this.score / this.quizData.questions.length) * 100),
            submitted: this.quizSubmitted
        };
    }
}

// ============================================================================
// Quiz Timer (Optional Feature)
// ============================================================================
class QuizTimer {
    constructor(duration, onComplete) {
        this.duration = duration; // in seconds
        this.remaining = duration;
        this.onComplete = onComplete;
        this.interval = null;
        this.isPaused = false;
    }

    start() {
        this.interval = setInterval(() => {
            if (!this.isPaused) {
                this.remaining--;

                this.updateDisplay();

                if (this.remaining <= 0) {
                    this.stop();
                    if (this.onComplete) {
                        this.onComplete();
                    }
                }
            }
        }, 1000);
    }

    pause() {
        this.isPaused = true;
    }

    resume() {
        this.isPaused = false;
    }

    stop() {
        if (this.interval) {
            clearInterval(this.interval);
            this.interval = null;
        }
    }

    updateDisplay() {
        const minutes = Math.floor(this.remaining / 60);
        const seconds = this.remaining % 60;
        const display = `${minutes}:${seconds.toString().padStart(2, '0')}`;

        const timerElement = document.getElementById('quiz-timer');
        if (timerElement) {
            timerElement.textContent = display;

            // Add warning class when time is running out
            if (this.remaining <= 60) {
                timerElement.classList.add('timer-warning');
            }
        }
    }

    getRemaining() {
        return this.remaining;
    }
}

// ============================================================================
// Initialize Quiz Engine
// ============================================================================
document.addEventListener('DOMContentLoaded', () => {
    // Check if we're on a quiz page
    if (typeof quizData !== 'undefined' && typeof moduleId !== 'undefined') {
        window.quizEngine = new QuizEngine(quizData, moduleId);

        console.log('📝 Quiz Engine initialized');

        // Optional: Add timer if quiz has time limit
        if (quizData.time_limit) {
            const timer = new QuizTimer(quizData.time_limit * 60, () => {
                alert('Time\'s up! The quiz will be submitted automatically.');
                window.quizEngine.submitQuiz();
            });
            timer.start();
        }
    }
});

// Make classes available globally
window.QuizEngine = QuizEngine;
window.QuizTimer = QuizTimer;
