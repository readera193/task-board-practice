package com.example.taskboard.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

/** 單實例 Demo 的登入與註冊限流；全站上限也限制來源計數的記憶體用量。 */
@Component
public class AuthRateLimitInterceptor implements HandlerInterceptor {
    private static final long WINDOW_MILLIS = 60_000;
    private final Clock clock;
    private final int loginLimit;
    private final int registerLimit;
    private final int totalLimit;
    private final Map<String, Integer> counts = new HashMap<>();
    private long windowStart;
    private int total;

    @Autowired
    public AuthRateLimitInterceptor(
            @Value("${app.auth-rate-limit.login-per-minute:20}") int loginLimit,
            @Value("${app.auth-rate-limit.register-per-minute:10}") int registerLimit,
            @Value("${app.auth-rate-limit.total-per-minute:300}") int totalLimit) {
        this(Clock.systemUTC(), loginLimit, registerLimit, totalLimit);
    }

    AuthRateLimitInterceptor(Clock clock, int loginLimit, int registerLimit, int totalLimit) {
        if (loginLimit < 1 || registerLimit < 1 || totalLimit < 1) {
            throw new IllegalArgumentException("Authentication rate limits must be positive");
        }
        this.clock = clock;
        this.loginLimit = loginLimit;
        this.registerLimit = registerLimit;
        this.totalLimit = totalLimit;
        this.windowStart = clock.millis();
    }

    @Override
    public synchronized boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!"POST".equals(request.getMethod())) return true;
        String path = request.getServletPath();
        if (path.isEmpty()) path = request.getRequestURI();
        boolean login = "/api/auth/login".equals(path);
        if (!login && !"/api/users/register".equals(path)) return true;

        long now = clock.millis();
        if (now - windowStart >= WINDOW_MILLIS) {
            counts.clear();
            total = 0;
            windowStart = now;
        }
        // 不直接採信呼叫者提供的 X-Forwarded-For。
        String key = path + ":" + request.getRemoteAddr();
        int count = counts.getOrDefault(key, 0);
        if (count >= (login ? loginLimit : registerLimit) || total >= totalLimit) {
            long retryAfter = Math.max(1, (WINDOW_MILLIS - (now - windowStart) + 999) / 1000);
            response.setHeader("Retry-After", Long.toString(retryAfter));
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "嘗試次數過多，請稍後再試");
        }
        counts.put(key, count + 1);
        total++;
        return true;
    }
}
