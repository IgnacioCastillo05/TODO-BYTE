package com.todoapp.entity;

import java.time.LocalDateTime;

/**
 * Entidad que representa una Tarea en el sistema.
 * POJO simple sin anotaciones JPA para almacenamiento en memoria.
 * Contiene toda la información necesaria para gestionar tareas individuales
 * dentro de una lista de tareas.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
public class Task {
    
    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private Priority priority;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private Boolean isImportant;
    private TaskList taskList;
    
    /**
     * Constructor por defecto que inicializa los valores predeterminados.
     * Establece la tarea como no completada, prioridad media, no importante
     * y fechas de creación y actualización actuales.
     */
    public Task() {
        this.completed = false;
        this.priority = Priority.MEDIUM;
        this.isImportant = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param id ID único de la tarea
     * @param title Título de la tarea
     * @param taskList Lista de tareas a la que pertenece
     */
    public Task(Long id, String title, TaskList taskList) {
        this();
        this.id = id;
        this.title = title;
        this.taskList = taskList;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getCompleted() { return completed; }
    
    /**
     * Establece el estado de completado de la tarea.
     * Actualiza automáticamente la fecha de actualización y,
     * si se marca como completada, establece la fecha de completado.
     * 
     * @param completed true si la tarea está completada, false en caso contrario
     */
    public void setCompleted(Boolean completed) { 
        this.completed = completed;
        this.updatedAt = LocalDateTime.now();
        if (completed) {
            this.completedAt = LocalDateTime.now();
        } else {
            this.completedAt = null;
        }
    }
    
    public Priority getPriority() { return priority; }
    
    /**
     * Establece la prioridad de la tarea y actualiza la fecha de modificación.
     * 
     * @param priority Nueva prioridad de la tarea
     */
    public void setPriority(Priority priority) { 
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getDueDate() { return dueDate; }
    
    /**
     * Establece la fecha límite de la tarea y actualiza la fecha de modificación.
     * 
     * @param dueDate Nueva fecha límite de la tarea
     */
    public void setDueDate(LocalDateTime dueDate) { 
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Boolean getIsImportant() { return isImportant; }
    
    /**
     * Establece si la tarea es importante y actualiza la fecha de modificación.
     * 
     * @param isImportant true si la tarea es importante, false en caso contrario
     */
    public void setIsImportant(Boolean isImportant) { 
        this.isImportant = isImportant;
        this.updatedAt = LocalDateTime.now();
    }
    
    public TaskList getTaskList() { return taskList; }
    public void setTaskList(TaskList taskList) { this.taskList = taskList; }
    
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
    
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", priority=" + priority +
                ", isImportant=" + isImportant +
                '}';
    }
}