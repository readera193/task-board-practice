package com.example.taskboard.application.task;

import com.example.taskboard.domain.task.Task;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Task 的資料存取 port。分頁刻意沿用 Spring Data 的 {@link Page} / {@link Pageable}，
 * 不另建一套分頁型別，以免為了隔離框架而重複包裝（取捨說明見 README）。
 */
public interface TaskRepository {

    Page<Task> findAllByUsername(String username, Pageable pageable);

    Optional<Task> findByIdAndUsername(Long id, String username);

    Task save(Task task);

    void delete(Task task);
}
