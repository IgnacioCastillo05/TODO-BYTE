package com.todoapp.dto;

import com.todoapp.entity.Task;
import com.todoapp.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para transferencia de datos de Tarea.
 * Proporciona una representación completa de una tarea incluyendo
 * información relacionada de lista y usuario, además de métodos
 * utilitarios para verificar estado y fechas.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
public class TaskDTO {
    
    private Long id;
    
    @NotBlank(message = "Título es obligatorio")
    @Size(max = 200, message = "Título no puede tener más de 200 caracteres")
    private String title;
    
    @Size(max = 1000, message = "Descripción no puede tener más de 1000 caracteres")
    private String description;
    
    private Boolean completed;
    private Priority priority;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private Boolean isImportant;
    
    private Long taskListId;
    private String taskListName;
    private String taskListColor;
    
    private Long userId;
    private String userName;
    
    /**
     * Constructor por defecto.
     */
    public TaskDTO() {}
    
    /**
     * Constructor que convierte una entidad Task en TaskDTO.
     * 
     * @param task La entidad Task a convertir
     */
    public TaskDTO(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.completed = task.getCompleted();
        this.priority = task.getPriority();
        this.dueDate = task.getDueDate();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
        this.completedAt = task.getCompletedAt();
        this.isImportant = task.getIsImportant();
        
        this.taskListId = task.getTaskList().getId();
        this.taskListName = task.getTaskList().getName();
        this.taskListColor = task.getTaskList().getColor();
        
        this.userId = task.getTaskList().getUser().getId();
        this.userName = task.getTaskList().getUser().getName();
    }
    
    /**
     * Constructor para crear un TaskDTO con datos básicos.
     * 
     * @param title Título de la tarea
     * @param description Descripción de la tarea
     * @param priority Prioridad de la tarea
     * @param isImportant Si la tarea es importante
     */
    public TaskDTO(String title, String description, Priority priority, Boolean isImportant) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.isImportant = isImportant;
        this.completed = false;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Boolean getIsImportant() { return isImportant; }
    public void setIsImportant(Boolean isImportant) { this.isImportant = isImportant; }
    
    public Long getTaskListId() { return taskListId; }
    public void setTaskListId(Long taskListId) { this.taskListId = taskListId; }
    
    public String getTaskListName() { return taskListName; }
    public void setTaskListName(String taskListName) { this.taskListName = taskListName; }
    
    public String getTaskListColor() { return taskListColor; }
    public void setTaskListColor(String taskListColor) { this.taskListColor = taskListColor; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    /**
     * Verifica si la tarea está vencida.
     * Una tarea está vencida si tiene fecha límite, no está completada
     * y la fecha límite ya pasó.
     * 
     * @return true si la tarea está vencida, false en caso contrario
     */
    public boolean isOverdue() {
        return dueDate != null && !completed && LocalDateTime.now().isAfter(dueDate);
    }
    
    /**
     * Verifica si la tarea vence hoy.
     * 
     * @return true si la tarea vence hoy, false en caso contrario
     */
    public boolean isDueToday() {
        if (dueDate == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return dueDate.toLocalDate().equals(now.toLocalDate());
    }
    
    /**
     * Verifica si la tarea vence pronto (en los próximos 3 días).
     * 
     * @return true si la tarea vence pronto, false en caso contrario
     */
    public boolean isDueSoon() {
        if (dueDate == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return dueDate.isAfter(now) && dueDate.isBefore(now.plusDays(3));
    }
    
    /**
     * Obtiene una representación textual del estado de la tarea.
     * 
     * @return String describiendo el estado actual de la tarea
     */
    public String getStatusDisplay() {
        if (completed) return "Completada";
        if (isOverdue()) return "Vencida";
        if (isDueToday()) return "Vence hoy";
        if (isDueSoon()) return "Vence pronto";
        return "Pendiente";
    }
    
    @Override
    public String toString() {
        return "TaskDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", priority=" + priority +
                ", isImportant=" + isImportant +
                ", taskListName='" + taskListName + '\'' +
                ", statusDisplay='" + getStatusDisplay() + '\'' +
                '}';
    }
}