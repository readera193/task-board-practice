package com.example.taskboard.application.task;

import com.example.taskboard.domain.task.Task;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepository {

    List<Task> findAll();

    List<Task> findAllWithUser();

    Page<Task> findAllByUsername(
            String username,
            Pageable pageable);

    Optional<Task> findByIdAndUsername(Long id, String username);

    Optional<Task> findById(Long id);

    Task save(Task task);

    void delete(Task task);
}