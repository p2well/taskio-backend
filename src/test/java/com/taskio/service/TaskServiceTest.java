package com.taskio.service;

import com.taskio.model.Task;
import com.taskio.model.TaskStatus;
import com.taskio.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
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
    void getAllTasks_ShouldReturnAllTasks() {
        // Given
        List<Task> mockTasks = Arrays.asList(testTask, new Task());
        when(taskRepository.findAll()).thenReturn(mockTasks);

        // When
        List<Task> result = taskService.getAllTasks();

        // Then
        assertThat(result).hasSize(2);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When
        Optional<Task> result = taskService.getTaskById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Task> result = taskService.getTaskById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        // Given
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setStatus(TaskStatus.TODO);
        
        when(taskRepository.save(any(Task.class))).thenReturn(newTask);

        // When
        Task result = taskService.createTask(newTask);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("New Task");
        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    void updateTask_WhenTaskExists_ShouldUpdateAndReturnTask() {
        // Given
        Task updatedDetails = new Task();
        updatedDetails.setTitle("Updated Task");
        updatedDetails.setDescription("Updated Description");
        updatedDetails.setStatus(TaskStatus.IN_PROGRESS);
        updatedDetails.setDueDate(LocalDate.of(2026, 2, 1));
        updatedDetails.setCategory("Personal");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.updateTask(1L, updatedDetails);

        // Then
        assertThat(result.getTitle()).isEqualTo("Updated Task");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(result.getDueDate()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(result.getCategory()).isEqualTo("Personal");
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ShouldThrowException() {
        // Given
        Task updatedDetails = new Task();
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.updateTask(999L, updatedDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found with id: 999");
        
        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldDeleteTask() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).delete(testTask);

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldThrowException() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found with id: 999");
        
        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void searchTasks_WithSearchTerm_ShouldReturnMatchingTasks() {
        // Given
        List<Task> mockTasks = Arrays.asList(testTask);
        when(taskRepository.searchByTitleOrDescription("test")).thenReturn(mockTasks);

        // When
        List<Task> result = taskService.searchTasks("test");

        // Then
        assertThat(result).hasSize(1);
        verify(taskRepository, times(1)).searchByTitleOrDescription("test");
    }

    @Test
    void searchTasks_WithEmptySearchTerm_ShouldReturnAllTasks() {
        // Given
        List<Task> mockTasks = Arrays.asList(testTask, new Task());
        when(taskRepository.findAll()).thenReturn(mockTasks);

        // When
        List<Task> result = taskService.searchTasks("");

        // Then
        assertThat(result).hasSize(2);
        verify(taskRepository, times(1)).findAll();
        verify(taskRepository, never()).searchByTitleOrDescription(anyString());
    }

    @Test
    void filterByStatus_ShouldReturnTasksWithStatus() {
        // Given
        List<Task> mockTasks = Arrays.asList(testTask);
        when(taskRepository.findByStatus(TaskStatus.TODO)).thenReturn(mockTasks);

        // When
        List<Task> result = taskService.filterByStatus(TaskStatus.TODO);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(TaskStatus.TODO);
        verify(taskRepository, times(1)).findByStatus(TaskStatus.TODO);
    }

    @Test
    void filterByDateRange_ShouldReturnTasksInRange() {
        // Given
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);
        List<Task> mockTasks = Arrays.asList(testTask);
        when(taskRepository.findByDueDateBetween(startDate, endDate)).thenReturn(mockTasks);

        // When
        List<Task> result = taskService.filterByDateRange(startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        verify(taskRepository, times(1)).findByDueDateBetween(startDate, endDate);
    }

    @Test
    void searchAndFilter_WithAllFilters_ShouldReturnFilteredTasks() {
        // Given
        String searchTerm = "test";
        TaskStatus status = TaskStatus.TODO;
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);
        String category = "Work";
        
        List<Task> mockTasks = Arrays.asList(testTask);
        when(taskRepository.findBySearchAndFilters(searchTerm, status, startDate, endDate, category))
                .thenReturn(mockTasks);

        // When
        List<Task> result = taskService.searchAndFilter(searchTerm, status, startDate, endDate, category);

        // Then
        assertThat(result).hasSize(1);
        verify(taskRepository, times(1)).findBySearchAndFilters(searchTerm, status, startDate, endDate, category);
    }

    @Test
    void searchAndFilter_WithNoFilters_ShouldReturnAllTasks() {
        // Given
        List<Task> mockTasks = Arrays.asList(testTask, new Task());
        when(taskRepository.findAll()).thenReturn(mockTasks);

        // When
        List<Task> result = taskService.searchAndFilter(null, null, null, null, null);

        // Then
        assertThat(result).hasSize(2);
        verify(taskRepository, times(1)).findAll();
        verify(taskRepository, never()).findBySearchAndFilters(any(), any(), any(), any(), any());
    }

    @Test
    void getAllCategories_ShouldReturnDistinctCategories() {
        // Given
        List<String> mockCategories = Arrays.asList("Work", "Personal", "Shopping");
        when(taskRepository.findDistinctCategories()).thenReturn(mockCategories);

        // When
        List<String> result = taskService.getAllCategories();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("Work", "Personal", "Shopping");
        verify(taskRepository, times(1)).findDistinctCategories();
    }
}
