package com.example.taskboard.application.task;

import com.example.taskboard.application.user.UserRepository;
import com.example.taskboard.domain.task.Task;
import com.example.taskboard.domain.user.User;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Transactional
    public TaskResult create(String title, String description, String username) {

        User owner = userRepository.findByUsername(username).orElseThrow(TaskErrors::ownerNotFound);

        Task task =
                new Task(
                        null,
                        title.trim(),
                        false,
                        LocalDateTime.now(clock),
                        normalizeDescription(description),
                        owner);

        Task savedTask = taskRepository.save(task);

        return TaskResult.from(savedTask);
    }

    @Transactional(readOnly = true)
    public Page<TaskResult> getAll(String username, Pageable pageable) {
        return taskRepository.findAllByUsername(username, pageable).map(TaskResult::from);
    }

    @Transactional(readOnly = true)
    public TaskResult getById(Long id, String username) {
        Task task =
                taskRepository.findByIdAndUsername(id, username).orElseThrow(TaskErrors::notFound);

        return TaskResult.from(task);
    }

    @Transactional
    public TaskResult toggle(Long id, String username) {
        Task task =
                taskRepository.findByIdAndUsername(id, username).orElseThrow(TaskErrors::notFound);

        task.toggle();

        return TaskResult.from(task);
    }

    @Transactional
    public TaskResult update(Long id, String title, String description, String username) {
        Task task =
                taskRepository.findByIdAndUsername(id, username).orElseThrow(TaskErrors::notFound);

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
        Task task =
                taskRepository.findByIdAndUsername(id, username).orElseThrow(TaskErrors::notFound);

        taskRepository.delete(task);
    }
}
