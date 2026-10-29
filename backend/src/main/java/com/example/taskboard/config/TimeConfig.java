package com.example.taskboard.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

    /** 集中提供時間來源，Service 注入 Clock 而非直接呼叫 now()，測試時可固定時間。 */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
