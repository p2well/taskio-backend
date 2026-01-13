package com.taskio.controller;

import com.taskio.model.Task;
import com.taskio.model.TaskStatus;
import com.taskio.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Task Management", description = "API endpoints for managing tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Operation(summary = "Get all tasks", description = "Retrieves a list of all tasks in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))
    })
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(summary = "Search and filter tasks", 
               description = "Search tasks by query string and filter by status, date range, and category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered tasks",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchAndFilterTasks(
            @Parameter(description = "Search query to match against task title and description")
            @RequestParam(required = false) String q,
            @Parameter(description = "Filter by task status")
            @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Filter tasks from this date onwards (ISO format: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Filter tasks up to this date (ISO format: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Filter by category name")
            @RequestParam(required = false) String category
    ) {
        List<Task> tasks = taskService.searchAndFilter(q, status, startDate, endDate, category);
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(summary = "Get all categories", description = "Retrieves a list of all unique task categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved categories",
                content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = taskService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved task",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @Parameter(description = "ID of the task to retrieve")
            @PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Create a new task", description = "Creates a new task with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task successfully created",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "400", description = "Invalid task data provided")
    })
    @PostMapping
    public ResponseEntity<Task> createTask(
            @Parameter(description = "Task object to create")
            @Valid @RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @Operation(summary = "Update an existing task", description = "Updates a task with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task successfully updated",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "400", description = "Invalid task data provided")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "ID of the task to update")
            @PathVariable Long id, 
            @Parameter(description = "Updated task object")
            @Valid @RequestBody Task task) {
        try {
            Task updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Delete a task", description = "Deletes a task by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID of the task to delete")
            @PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
