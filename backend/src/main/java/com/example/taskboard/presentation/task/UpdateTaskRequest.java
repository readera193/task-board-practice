package com.example.taskboard.presentation.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(

        @NotBlank(message = "待辦事項標題不可為空")
        @Size(max = 100, message = "待辦事項標題不可超過 100 個字")
        String title,

        @Size(max = 1000, message = "待辦事項描述不可超過 1000 個字")
        String description

) {
}
