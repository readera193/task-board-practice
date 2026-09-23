package com.example.taskboard.application.user;

import com.example.taskboard.shared.exception.ConflictException;

public final class UserErrors {

    private UserErrors() {
    }

    public static ConflictException usernameAlreadyExists() {
        return new ConflictException(
                "USERNAME_ALREADY_EXISTS",
                "帳號已存在"
        );
    }
}
