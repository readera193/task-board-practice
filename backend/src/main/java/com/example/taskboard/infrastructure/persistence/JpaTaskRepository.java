package com.example.taskboard.infrastructure.persistence;

import com.example.taskboard.domain.task.Task;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaTaskRepository
        extends JpaRepository<Task, Long> {

    @Query("""
                select t
                from Task t
                join fetch t.user
            """)
    List<Task> findAllWithUser();

    Page<Task> findAllByUserUsername(
            String username,
            Pageable pageable);

    Optional<Task> findByIdAndUserUsername(Long id, String username);
}