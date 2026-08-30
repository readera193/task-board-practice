package com.example.taskboard.domain.task;

import java.time.LocalDateTime;

public class Task {

    private Long id;
    private String title;
    private boolean completed;
    private LocalDateTime createdAt;

    public Task(
            Long id,
            String title,
            boolean completed,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void toggle() {
        completed = !completed;
    }
}