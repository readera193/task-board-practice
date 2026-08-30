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

    public static final AppError INVALID_TITLE =
            new AppError(
                    "TASK_INVALID_TITLE",
                    "待辦事項標題不可為空"
            );
}