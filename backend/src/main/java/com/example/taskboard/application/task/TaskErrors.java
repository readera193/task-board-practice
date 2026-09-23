package com.example.taskboard.application.task;

import com.example.taskboard.shared.exception.NotFoundException;

public final class TaskErrors {

    private TaskErrors() {
    }

    public static NotFoundException notFound() {
        return new NotFoundException(
                "TASK_NOT_FOUND",
                "找不到指定的待辦事項"
        );
    }

    public static NotFoundException ownerNotFound() {
        return new NotFoundException(
                "TASK_OWNER_NOT_FOUND",
                "找不到建立此待辦事項的使用者"
        );
    }
}
