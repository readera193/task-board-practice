package com.example.taskboard.infrastructure.persistence;

import com.example.taskboard.domain.task.Task;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByUserUsername(String username, Pageable pageable);

    Optional<Task> findByIdAndUserUsername(Long id, String username);
}
