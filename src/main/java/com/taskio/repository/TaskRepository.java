package com.taskio.repository;

import com.taskio.model.Task;
import com.taskio.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Search by title or description
    @Query("SELECT t FROM Task t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Task> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);
    
    // Find by status
    List<Task> findByStatus(TaskStatus status);
    
    // Find by due date range
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findByDueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Combined search and filter
    @Query("SELECT t FROM Task t WHERE " +
           "(:searchTerm IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:startDate IS NULL OR t.dueDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.dueDate <= :endDate) AND " +
           "(:category IS NULL OR t.category = :category)")
    List<Task> findBySearchAndFilters(
        @Param("searchTerm") String searchTerm,
        @Param("status") TaskStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("category") String category
    );
    
    // Get distinct categories
    @Query("SELECT DISTINCT t.category FROM Task t WHERE t.category IS NOT NULL ORDER BY t.category")
    List<String> findDistinctCategories();
}
