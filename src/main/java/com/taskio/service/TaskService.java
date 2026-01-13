package com.taskio.service;

import com.taskio.model.Task;
import com.taskio.model.TaskStatus;
import com.taskio.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
    
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setDueDate(taskDetails.getDueDate());
        
        return taskRepository.save(task);
    }
    
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(task);
    }
    
    // Search and filter methods
    public List<Task> searchTasks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllTasks();
        }
        return taskRepository.searchByTitleOrDescription(searchTerm.trim());
    }
    
    public List<Task> filterByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
    
    public List<Task> filterByDateRange(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findByDueDateBetween(startDate, endDate);
    }
    
    public List<Task> searchAndFilter(String searchTerm, TaskStatus status, LocalDate startDate, LocalDate endDate) {
        // If all filters are null, return all tasks
        if ((searchTerm == null || searchTerm.trim().isEmpty()) && 
            status == null && 
            startDate == null && 
            endDate == null) {
            return getAllTasks();
        }
        
        // Normalize search term
        String normalizedSearch = (searchTerm != null && !searchTerm.trim().isEmpty()) 
            ? searchTerm.trim() 
            : null;
        
        return taskRepository.findBySearchAndFilters(normalizedSearch, status, startDate, endDate);
    }
}
