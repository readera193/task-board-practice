package com.example.taskboard.presentation.task;

import com.example.taskboard.application.task.TaskErrors;
import com.example.taskboard.application.task.TaskResult;
import com.example.taskboard.application.task.TaskService;
import com.example.taskboard.shared.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private TaskService taskService;

        @Test
        void getById_whenAuthenticated_shouldReturnTask() throws Exception {
                LocalDateTime createdAt = LocalDateTime.of(2026, 9, 23, 10, 0);

                TaskResult result = new TaskResult(
                                1L,
                                "Test Task",
                                false,
                                createdAt,
                                null);

                when(taskService.getById(
                                1L,
                                "testuser")).thenReturn(result);

                mockMvc.perform(
                                get("/api/tasks/1")
                                                .principal(authentication()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("Test Task"))
                                .andExpect(jsonPath("$.completed").value(false));

                verify(taskService).getById(
                                1L,
                                "testuser");
        }

        @Test
        void getById_whenTaskNotFound_shouldReturnNotFound() throws Exception {
                when(taskService.getById(
                                999L,
                                "testuser")).thenThrow(
                                                new NotFoundException(
                                                                "TASK_NOT_FOUND",
                                                                "找不到指定的待辦事項"));

                mockMvc.perform(
                                get("/api/tasks/999")
                                                .principal(authentication()))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.code").value("TASK_NOT_FOUND"))
                                .andExpect(jsonPath("$.detail")
                                                .value("找不到指定的待辦事項"));

                verify(taskService).getById(
                                999L,
                                "testuser");
        }

        @Test
        void getAll_shouldPassPageableToService() throws Exception {
                LocalDateTime createdAt = LocalDateTime.of(2026, 9, 23, 10, 0);

                TaskResult task = new TaskResult(
                                1L,
                                "Test Task",
                                false,
                                createdAt,
                                null);

                Pageable expectedPageable = PageRequest.of(
                                0,
                                2,
                                Sort.by("createdAt").descending());

                Page<TaskResult> page = new PageImpl<>(
                                List.of(task),
                                expectedPageable,
                                1);

                when(taskService.getAll(
                                eq("testuser"),
                                any(Pageable.class))).thenReturn(page);

                mockMvc.perform(
                                get("/api/tasks")
                                                .param("page", "0")
                                                .param("size", "2")
                                                .param("sort", "createdAt,desc")
                                                .principal(authentication()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.items[0].id").value(1))
                                .andExpect(jsonPath("$.items[0].title").value("Test Task"))
                                .andExpect(jsonPath("$.page").value(0))
                                .andExpect(jsonPath("$.size").value(2))
                                .andExpect(jsonPath("$.totalElements").value(1))
                                .andExpect(jsonPath("$.totalPages").value(1));

                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

                verify(taskService).getAll(
                                eq("testuser"),
                                pageableCaptor.capture());

                Pageable actualPageable = pageableCaptor.getValue();

                assertEquals(0, actualPageable.getPageNumber());
                assertEquals(2, actualPageable.getPageSize());

                Sort.Order order = actualPageable
                                .getSort()
                                .getOrderFor("createdAt");

                assertNotNull(order);
                assertTrue(order.isDescending());
        }

        @Test
        void getAll_whenNoPaginationParameters_shouldUseDefaultPageable() throws Exception {
                Pageable returnedPageable = PageRequest.of(
                                0,
                                20,
                                Sort.by("createdAt").descending());

                Page<TaskResult> page = new PageImpl<>(
                                List.of(),
                                returnedPageable,
                                0);

                when(taskService.getAll(
                                eq("testuser"),
                                any(Pageable.class))).thenReturn(page);

                mockMvc.perform(
                                get("/api/tasks")
                                                .principal(authentication()))
                                .andExpect(status().isOk());

                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

                verify(taskService).getAll(
                                eq("testuser"),
                                pageableCaptor.capture());

                Pageable actualPageable = pageableCaptor.getValue();

                assertEquals(0, actualPageable.getPageNumber());
                assertEquals(20, actualPageable.getPageSize());

                Sort.Order order = actualPageable
                                .getSort()
                                .getOrderFor("createdAt");

                assertNotNull(order);
                assertTrue(order.isDescending());
        }

        @Test
        void getAll_whenSortFieldIsInvalid_shouldReturnBadRequest() throws Exception {
                mockMvc.perform(
                                get("/api/tasks")
                                                .param("sort", "abc,desc")
                                                .principal(authentication()))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.detail")
                                                .value("Unsupported sort field: abc"));

                verifyNoInteractions(taskService);
        }

        @Test
        void create_whenTitleIsBlank_shouldReturnBadRequest() throws Exception {
                mockMvc.perform(
                                post("/api/tasks")
                                                .principal(authentication())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "title": "   "
                                                                }
                                                                """))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.title")
                                                .value("Validation Failed"))
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.detail")
                                                .value("請求資料驗證失敗"))
                                .andExpect(jsonPath("$.code")
                                                .value("VALIDATION_ERROR"))
                                .andExpect(jsonPath("$.errors.title[0]")
                                                .value("待辦事項標題不可為空"));

                verifyNoInteractions(taskService);
        }

        @Test
        void create_whenTitleExceeds100Characters_shouldReturnBadRequest() throws Exception {
                String title = "a".repeat(101);

                mockMvc.perform(
                                post("/api/tasks")
                                                .principal(authentication())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "title": "%s"
                                                                }
                                                                """.formatted(title)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code")
                                                .value("VALIDATION_ERROR"))
                                .andExpect(jsonPath("$.errors.title[0]")
                                                .value("待辦事項標題不可超過 100 個字"));

                verifyNoInteractions(taskService);
        }

        @Test
        void create_whenRequestIsValid_shouldReturnCreatedTask() throws Exception {
                LocalDateTime createdAt = LocalDateTime.of(2026, 9, 23, 15, 0);

                TaskResult result = new TaskResult(
                                1L,
                                "Test Task",
                                false,
                                createdAt,
                                null);

                when(taskService.create(
                                "Test Task",
                                null,
                                "testuser")).thenReturn(result);

                mockMvc.perform(
                                post("/api/tasks")
                                                .principal(authentication())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "title": "Test Task"
                                                                }
                                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("Test Task"))
                                .andExpect(jsonPath("$.completed").value(false));

                verify(taskService).create(
                                "Test Task",
                                null,
                                "testuser");
        }

        @Test
        void update_whenRequestIsValid_shouldReturnUpdatedTask() throws Exception {
                LocalDateTime createdAt = LocalDateTime.of(2026, 9, 23, 15, 0);

                TaskResult result = new TaskResult(
                                1L,
                                "New Title",
                                false,
                                createdAt,
                                "New Description");

                when(taskService.update(
                                1L,
                                "New Title",
                                "New Description",
                                "testuser")).thenReturn(result);

                mockMvc.perform(
                                put("/api/tasks/1")
                                                .principal(authentication())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "title": "New Title",
                                                                  "description": "New Description"
                                                                }
                                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("New Title"))
                                .andExpect(jsonPath("$.description").value("New Description"));

                verify(taskService).update(
                                1L,
                                "New Title",
                                "New Description",
                                "testuser");
        }

        @Test
        void update_whenTitleIsBlank_shouldReturnBadRequest() throws Exception {
                mockMvc.perform(
                                put("/api/tasks/1")
                                                .principal(authentication())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "title": "   "
                                                                }
                                                                """))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code")
                                                .value("VALIDATION_ERROR"));

                verifyNoInteractions(taskService);
        }

        @Test
        void update_whenTaskNotFound_shouldReturnNotFound() throws Exception {
                when(taskService.update(
                                999L,
                                "New Title",
                                null,
                                "testuser")).thenThrow(TaskErrors.notFound());

                mockMvc.perform(
                                put("/api/tasks/999")
                                                .principal(authentication())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "title": "New Title"
                                                                }
                                                                """))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.code")
                                                .value("TASK_NOT_FOUND"));
        }

        @Test
        void toggle_whenTaskExists_shouldReturnUpdatedTask() throws Exception {
                LocalDateTime createdAt = LocalDateTime.of(2026, 9, 23, 15, 0);

                TaskResult result = new TaskResult(
                                1L,
                                "Test Task",
                                true,
                                createdAt,
                                null);

                when(taskService.toggle(
                                1L,
                                "testuser")).thenReturn(result);

                mockMvc.perform(
                                patch("/api/tasks/1/toggle")
                                                .principal(authentication()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("Test Task"))
                                .andExpect(jsonPath("$.completed").value(true));

                verify(taskService).toggle(
                                1L,
                                "testuser");
        }

        @Test
        void delete_whenTaskExists_shouldReturnNoContent() throws Exception {
                mockMvc.perform(
                                delete("/api/tasks/1")
                                                .principal(authentication()))
                                .andExpect(status().isNoContent());

                verify(taskService).delete(
                                1L,
                                "testuser");
        }

        @Test
        void delete_whenTaskNotFound_shouldReturnNotFound() throws Exception {
                doThrow(TaskErrors.notFound())
                                .when(taskService)
                                .delete(
                                                999L,
                                                "testuser");

                mockMvc.perform(
                                delete("/api/tasks/999")
                                                .principal(authentication()))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.code")
                                                .value("TASK_NOT_FOUND"));

                verify(taskService).delete(
                                999L,
                                "testuser");
        }

        private Authentication authentication() {
                return new UsernamePasswordAuthenticationToken(
                                "testuser",
                                null,
                                List.of());
        }

}