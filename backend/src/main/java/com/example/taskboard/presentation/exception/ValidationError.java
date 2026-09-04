package com.example.taskboard.presentation.exception;

import java.util.List;
import java.util.Map;

public record ValidationError(
        Map<String, List<String>> errors
) {
}