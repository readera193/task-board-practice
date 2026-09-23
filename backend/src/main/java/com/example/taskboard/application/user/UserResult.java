package com.example.taskboard.application.user;

import com.example.taskboard.domain.user.User;

public record UserResult(
        Long id,
        String username
) {

    public static UserResult from(User user) {
        return new UserResult(
                user.getId(),
                user.getUsername()
        );
    }
}