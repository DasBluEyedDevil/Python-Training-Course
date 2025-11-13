package com.pythonlearning.model;

/**
 * Represents a single lesson in the Python Learning Platform.
 * This class maps to the JSON lesson structure created for the course.
 *
 * Philosophy: "Concept First, Jargon Last"
 * Each lesson follows a strict 6-part format.
 */
public class Lesson {
    private String title;
    private String estimatedTime;
    private String concept;              // HTML content with real-world analogies
    private CodeExample codeExample;
    private String syntaxBreakdown;      // Line-by-line explanation
    private Exercise exercise;
    private Solution solution;
    private String keyTakeaways;         // Bullet-point summary

    // Constructors
    public Lesson() {}

    public Lesson(String title, String estimatedTime, String concept) {
        this.title = title;
        this.estimatedTime = estimatedTime;
        this.concept = concept;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public CodeExample getCodeExample() {
        return codeExample;
    }

    public void setCodeExample(CodeExample codeExample) {
        this.codeExample = codeExample;
    }

    public String getSyntaxBreakdown() {
        return syntaxBreakdown;
    }

    public void setSyntaxBreakdown(String syntaxBreakdown) {
        this.syntaxBreakdown = syntaxBreakdown;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public String getKeyTakeaways() {
        return keyTakeaways;
    }

    public void setKeyTakeaways(String keyTakeaways) {
        this.keyTakeaways = keyTakeaways;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "title='" + title + '\'' +
                ", estimatedTime='" + estimatedTime + '\'' +
                '}';
    }

    /**
     * Nested class for code examples
     */
    public static class CodeExample {
        private String language;
        private String code;
        private String output;

        public CodeExample() {}

        public CodeExample(String language, String code, String output) {
            this.language = language;
            this.code = code;
            this.output = output;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }
    }

    /**
     * Nested class for exercises
     */
    public static class Exercise {
        private String instructions;
        private String starterCode;
        private String hint;

        public Exercise() {}

        public Exercise(String instructions, String starterCode, String hint) {
            this.instructions = instructions;
            this.starterCode = starterCode;
            this.hint = hint;
        }

        public String getInstructions() {
            return instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        public String getStarterCode() {
            return starterCode;
        }

        public void setStarterCode(String starterCode) {
            this.starterCode = starterCode;
        }

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }
    }

    /**
     * Nested class for solutions
     */
    public static class Solution {
        private String code;
        private String explanation;
        private String commonMistakes;

        public Solution() {}

        public Solution(String code, String explanation, String commonMistakes) {
            this.code = code;
            this.explanation = explanation;
            this.commonMistakes = commonMistakes;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getCommonMistakes() {
            return commonMistakes;
        }

        public void setCommonMistakes(String commonMistakes) {
            this.commonMistakes = commonMistakes;
        }
    }
}
