package com.example.taskboard.application.service;

import com.example.taskboard.shared.exception.NotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.taskboard.application.task.TaskRepository;
import com.example.taskboard.application.task.TaskResult;
import com.example.taskboard.application.task.TaskService;
import com.example.taskboard.application.user.UserRepository;
import com.example.taskboard.domain.task.Task;
import com.example.taskboard.domain.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

        @Mock
        private TaskRepository taskRepository;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private TaskService taskService;

        @Test
        void getById_whenTaskExists_shouldReturnTaskResult() {
                User user = new User(
                                1L,
                                "testuser",
                                "password",
                                "ROLE_USER");

                LocalDateTime createdAt = LocalDateTime.of(
                                2026, 9, 23, 9, 0);

                Task task = new Task(
                                1L,
                                "Test Task",
                                false,
                                createdAt,
                                null,
                                user);

                when(taskRepository.findByIdAndUsername(
                                1L,
                                "testuser")).thenReturn(Optional.of(task));

                TaskResult result = taskService.getById(
                                1L,
                                "testuser");

                assertEquals(1L, result.id());
                assertEquals("Test Task", result.title());
                assertFalse(result.completed());
                assertEquals(createdAt, result.createdAt());

                verify(taskRepository).findByIdAndUsername(
                                1L,
                                "testuser");
        }

        @Test
        void getById_whenTaskNotFound_shouldThrowNotFoundException() {
                when(taskRepository.findByIdAndUsername(
                                999L,
                                "testuser")).thenReturn(Optional.empty());

                NotFoundException exception = assertThrows(
                                NotFoundException.class,
                                () -> taskService.getById(
                                                999L,
                                                "testuser"));

                assertEquals("TASK_NOT_FOUND", exception.getCode());
                assertEquals(
                                "找不到指定的待辦事項",
                                exception.getMessage());

                verify(taskRepository).findByIdAndUsername(
                                999L,
                                "testuser");
        }

        @Test
        void create_whenUserExists_shouldCreateTaskForUser() {
                User user = new User(
                                1L,
                                "testuser",
                                "password",
                                "ROLE_USER");

                when(userRepository.findByUsername("testuser"))
                                .thenReturn(Optional.of(user));

                when(taskRepository.save(any(Task.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                TaskResult result = taskService.create(
                                "  Test Task  ",
                                "testuser");

                ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

                verify(taskRepository).save(taskCaptor.capture());

                Task savedTask = taskCaptor.getValue();

                assertEquals("Test Task", savedTask.getTitle());
                assertFalse(savedTask.isCompleted());
                assertEquals(user, savedTask.getUser());

                assertEquals("Test Task", result.title());
                assertFalse(result.completed());
        }

        @Test
        void create_whenUserNotFound_shouldThrowNotFoundException() {
                when(userRepository.findByUsername("testuser"))
                                .thenReturn(Optional.empty());

                NotFoundException exception = assertThrows(
                                NotFoundException.class,
                                () -> taskService.create(
                                                "Test Task",
                                                "testuser"));

                assertEquals(
                                "TASK_OWNER_NOT_FOUND",
                                exception.getCode());

                verify(taskRepository, never())
                                .save(any(Task.class));
        }

        @Test
        void toggle_whenTaskExists_shouldToggleCompleted() {
                User user = new User(
                                1L,
                                "testuser",
                                "password",
                                "ROLE_USER");

                Task task = new Task(
                                1L,
                                "Test Task",
                                false,
                                LocalDateTime.now(),
                                null,
                                user);

                when(taskRepository.findByIdAndUsername(
                                1L,
                                "testuser")).thenReturn(Optional.of(task));

                TaskResult result = taskService.toggle(
                                1L,
                                "testuser");

                assertTrue(result.completed());
                assertTrue(task.isCompleted());

                verify(taskRepository).findByIdAndUsername(
                                1L,
                                "testuser");
                verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        void toggle_whenTaskNotFound_shouldThrowNotFoundException() {
                when(taskRepository.findByIdAndUsername(
                                999L,
                                "testuser")).thenReturn(Optional.empty());

                NotFoundException exception = assertThrows(
                                NotFoundException.class,
                                () -> taskService.toggle(
                                                999L,
                                                "testuser"));

                assertEquals(
                                "TASK_NOT_FOUND",
                                exception.getCode());

                verify(taskRepository).findByIdAndUsername(
                                999L,
                                "testuser");
        }

        @Test
        void delete_whenTaskExists_shouldDeleteTask() {
                User user = new User(
                                1L,
                                "testuser",
                                "password",
                                "ROLE_USER");

                Task task = new Task(
                                1L,
                                "Test Task",
                                false,
                                LocalDateTime.now(),
                                null,
                                user);

                when(taskRepository.findByIdAndUsername(
                                1L,
                                "testuser")).thenReturn(Optional.of(task));

                taskService.delete(
                                1L,
                                "testuser");

                verify(taskRepository).findByIdAndUsername(
                                1L,
                                "testuser");

                verify(taskRepository).delete(task);
        }

        @Test
        void delete_whenTaskNotFound_shouldThrowNotFoundException() {
                when(taskRepository.findByIdAndUsername(
                                999L,
                                "testuser")).thenReturn(Optional.empty());

                NotFoundException exception = assertThrows(
                                NotFoundException.class,
                                () -> taskService.delete(
                                                999L,
                                                "testuser"));

                assertEquals(
                                "TASK_NOT_FOUND",
                                exception.getCode());

                verify(taskRepository).findByIdAndUsername(
                                999L,
                                "testuser");

                verify(taskRepository, never())
                                .delete(any(Task.class));
        }

        @Test
        void getAll_shouldMapResultsAndPreservePaginationMetadata() {
                User user = new User(
                                1L,
                                "testuser",
                                "password",
                                "ROLE_USER");

                Task task1 = new Task(
                                3L,
                                "Task 3",
                                false,
                                LocalDateTime.now(),
                                null,
                                user);

                Task task2 = new Task(
                                4L,
                                "Task 4",
                                true,
                                LocalDateTime.now(),
                                null,
                                user);

                Pageable pageable = PageRequest.of(
                                1,
                                2,
                                Sort.by("createdAt").descending());

                Page<Task> taskPage = new PageImpl<>(
                                List.of(task1, task2),
                                pageable,
                                5);

                when(taskRepository.findAllByUsername(
                                "testuser",
                                pageable)).thenReturn(taskPage);

                Page<TaskResult> result = taskService.getAll(
                                "testuser",
                                pageable);

                assertEquals(1, result.getNumber());
                assertEquals(2, result.getSize());
                assertEquals(5, result.getTotalElements());
                assertEquals(3, result.getTotalPages());
                assertEquals(2, result.getContent().size());
                assertEquals("Task 3", result.getContent().get(0).title());
                assertEquals("Task 4", result.getContent().get(1).title());

                verify(taskRepository).findAllByUsername(
                                "testuser",
                                pageable);
        }
}
