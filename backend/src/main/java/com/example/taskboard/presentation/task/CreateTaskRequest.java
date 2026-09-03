package com.example.taskboard.presentation.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(

        @NotBlank(message = "待辦事項標題不可為空")
        @Size(max = 100, message = "待辦事項標題不可超過 100 個字")
        String title

) {
}
