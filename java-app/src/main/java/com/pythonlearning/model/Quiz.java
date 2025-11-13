package com.pythonlearning.model;

import java.util.List;

/**
 * Represents a quiz for a module in the Python Learning Platform.
 * Maps to the JSON quiz structure.
 */
public class Quiz {
    private String title;
    private String description;
    private String estimatedTime;
    private int passingScore;
    private List<Question> questions;

    // Constructors
    public Quiz() {}

    public Quiz(String title, String description, int passingScore, List<Question> questions) {
        this.title = title;
        this.description = description;
        this.passingScore = passingScore;
        this.questions = questions;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "title='" + title + '\'' +
                ", questions=" + (questions != null ? questions.size() : 0) +
                ", passingScore=" + passingScore +
                '}';
    }

    /**
     * Nested class for quiz questions
     */
    public static class Question {
        private String question;
        private String type;              // "multiple_choice", "true_false", "code_output"
        private List<String> options;     // For multiple choice questions
        private Object correctAnswer;     // Can be int (index) or String ("true"/"false")
        private String explanation;
        private String code;              // Optional: for code_output type questions

        public Question() {}

        public Question(String question, String type, List<String> options, Object correctAnswer, String explanation) {
            this.question = question;
            this.type = type;
            this.options = options;
            this.correctAnswer = correctAnswer;
            this.explanation = explanation;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public Object getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(Object correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        /**
         * Check if the user's answer is correct
         */
        public boolean isCorrect(Object userAnswer) {
            if (correctAnswer == null || userAnswer == null) {
                return false;
            }

            // Handle different answer types
            if (correctAnswer instanceof Number && userAnswer instanceof Number) {
                return correctAnswer.equals(userAnswer);
            }

            if (correctAnswer instanceof String && userAnswer instanceof String) {
                return ((String) correctAnswer).equalsIgnoreCase((String) userAnswer);
            }

            return correctAnswer.equals(userAnswer);
        }
    }
}
