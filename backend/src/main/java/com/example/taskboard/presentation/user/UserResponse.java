package com.example.taskboard.presentation.user;

import com.example.taskboard.application.user.UserResult;

public record UserResponse(
        Long id,
        String username
) {

    public static UserResponse from(UserResult result) {
        return new UserResponse(
                result.id(),
                result.username()
        );
    }
}