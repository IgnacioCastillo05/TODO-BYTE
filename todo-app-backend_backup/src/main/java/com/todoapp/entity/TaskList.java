package com.todoapp.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una Lista de Tareas en el sistema.
 * POJO simple sin anotaciones JPA para almacenamiento en memoria.
 * Contiene una colección de tareas y métodos utilitarios para
 * gestionar las tareas asociadas.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
public class TaskList {
    
    private Long id;
    private String name;
    private String description;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private User user;
    private List<Task> tasks;
    
    /**
     * Constructor por defecto que inicializa los valores predeterminados.
     * Crea una lista vacía de tareas, establece fechas actuales,
     * marca como activa y asigna color predeterminado.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.color = "#007ACC";
    }
    
    /**
     * Constructor con parámetros básicos.
     * 
     * @param id ID único de la lista
     * @param name Nombre de la lista
     * @param description Descripción de la lista
     * @param user Usuario propietario de la lista
     */
    public TaskList(Long id, String name, String description, User user) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
        this.user = user;
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
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
    
    /**
     * Agrega una tarea a la lista y establece la relación bidireccional.
     * Si la lista de tareas es null, la inicializa.
     * 
     * @param task Tarea a agregar a la lista
     */
    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
        task.setTaskList(this);
    }
    
    /**
     * Remueve una tarea de la lista y rompe la relación bidireccional.
     * 
     * @param task Tarea a remover de la lista
     */
    public void removeTask(Task task) {
        if (tasks != null) {
            tasks.remove(task);
            task.setTaskList(null);
        }
    }
    
    /**
     * Cuenta el número de tareas completadas en la lista.
     * 
     * @return Número de tareas marcadas como completadas
     */
    public int getCompletedTasksCount() {
        if (tasks == null) return 0;
        return (int) tasks.stream().filter(Task::getCompleted).count();
    }
    
    /**
     * Cuenta el número total de tareas en la lista.
     * 
     * @return Número total de tareas en la lista
     */
    public int getTotalTasksCount() {
        return tasks != null ? tasks.size() : 0;
    }
    
    @Override
    public String toString() {
        return "TaskList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", totalTasks=" + getTotalTasksCount() +
                '}';
    }
}