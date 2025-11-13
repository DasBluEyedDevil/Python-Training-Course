package com.pythonlearning.model;

/**
 * Represents a course module in the Python Learning Platform.
 * Each module contains multiple lessons and a quiz.
 */
public class Module {
    private int id;
    private String title;
    private String subtitle;
    private int lessons;        // Number of lessons in this module
    private String icon;        // Emoji icon for the module

    // Constructors
    public Module() {}

    public Module(int id, String title, String subtitle, int lessons, String icon) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.lessons = lessons;
        this.icon = icon;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getLessons() {
        return lessons;
    }

    public void setLessons(int lessons) {
        this.lessons = lessons;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Get the display name for this module
     */
    public String getDisplayName() {
        return icon + " Module " + id + ": " + title;
    }

    @Override
    public String toString() {
        return "Module " + id + ": " + title + " (" + lessons + " lessons)";
    }
}
