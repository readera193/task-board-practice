package com.example.taskboard.presentation.task;

import com.example.taskboard.application.task.TaskService;
import com.example.taskboard.presentation.response.ResultResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResultResponse.from(taskService.getAll(),
                results -> results.stream().map(TaskResponse::from).toList());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateTaskRequest request) {
        return ResultResponse.from(taskService.create(request.title()), TaskResponse::from);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResultResponse.from(taskService.getById(id), TaskResponse::from);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        return ResultResponse.from(taskService.toggle(id), TaskResponse::from);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResultResponse.noContent(taskService.delete(id));
    }
}
