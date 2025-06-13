package com.todoapp.dto;

import com.todoapp.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para transferencia de datos de Usuario.
 * Versión segura que no expone información sensible como contraseñas,
 * e incluye estadísticas calculadas del usuario como número de listas y tareas.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
public class UserDTO {
    
    private Long id;
    
    @Email(message = "Email debe tener formato válido")
    @NotBlank(message = "Email es obligatorio")
    private String email;
    
    @NotBlank(message = "Nombre es obligatorio")
    @Size(min = 2, max = 100, message = "Nombre debe tener entre 2 y 100 caracteres")
    private String name;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private int totalLists;
    private int totalTasks;
    
    /**
     * Constructor por defecto.
     */
    public UserDTO() {}
    
    /**
     * Constructor que convierte una entidad User en UserDTO.
     * Calcula automáticamente las estadísticas del usuario.
     * 
     * @param user La entidad User a convertir
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.isActive = user.getIsActive();
        this.totalLists = user.getTaskLists() != null ? user.getTaskLists().size() : 0;
        this.totalTasks = user.getTaskLists() != null ? 
            user.getTaskLists().stream()
                .mapToInt(list -> list.getTasks() != null ? list.getTasks().size() : 0)
                .sum() : 0;
    }
    
    /**
     * Constructor para crear un UserDTO con datos básicos.
     * 
     * @param id ID del usuario
     * @param email Email del usuario
     * @param name Nombre del usuario
     */
    public UserDTO(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public int getTotalLists() { return totalLists; }
    public void setTotalLists(int totalLists) { this.totalLists = totalLists; }
    
    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
    
    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                ", totalLists=" + totalLists +
                ", totalTasks=" + totalTasks +
                '}';
    }
}