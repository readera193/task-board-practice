package com.example.taskboard.infrastructure.persistence;

import com.example.taskboard.application.task.TaskRepository;
import com.example.taskboard.domain.task.Task;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private final JpaTaskRepository jpaTaskRepository;

    public TaskRepositoryImpl(JpaTaskRepository jpaTaskRepository) {
        this.jpaTaskRepository = jpaTaskRepository;
    }

    @Override
    public Page<Task> findAllByUsername(String username, Pageable pageable) {
        return jpaTaskRepository.findAllByUserUsername(username, pageable);
    }

    @Override
    public Optional<Task> findByIdAndUsername(Long id, String username) {
        return jpaTaskRepository.findByIdAndUserUsername(id, username);
    }

    @Override
    public Task save(Task task) {
        return jpaTaskRepository.save(task);
    }

    @Override
    public void delete(Task task) {
        jpaTaskRepository.delete(task);
    }

    @Override
    public int deleteOfDemoUsersCreatedBefore(LocalDateTime cutoff) {
        return jpaTaskRepository.deleteOfDemoUsersCreatedBefore(cutoff);
    }
}
