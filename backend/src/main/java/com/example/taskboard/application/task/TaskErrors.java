package com.example.taskboard.application.task;

import com.example.taskboard.shared.result.AppError;

public final class TaskErrors {

    private TaskErrors() {
    }

    public static final AppError NOT_FOUND =
            new AppError(
                    "TASK_NOT_FOUND",
                    "找不到指定的待辦事項"
            );

}