package com.example.taskboard.presentation.auth;

public record LoginResponse(
        String username,
        String accessToken,
        String tokenType
) {
}