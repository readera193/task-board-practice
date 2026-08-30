package com.example.taskboard.application.task;

import com.example.taskboard.domain.task.Task;
import com.example.taskboard.shared.result.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Result<TaskDto> create(CreateTaskRequest request) {

        if (request.title() == null || request.title().isBlank()) {
            return Result.failure(TaskErrors.INVALID_TITLE);
        }

        Task task = new Task(
                null,
                request.title().trim(),
                false,
                LocalDateTime.now()
        );

        Task savedTask = taskRepository.save(task);

        return Result.success(TaskDto.from(savedTask));
    }

    public Result<List<TaskDto>> getAll() {

        List<TaskDto> tasks = taskRepository
                .findAll()
                .stream()
                .map(TaskDto::from)
                .toList();

        return Result.success(tasks);
    }
}