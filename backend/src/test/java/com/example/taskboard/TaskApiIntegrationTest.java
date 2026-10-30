package com.example.taskboard;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskApiIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void registerLoginCreateAndGetTask_shouldWork() throws Exception {

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                  "username": "testuser",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));

        MvcResult loginResult =
                mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                """
                                {
                                  "username": "testuser",
                                  "password": "password123"
                                }
                                """))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.username").value("testuser"))
                        .andExpect(jsonPath("$.tokenType").value("Bearer"))
                        .andReturn();

        JsonNode json = objectMapper.readTree(loginResult.getResponse().getContentAsString());

        String accessToken = json.get("accessToken").asText();

        assertFalse(accessToken.isBlank());

        mockMvc.perform(
                        post("/api/tasks")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {
                                  "title": "Integration Test Task"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.completed").value(false));

        mockMvc.perform(get("/api/tasks").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].title").value("Integration Test Task"))
                .andExpect(jsonPath("$.items[0].completed").value(false));
    }

    @Test
    void getTasks_withoutToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/tasks")).andExpect(status().isUnauthorized());
    }

    @Test
    void register_withExistingUsername_shouldReturnConflict() throws Exception {
        String body =
                """
                { "username": "dupuser", "password": "password123" }
                """;
        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    void getTask_ofAnotherUser_shouldReturnNotFound() throws Exception {
        String tokenA = registerAndLogin("owner-a");
        String tokenB = registerAndLogin("other-b");

        MvcResult created =
                mockMvc.perform(
                                post("/api/tasks")
                                        .header("Authorization", "Bearer " + tokenA)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                """
                                { "title": "A 的任務" }
                                """))
                        .andExpect(status().isCreated())
                        .andReturn();
        long taskId =
                objectMapper
                        .readTree(created.getResponse().getContentAsString())
                        .get("id")
                        .asLong();

        mockMvc.perform(get("/api/tasks/" + taskId).header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
        mockMvc.perform(
                        put("/api/tasks/" + taskId)
                                .header("Authorization", "Bearer " + tokenB)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\":\"unauthorized change\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(
                        patch("/api/tasks/" + taskId + "/toggle")
                                .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/tasks/" + taskId).header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/tasks").header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
        mockMvc.perform(get("/api/tasks/" + taskId).header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("A 的任務"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    private String registerAndLogin(String username) throws Exception {
        String credentials =
                """
                { "username": "%s", "password": "password123" }
                """
                        .formatted(username);
        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(credentials))
                .andExpect(status().isCreated());
        MvcResult login =
                mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(credentials))
                        .andExpect(status().isOk())
                        .andReturn();
        return objectMapper
                .readTree(login.getResponse().getContentAsString())
                .get("accessToken")
                .asText();
    }
}
