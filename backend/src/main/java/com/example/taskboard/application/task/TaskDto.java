package com.example.taskboard.application.task;

import com.example.taskboard.domain.task.Task;

import java.time.LocalDateTime;

public record TaskDto(
        Long id,
        String title,
        boolean completed,
        LocalDateTime createdAt
) {

    public static TaskDto from(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.isCompleted(),
                task.getCreatedAt()
        );
    }
}