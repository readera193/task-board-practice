package com.example.taskboard.presentation.task;

import org.springframework.data.domain.Pageable;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import com.example.taskboard.application.task.TaskResult;
import com.example.taskboard.application.task.TaskService;
import com.example.taskboard.presentation.response.PagedResponse;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt",
            "title",
            "completed");

    private void validateSort(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new IllegalArgumentException(
                        "Unsupported sort field: " + order.getProperty());
            }
        }
    }

    @GetMapping
    public PagedResponse<TaskResult> getAll(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        validateSort(pageable);

        Page<TaskResult> result = taskService.getAll(
                authentication.getName(),
                pageable);

        return PagedResponse.from(result);
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request, Authentication authentication) {
        return TaskResponse.from(taskService.create(request.title(), authentication.getName()));
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Long id, Authentication authentication) {
        return TaskResponse.from(taskService.getById(id, authentication.getName()));
    }

    @PatchMapping("/{id}/toggle")
    public TaskResponse toggle(@PathVariable Long id, Authentication authentication) {
        return TaskResponse.from(taskService.toggle(id, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        taskService.delete(id, authentication.getName());
    }
}
