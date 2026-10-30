package com.example.taskboard.infrastructure.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.taskboard.presentation.exception.GlobalExceptionHandler;
import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

class AuthRateLimitInterceptorTest {
    private final Clock clock = mock(Clock.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        when(clock.millis()).thenReturn(0L);
        mvc =
                MockMvcBuilders.standaloneSetup(new Probe())
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .addInterceptors(new AuthRateLimitInterceptor(clock, 2, 1, 4))
                        .build();
    }

    @Test
    void repeatedLoginIsLimitedEvenWhenForwardedHeaderChanges() throws Exception {
        mvc.perform(post("/api/auth/login")).andExpect(status().isOk());
        mvc.perform(post("/api/auth/login")).andExpect(status().isOk());
        mvc.perform(post("/api/auth/login").header("X-Forwarded-For", "203.0.113.9"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"));
    }

    @Test
    void registrationHasSeparateLimitAndWindowResets() throws Exception {
        mvc.perform(post("/api/users/register")).andExpect(status().isOk());
        mvc.perform(post("/api/users/register")).andExpect(status().isTooManyRequests());
        mvc.perform(post("/api/auth/login")).andExpect(status().isOk());
        when(clock.millis()).thenReturn(60_000L);
        mvc.perform(post("/api/users/register")).andExpect(status().isOk());
    }

    @Test
    void globalLimitBoundsRequestsFromManySources() throws Exception {
        for (int i = 0; i < 4; i++) {
            final String address = "192.0.2." + i;
            mvc.perform(
                            post("/api/auth/login")
                                    .with(
                                            request -> {
                                                request.setRemoteAddr(address);
                                                return request;
                                            }))
                    .andExpect(status().isOk());
        }
        mvc.perform(
                        post("/api/auth/login")
                                .with(
                                        request -> {
                                            request.setRemoteAddr("192.0.2.100");
                                            return request;
                                        }))
                .andExpect(status().isTooManyRequests());
    }

    @RestController
    static class Probe {
        @PostMapping({"/api/auth/login", "/api/users/register"})
        String authenticate() {
            return "ok";
        }
    }
}
