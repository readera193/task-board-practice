package com.example.taskboard.application.task;

import com.example.taskboard.application.user.UserRepository;
import com.example.taskboard.domain.task.Task;
import com.example.taskboard.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResult create(String title, String description, String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(TaskErrors::ownerNotFound);

        Task task = new Task(null, title.trim(), false, LocalDateTime.now(),
                normalizeDescription(description), owner);

        Task savedTask = taskRepository.save(task);

        return TaskResult.from(savedTask);
    }

    public Page<TaskResult> getAll(
            String username,
            Pageable pageable) {
        return taskRepository
                .findAllByUsername(username, pageable)
                .map(TaskResult::from);
    }

    @Transactional(readOnly = true)
    public TaskResult getById(Long id, String username) {
        Task task = taskRepository.findByIdAndUsername(id, username)
                .orElseThrow(TaskErrors::notFound);

        return TaskResult.from(task);
    }

    @Transactional
    public TaskResult toggle(Long id, String username) {
        Task task = taskRepository.findByIdAndUsername(id, username)
                .orElseThrow(TaskErrors::notFound);

        task.toggle();

        return TaskResult.from(task);
    }

    @Transactional
    public TaskResult update(Long id, String title, String description, String username) {
        Task task = taskRepository.findByIdAndUsername(id, username)
                .orElseThrow(TaskErrors::notFound);

        task.update(title.trim(), normalizeDescription(description));

        return TaskResult.from(task);
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }

    @Transactional
    public void delete(Long id, String username) {
        Task task = taskRepository.findByIdAndUsername(id, username)
                .orElseThrow(TaskErrors::notFound);

        taskRepository.delete(task);
    }
}
