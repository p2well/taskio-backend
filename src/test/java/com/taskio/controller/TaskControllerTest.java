package com.taskio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskio.model.Task;
import com.taskio.model.TaskStatus;
import com.taskio.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.TODO);
        testTask.setDueDate(LocalDate.of(2026, 1, 20));
        testTask.setCategory("Work");
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() throws Exception {
        // Given
        List<Task> tasks = Arrays.asList(testTask, new Task());
        when(taskService.getAllTasks()).thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Test Task")))
                .andExpect(jsonPath("$[0].status", is("TODO")));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void searchAndFilterTasks_WithSearchQuery_ShouldReturnFilteredTasks() throws Exception {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.searchAndFilter(eq("test"), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/tasks/search")
                        .param("q", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task")));

        verify(taskService, times(1)).searchAndFilter(eq("test"), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void searchAndFilterTasks_WithStatus_ShouldReturnFilteredTasks() throws Exception {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.searchAndFilter(isNull(), eq(TaskStatus.TODO), isNull(), isNull(), isNull()))
                .thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/tasks/search")
                        .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("TODO")));

        verify(taskService, times(1)).searchAndFilter(isNull(), eq(TaskStatus.TODO), isNull(), isNull(), isNull());
    }

    @Test
    void searchAndFilterTasks_WithDateRange_ShouldReturnFilteredTasks() throws Exception {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);
        
        when(taskService.searchAndFilter(isNull(), isNull(), eq(startDate), eq(endDate), isNull()))
                .thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/tasks/search")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(taskService, times(1)).searchAndFilter(isNull(), isNull(), eq(startDate), eq(endDate), isNull());
    }

    @Test
    void searchAndFilterTasks_WithCategory_ShouldReturnFilteredTasks() throws Exception {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskService.searchAndFilter(isNull(), isNull(), isNull(), isNull(), eq("Work")))
                .thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/tasks/search")
                        .param("category", "Work"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Work")));

        verify(taskService, times(1)).searchAndFilter(isNull(), isNull(), isNull(), isNull(), eq("Work"));
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() throws Exception {
        // Given
        List<String> categories = Arrays.asList("Work", "Personal", "Shopping");
        when(taskService.getAllCategories()).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/tasks/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Work")))
                .andExpect(jsonPath("$[1]", is("Personal")))
                .andExpect(jsonPath("$[2]", is("Shopping")));

        verify(taskService, times(1)).getAllCategories();
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() throws Exception {
        // Given
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(testTask));

        // When & Then
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.status", is("TODO")))
                .andExpect(jsonPath("$.category", is("Work")));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        when(taskService.getTaskById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTaskById(999L);
    }

    @Test
    void createTask_WithValidData_ShouldCreateAndReturnTask() throws Exception {
        // Given
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("New Description");
        newTask.setStatus(TaskStatus.TODO);
        newTask.setDueDate(LocalDate.of(2026, 1, 25));
        newTask.setCategory("Personal");

        Task createdTask = new Task();
        createdTask.setId(2L);
        createdTask.setTitle(newTask.getTitle());
        createdTask.setDescription(newTask.getDescription());
        createdTask.setStatus(newTask.getStatus());
        createdTask.setDueDate(newTask.getDueDate());
        createdTask.setCategory(newTask.getCategory());

        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);

        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("New Task")))
                .andExpect(jsonPath("$.description", is("New Description")))
                .andExpect(jsonPath("$.status", is("TODO")))
                .andExpect(jsonPath("$.category", is("Personal")));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void createTask_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - Task with empty title (validation should fail)
        Task invalidTask = new Task();
        invalidTask.setTitle(""); // Empty title violates @NotBlank
        invalidTask.setStatus(TaskStatus.TODO);

        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(Task.class));
    }

    @Test
    void updateTask_WhenTaskExists_ShouldUpdateAndReturnTask() throws Exception {
        // Given
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        updatedTask.setDueDate(LocalDate.of(2026, 2, 1));
        updatedTask.setCategory("Personal");

        Task returnedTask = new Task();
        returnedTask.setId(1L);
        returnedTask.setTitle(updatedTask.getTitle());
        returnedTask.setDescription(updatedTask.getDescription());
        returnedTask.setStatus(updatedTask.getStatus());
        returnedTask.setDueDate(updatedTask.getDueDate());
        returnedTask.setCategory(updatedTask.getCategory());

        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(returnedTask);

        // When & Then
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.category", is("Personal")));

        verify(taskService, times(1)).updateTask(eq(1L), any(Task.class));
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        updatedTask.setStatus(TaskStatus.TODO);

        when(taskService.updateTask(eq(999L), any(Task.class)))
                .thenThrow(new RuntimeException("Task not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).updateTask(eq(999L), any(Task.class));
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(taskService).deleteTask(1L);

        // When & Then
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Task not found with id: 999"))
                .when(taskService).deleteTask(999L);

        // When & Then
        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).deleteTask(999L);
    }
}
