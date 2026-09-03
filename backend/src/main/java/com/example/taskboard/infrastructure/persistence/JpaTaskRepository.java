package com.example.taskboard.infrastructure.persistence;

import com.example.taskboard.domain.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTaskRepository
        extends JpaRepository<Task, Long> {
}