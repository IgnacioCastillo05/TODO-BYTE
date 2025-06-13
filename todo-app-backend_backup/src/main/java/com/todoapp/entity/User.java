package com.todoapp.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Usuario en el sistema.
 * POJO simple sin anotaciones JPA para almacenamiento en memoria.
 * Contiene la información básica del usuario y una colección de
 * sus listas de tareas asociadas.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
public class User {
    
    private Long id;
    private String email;
    private String name;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private List<TaskList> taskLists;
    
    /**
     * Constructor por defecto que inicializa los valores predeterminados.
     * Crea una lista vacía de listas de tareas, establece fechas actuales
     * y marca el usuario como activo.
     */
    public User() {
        this.taskLists = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param id ID único del usuario
     * @param email Dirección de correo electrónico del usuario
     * @param name Nombre completo del usuario
     * @param password Contraseña del usuario
     */
    public User(Long id, String email, String name, String password) {
        this();
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public List<TaskList> getTaskLists() { return taskLists; }
    public void setTaskLists(List<TaskList> taskLists) { this.taskLists = taskLists; }
    
    /**
     * Agrega una lista de tareas al usuario y establece la relación bidireccional.
     * Si la lista de listas de tareas es null, la inicializa.
     * 
     * @param taskList Lista de tareas a agregar al usuario
     */
    public void addTaskList(TaskList taskList) {
        if (taskLists == null) {
            taskLists = new ArrayList<>();
        }
        taskLists.add(taskList);
        taskList.setUser(this);
    }
    
    /**
     * Remueve una lista de tareas del usuario y rompe la relación bidireccional.
     * 
     * @param taskList Lista de tareas a remover del usuario
     */
    public void removeTaskList(TaskList taskList) {
        if (taskLists != null) {
            taskLists.remove(taskList);
            taskList.setUser(null);
        }
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}