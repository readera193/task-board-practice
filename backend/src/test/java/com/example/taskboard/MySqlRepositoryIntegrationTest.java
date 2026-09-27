package com.example.taskboard;

import com.example.taskboard.domain.task.Task;
import com.example.taskboard.domain.user.User;
import com.example.taskboard.infrastructure.persistence.JpaTaskRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {"spring.flyway.enabled=true",
                "spring.jpa.hibernate.ddl-auto=validate"})
@ActiveProfiles("test")
@Transactional
class MySqlRepositoryIntegrationTest {

        @Container
        @ServiceConnection
        static final MySQLContainer mysql = new MySQLContainer("mysql:8.4");

        @PersistenceContext
        private EntityManager entityManager;

        @Autowired
        private JpaTaskRepository taskRepository;

        @Test
        void findAllByUserUsername_shouldWorkWithFlywayOnRealMySql() {
                User userA = new User(null, "userA", "password", "ROLE_USER");

                User userB = new User(null, "userB", "password", "ROLE_USER");

                entityManager.persist(userA);
                entityManager.persist(userB);

                entityManager.persist(new Task(null, "Task 1", false,
                                LocalDateTime.of(2026, 9, 23, 9, 0), null, userA));

                entityManager.persist(new Task(null, "Task 2", false,
                                LocalDateTime.of(2026, 9, 23, 10, 0), null, userA));

                entityManager.persist(new Task(null, "Other Task", false,
                                LocalDateTime.of(2026, 9, 23, 11, 0), null, userB));

                entityManager.flush();
                entityManager.clear();

                Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

                Page<Task> result = taskRepository.findAllByUserUsername("userA", pageable);

                assertEquals(2, result.getTotalElements());
                assertEquals(2, result.getContent().size());
                assertEquals("Task 2", result.getContent().get(0).getTitle());
                assertEquals("Task 1", result.getContent().get(1).getTitle());
        }
}
