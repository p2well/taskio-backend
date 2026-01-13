package com.taskio.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Task entity representing a task in the system")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the task", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    @Schema(description = "Title of the task", example = "Complete project documentation", required = true, maxLength = 100)
    private String title;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(length = 500)
    @Schema(description = "Detailed description of the task", example = "Write comprehensive documentation for the API endpoints", maxLength = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Current status of the task", example = "TODO", defaultValue = "TODO")
    private TaskStatus status = TaskStatus.TODO;
    
    @Column(name = "due_date")
    @Schema(description = "Due date for task completion", example = "2026-01-20")
    private LocalDate dueDate;
    
    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Column(length = 50)
    @Schema(description = "Category or tag for the task", example = "Development", maxLength = 50)
    private String category;
}
