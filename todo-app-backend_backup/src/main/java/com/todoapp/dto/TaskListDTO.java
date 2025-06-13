package com.todoapp.dto;

import com.todoapp.entity.TaskList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para transferencia de datos de Lista de Tareas.
 * Proporciona una representación completa de una lista de tareas incluyendo
 * estadísticas calculadas y información del usuario propietario.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
public class TaskListDTO {
    
    private Long id;
    
    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 100, message = "Nombre no puede tener más de 100 caracteres")
    private String name;
    
    @Size(max = 500, message = "Descripción no puede tener más de 500 caracteres")
    private String description;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color debe ser un código hexadecimal válido")
    private String color;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private Long userId;
    private String userName;
    
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private double completionPercentage;
    
    /**
     * Constructor por defecto.
     */
    public TaskListDTO() {}
    
    /**
     * Constructor que convierte una entidad TaskList en TaskListDTO.
     * Calcula automáticamente las estadísticas de la lista.
     * 
     * @param taskList La entidad TaskList a convertir
     */
    public TaskListDTO(TaskList taskList) {
        this.id = taskList.getId();
        this.name = taskList.getName();
        this.description = taskList.getDescription();
        this.color = taskList.getColor();
        this.createdAt = taskList.getCreatedAt();
        this.updatedAt = taskList.getUpdatedAt();
        this.isActive = taskList.getIsActive();
        this.userId = taskList.getUser().getId();
        this.userName = taskList.getUser().getName();
        
        this.totalTasks = taskList.getTotalTasksCount();
        this.completedTasks = taskList.getCompletedTasksCount();
        this.pendingTasks = this.totalTasks - this.completedTasks;
        this.completionPercentage = this.totalTasks > 0 ? 
            (double) this.completedTasks / this.totalTasks * 100 : 0;
    }
    
    /**
     * Constructor para crear un TaskListDTO con datos básicos.
     * 
     * @param name Nombre de la lista
     * @param description Descripción de la lista
     * @param color Color de la lista
     */
    public TaskListDTO(String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
    
    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
    
    public int getPendingTasks() { return pendingTasks; }
    public void setPendingTasks(int pendingTasks) { this.pendingTasks = pendingTasks; }
    
    public double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; }
    
    @Override
    public String toString() {
        return "TaskListDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", totalTasks=" + totalTasks +
                ", completedTasks=" + completedTasks +
                ", completionPercentage=" + completionPercentage +
                '}';
    }
}