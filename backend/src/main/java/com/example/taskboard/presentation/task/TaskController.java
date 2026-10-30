package com.example.taskboard.presentation.task;

import com.example.taskboard.application.task.TaskResult;
import com.example.taskboard.application.task.TaskService;
import com.example.taskboard.presentation.response.PagedResponse;
import com.example.taskboard.shared.exception.BusinessException;
import com.example.taskboard.shared.exception.ErrorType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任務", description = "任務 CRUD 與完成狀態切換；只能存取自己的任務，排序欄位限 createdAt、title、completed")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("createdAt", "title", "completed");

    private void validateSort(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new BusinessException(
                        "INVALID_SORT_FIELD",
                        "Unsupported sort field: " + order.getProperty(),
                        ErrorType.VALIDATION);
            }
        }
    }

    @GetMapping
    public PagedResponse<TaskResult> getAll(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        validateSort(pageable);

        Page<TaskResult> result = taskService.getAll(authentication.getName(), pageable);

        return PagedResponse.from(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(
            @Valid @RequestBody CreateTaskRequest request, Authentication authentication) {
        return TaskResponse.from(
                taskService.create(
                        request.title(), request.description(), authentication.getName()));
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Long id, Authentication authentication) {
        return TaskResponse.from(taskService.getById(id, authentication.getName()));
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            Authentication authentication) {
        return TaskResponse.from(
                taskService.update(
                        id, request.title(), request.description(), authentication.getName()));
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
