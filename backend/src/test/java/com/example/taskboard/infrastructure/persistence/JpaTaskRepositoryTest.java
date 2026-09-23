package com.example.taskboard.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.taskboard.domain.task.Task;
import com.example.taskboard.domain.user.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
                "spring.flyway.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop"
})
class JpaTaskRepositoryTest {

        @Autowired
        private TestEntityManager entityManager;

        @Autowired
        private JpaTaskRepository taskRepository;

        @Test
        void findByIdAndUserUsername_whenOwnerMatches_shouldReturnTask() {
                User user = new User(
                                null,
                                "testuser",
                                "password",
                                "ROLE_USER");

                entityManager.persist(user);

                Task task = new Task(
                                null,
                                "Test Task",
                                false,
                                LocalDateTime.now(),
                                null,
                                user);

                entityManager.persist(task);
                entityManager.flush();

                Optional<Task> result = taskRepository.findByIdAndUserUsername(
                                task.getId(),
                                "testuser");

                assertTrue(result.isPresent());
                assertEquals("Test Task", result.get().getTitle());
                assertEquals("testuser", result.get().getUser().getUsername());
        }

        @Test
        void findByIdAndUserUsername_whenOwnerDoesNotMatch_shouldReturnEmpty() {
                User owner = new User(
                                null,
                                "owner",
                                "password",
                                "ROLE_USER");

                User anotherUser = new User(
                                null,
                                "another",
                                "password",
                                "ROLE_USER");

                entityManager.persist(owner);
                entityManager.persist(anotherUser);

                Task task = new Task(
                                null,
                                "Owner Task",
                                false,
                                LocalDateTime.now(),
                                null,
                                owner);

                entityManager.persist(task);

                entityManager.flush();
                entityManager.clear();

                Optional<Task> result = taskRepository.findByIdAndUserUsername(
                                task.getId(),
                                "another");

                assertTrue(result.isEmpty());
        }

        @Test
        void findAllByUserUsername_shouldReturnOnlyOwnerTasksWithPaginationAndSorting() {
                User userA = new User(
                                null,
                                "userA",
                                "password",
                                "ROLE_USER");

                User userB = new User(
                                null,
                                "userB",
                                "password",
                                "ROLE_USER");

                entityManager.persist(userA);
                entityManager.persist(userB);

                Task task1 = new Task(
                                null,
                                "Task 1",
                                false,
                                LocalDateTime.of(2026, 9, 23, 9, 0),
                                null,
                                userA);

                Task task2 = new Task(
                                null,
                                "Task 2",
                                false,
                                LocalDateTime.of(2026, 9, 23, 10, 0),
                                null,
                                userA);

                Task task3 = new Task(
                                null,
                                "Task 3",
                                false,
                                LocalDateTime.of(2026, 9, 23, 11, 0),
                                null,
                                userA);

                Task otherUserTask = new Task(
                                null,
                                "Other User Task",
                                false,
                                LocalDateTime.of(2026, 9, 23, 12, 0),
                                null,
                                userB);

                entityManager.persist(task1);
                entityManager.persist(task2);
                entityManager.persist(task3);
                entityManager.persist(otherUserTask);

                entityManager.flush();
                entityManager.clear();

                Pageable pageable = PageRequest.of(
                                0,
                                2,
                                Sort.by("createdAt").descending());

                Page<Task> result = taskRepository.findAllByUserUsername(
                                "userA",
                                pageable);

                assertEquals(2, result.getContent().size());
                assertEquals(3, result.getTotalElements());
                assertEquals(2, result.getTotalPages());

                assertEquals("Task 3", result.getContent().get(0).getTitle());
                assertEquals("Task 2", result.getContent().get(1).getTitle());

                assertTrue(
                                result.getContent().stream()
                                                .allMatch(task -> task.getUser().getUsername().equals("userA")));
        }

}
