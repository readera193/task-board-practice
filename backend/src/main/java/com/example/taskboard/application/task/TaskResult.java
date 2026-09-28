package com.example.taskboard.application.task;

import com.example.taskboard.domain.task.Task;

import java.time.LocalDateTime;

public record TaskResult(
        Long id,
        String title,
        boolean completed,
        LocalDateTime createdAt,
        String description
) {

    public static TaskResult from(Task task) {
        return new TaskResult(
                task.getId(),
                task.getTitle(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getDescription()
        );
    }
}