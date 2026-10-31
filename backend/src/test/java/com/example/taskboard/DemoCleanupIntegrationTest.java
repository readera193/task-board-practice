package com.example.taskboard;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.taskboard.application.task.TaskService;
import com.example.taskboard.application.user.DemoLoginService;
import com.example.taskboard.application.user.UserRepository;
import com.example.taskboard.application.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/** 保留時間設為 0，讓剛建立的 Demo 帳號立即視為過期，驗證清除只影響 Demo 帳號。 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(
        properties = {"app.demo-login.enabled=true", "app.demo-login.retention-hours=0"})
@Transactional
class DemoCleanupIntegrationTest {

    @Autowired private DemoLoginService demoLoginService;
    @Autowired private UserService userService;
    @Autowired private TaskService taskService;
    @Autowired private UserRepository userRepository;

    @Test
    void cleanupExpired_removesDemoUsersAndTheirTasks_butKeepsRegularUsers() {
        String demo = demoLoginService.createDemoUser();
        userService.register("regular-user", "password123");
        taskService.create("一般使用者的任務", null, "regular-user");

        int deleted = demoLoginService.cleanupExpired();

        assertThat(deleted).isEqualTo(1);
        assertThat(userRepository.existsByUsername(demo)).isFalse();
        assertThat(userRepository.existsByUsername("regular-user")).isTrue();
        assertThat(taskService.getAll("regular-user", PageRequest.of(0, 10)).getTotalElements())
                .isEqualTo(1);
    }
}
