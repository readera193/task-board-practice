package com.example.taskboard.infrastructure.scheduling;

import com.example.taskboard.application.user.DemoLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 定期清除過期的 Demo 帳號；僅在啟用 Demo 登入時註冊。 */
@Component
@ConditionalOnProperty(name = "app.demo-login.enabled", havingValue = "true")
public class DemoCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(DemoCleanupJob.class);

    private final DemoLoginService demoLoginService;

    public DemoCleanupJob(DemoLoginService demoLoginService) {
        this.demoLoginService = demoLoginService;
    }

    @Scheduled(fixedDelayString = "${app.demo-login.cleanup-interval-ms:3600000}")
    public void cleanup() {
        try {
            int deleted = demoLoginService.cleanupExpired();
            if (deleted > 0) {
                log.info("已清除過期 Demo 帳號 {} 個", deleted);
            }
        } catch (RuntimeException e) {
            // 清除失敗不應影響服務，下次排程再試
            log.warn("清除過期 Demo 帳號失敗", e);
        }
    }
}
