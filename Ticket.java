package com.helpdesk.model;

public class Ticket {
    public enum Status {
        OPEN, IN_PROGRESS, RESOLVED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    private int id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private long createdAt;
    private int creatorId;
    private int assigneeId; // 0 if unassigned
    private String response;
    private String feedback;

    public Ticket(int id, String title, String description, Priority priority, int creatorId, long createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = Status.OPEN;
        this.priority = priority;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.assigneeId = 0;
        this.response = "";
        this.feedback = "";
    }

    // Constructor for backward compatibility or when loading without timestamp
    public Ticket(int id, String title, String description, Priority priority, int creatorId) {
        this(id, title, description, priority, creatorId, System.currentTimeMillis());
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public int getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(int assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return String.format("Ticket #%d: %s [%s] - %s (Created: %s, Assigned to: %d) | Response: %s | Feedback: %s",
                id, title, status, priority, new java.util.Date(createdAt), assigneeId, response, feedback);
    }
}
