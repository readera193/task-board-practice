package com.example.taskboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.taskboard.application.user.DemoLoginService;
import com.example.taskboard.application.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "app.demo-login.enabled=true")
@Transactional
class DemoLoginApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private DemoLoginService demoLoginService;
    @Autowired private UserRepository userRepository;

    @Test
    void demoLogin_shouldIssueTokenAndSeedSampleTasks() throws Exception {
        MvcResult result =
                mockMvc.perform(post("/api/auth/demo"))
                        .andExpect(status().isOk())
                        .andExpect(
                                jsonPath("$.username")
                                        .value(org.hamcrest.Matchers.startsWith("demo-")))
                        .andExpect(jsonPath("$.tokenType").value("Bearer"))
                        .andReturn();
        String token =
                objectMapper
                        .readTree(result.getResponse().getContentAsString())
                        .get("accessToken")
                        .asText();

        mockMvc.perform(get("/api/tasks").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(3));
    }

    @Test
    void demoLogin_eachCallCreatesIndependentAccount() throws Exception {
        String first = usernameOf(mockMvc.perform(post("/api/auth/demo")).andReturn());
        String second = usernameOf(mockMvc.perform(post("/api/auth/demo")).andReturn());

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void cleanupExpired_keepsDemoUsersWithinRetention() {
        String demo = demoLoginService.createDemoUser();

        assertThat(demoLoginService.cleanupExpired()).isZero();
        assertThat(userRepository.existsByUsername(demo)).isTrue();
    }

    private String usernameOf(MvcResult result) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("username").asText();
    }
}
