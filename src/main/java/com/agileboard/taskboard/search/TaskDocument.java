package com.agileboard.taskboard.search;

public class TaskDocument {
    private String id;
    private String title;
    private String description;
    private String status;

    public TaskDocument() {}

    public TaskDocument(Long id, String title, String description, String status) {
        this.id = String.valueOf(id);
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
}
