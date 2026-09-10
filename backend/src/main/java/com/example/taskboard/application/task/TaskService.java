package com.example.taskboard.application.task;

import com.example.taskboard.domain.task.Task;
import com.example.taskboard.shared.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Result<TaskResult> create(String title) {

        Task task = new Task(null, title.trim(), false, LocalDateTime.now());

        Task savedTask = taskRepository.save(task);

        return Result.success(TaskResult.from(savedTask));
    }

    @Transactional(readOnly = true)
    public Result<List<TaskResult>> getAll() {

        List<TaskResult> tasks = taskRepository.findAll()
                .stream()
                .map(TaskResult::from)
                .toList();

        return Result.success(tasks);
    }

    @Transactional(readOnly = true)
    public Result<TaskResult> getById(Long id) {
        return taskRepository.findById(id)
                .map(task -> Result.success(TaskResult.from(task)))
                .orElseGet(() -> Result.failure(TaskErrors.NOT_FOUND));
    }

    @Transactional
    public Result<TaskResult> toggle(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isEmpty()) {
            return Result.failure(TaskErrors.NOT_FOUND);
        }

        Task task = optionalTask.get();

        task.toggle();

        return Result.success(TaskResult.from(task));
    }

    @Transactional
    public Result<Void> delete(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isEmpty()) {
            return Result.failure(TaskErrors.NOT_FOUND);
        }

        taskRepository.delete(optionalTask.get());

        return Result.success();
    }
}