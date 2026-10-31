package com.example.taskboard.application.user;

import com.example.taskboard.application.task.TaskRepository;
import com.example.taskboard.application.task.TaskResult;
import com.example.taskboard.application.task.TaskService;
import com.example.taskboard.domain.user.Role;
import com.example.taskboard.domain.user.User;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Demo 體驗登入：一鍵建立隨機帳號與範例任務，讓訪客不必註冊；過期帳號由排程清除。 */
@Service
public class DemoLoginService {

    public static final String USERNAME_PREFIX = "demo-";

    private static final List<String> SAMPLE_TASKS =
            List.of("歡迎使用 Task Board", "點選任務可切換完成狀態", "試著新增、編輯或刪除任務");

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final Duration retention;

    public DemoLoginService(
            UserRepository userRepository,
            TaskRepository taskRepository,
            TaskService taskService,
            PasswordEncoder passwordEncoder,
            Clock clock,
            @Value("${app.demo-login.retention-hours:24}") long retentionHours) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.retention = Duration.ofHours(retentionHours);
    }

    /** 建立 Demo 帳號並放入範例任務，回傳帳號名稱。密碼為隨機值且不對外提供，只能經由此流程取得 token。 */
    @Transactional
    public String createDemoUser() {
        String username = USERNAME_PREFIX + UUID.randomUUID().toString().substring(0, 8);
        String password = passwordEncoder.encode(UUID.randomUUID().toString());

        userRepository.save(
                new User(null, username, password, Role.USER, true, LocalDateTime.now(clock)));

        TaskResult first = null;
        for (String title : SAMPLE_TASKS) {
            TaskResult created = taskService.create(title, null, username);
            if (first == null) {
                first = created;
            }
        }
        // 讓畫面一開始就同時看得到「已完成」與「未完成」兩種狀態
        taskService.toggle(first.id(), username);
        return username;
    }

    /** 刪除超過保留時間的 Demo 帳號與其任務，回傳刪除的帳號數。 */
    @Transactional
    public int cleanupExpired() {
        LocalDateTime cutoff = LocalDateTime.now(clock).minus(retention);
        taskRepository.deleteOfDemoUsersCreatedBefore(cutoff);
        return userRepository.deleteDemoUsersCreatedBefore(cutoff);
    }
}
