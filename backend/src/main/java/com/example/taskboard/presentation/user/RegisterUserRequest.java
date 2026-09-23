package com.example.taskboard.presentation.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(

        @NotBlank(message = "帳號不可為空")
        @Size(max = 50, message = "帳號不可超過 50 個字")
        String username,

        @NotBlank(message = "密碼不可為空")
        @Size(min = 8, max = 100, message = "密碼長度必須為 8 到 100 個字")
        String password

) {
}