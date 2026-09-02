package com.example.taskboard.presentation.task;

import com.example.taskboard.application.task.CreateTaskRequest;
import com.example.taskboard.application.task.TaskService;
import com.example.taskboard.presentation.response.ResultResponse;
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
        return ResultResponse.from(taskService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateTaskRequest request) {
        return ResultResponse.from(taskService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResultResponse.from(taskService.getById(id));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        return ResultResponse.from(taskService.toggle(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResultResponse.noContent(taskService.delete(id));
    }
}
