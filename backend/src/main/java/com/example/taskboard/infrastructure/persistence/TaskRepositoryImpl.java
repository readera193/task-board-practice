package com.example.taskboard.infrastructure.persistence;

import com.example.taskboard.application.task.TaskRepository;
import com.example.taskboard.domain.task.Task;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private final JpaTaskRepository jpaTaskRepository;

    public TaskRepositoryImpl(JpaTaskRepository jpaTaskRepository) {
        this.jpaTaskRepository = jpaTaskRepository;
    }

    @Override
    public List<Task> findAll() {
        return jpaTaskRepository.findAll();
    }

    @Override
    public Optional<Task> findById(Long id) {
        return jpaTaskRepository.findById(id);
    }

    @Override
    public Task save(Task task) {
        return jpaTaskRepository.save(task);
    }

    @Override
    public void delete(Task task) {
        jpaTaskRepository.delete(task);
    }
}