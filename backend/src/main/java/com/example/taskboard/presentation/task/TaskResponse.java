package com.example.taskboard.presentation.task;

import com.example.taskboard.application.task.TaskResult;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        boolean completed,
        LocalDateTime createdAt
) {

    public static TaskResponse from(TaskResult result) {
        return new TaskResponse(
                result.id(),
                result.title(),
                result.completed(),
                result.createdAt()
        );
    }
}