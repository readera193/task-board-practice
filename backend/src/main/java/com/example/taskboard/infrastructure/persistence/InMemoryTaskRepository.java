package com.example.taskboard.infrastructure.persistence;

import com.example.taskboard.application.task.TaskRepository;
import com.example.taskboard.domain.task.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTaskRepository implements TaskRepository {

    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Task> findAll() {
        return List.copyOf(tasks);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    @Override
    public Task save(Task task) {

        Task savedTask = new Task(
                idGenerator.getAndIncrement(),
                task.getTitle(),
                task.isCompleted(),
                task.getCreatedAt()
        );

        tasks.add(savedTask);

        return savedTask;
    }

    @Override
    public void delete(Task task) {
        tasks.remove(task);
    }
}