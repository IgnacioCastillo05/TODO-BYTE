package com.todoapp.service;

import com.todoapp.entity.TaskList;
import com.todoapp.entity.User;
import com.todoapp.entity.Task;
import com.todoapp.exception.ResourceNotFoundException;
import com.todoapp.exception.UnauthorizedAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de negocio para la gestión de listas de tareas.
 * Proporciona operaciones CRUD y funcionalidades específicas para listas,
 * incluyendo validaciones de seguridad y reglas de negocio.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
@Service
public class TaskListService {
    
    @Autowired
    private MemoryStorageService storageService;
    
    /**
     * Crea una nueva lista de tareas para el usuario especificado.
     * Valida que el usuario exista y que los datos sean válidos.
     * 
     * @param userId ID del usuario propietario
     * @param name Nombre de la lista (obligatorio)
     * @param description Descripción de la lista (opcional)
     * @param color Color de la lista en hexadecimal (opcional, por defecto #007ACC)
     * @return Lista de tareas creada
     * @throws ResourceNotFoundException si el usuario no existe
     * @throws IllegalArgumentException si el nombre está vacío o el color es inválido
     */
    public TaskList createTaskList(Long userId, String name, String description, String color) {
        User user = storageService.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
        }
        
        if (color == null || !isValidHexColor(color)) {
            color = "#007ACC";
        }
        
        TaskList taskList = new TaskList();
        taskList.setName(name.trim());
        taskList.setDescription(description != null ? description.trim() : null);
        taskList.setColor(color);
        taskList.setUser(user);
        taskList.setIsActive(true);
        
        return storageService.saveTaskList(taskList);
    }
    
    /**
     * Obtiene todas las listas de tareas activas de un usuario específico.
     * 
     * @param userId ID del usuario
     * @return Lista de listas de tareas del usuario
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public List<TaskList> getTaskListsByUserId(Long userId) {
        storageService.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        return storageService.findTaskListsByUserId(userId);
    }
    
    /**
     * Obtiene una lista de tareas específica por su ID.
     * 
     * @param id ID de la lista
     * @return Lista de tareas encontrada
     * @throws ResourceNotFoundException si la lista no existe
     */
    public TaskList getTaskListById(Long id) {
        return storageService.findTaskListById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lista no encontrada con ID: " + id));
    }
    
    /**
     * Obtiene una lista de tareas validando permisos del usuario.
     * 
     * @param listId ID de la lista
     * @param userId ID del usuario
     * @return Lista de tareas encontrada
     * @throws ResourceNotFoundException si la lista no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public TaskList getTaskListByIdAndUserId(Long listId, Long userId) {
        TaskList taskList = getTaskListById(listId);
        
        if (!taskList.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("No tienes permisos para acceder a esta lista");
        }
        
        return taskList;
    }
    
    /**
     * Actualiza los datos de una lista de tareas existente.
     * Valida permisos del usuario y que los datos sean válidos.
     * 
     * @param listId ID de la lista a actualizar
     * @param userId ID del usuario propietario
     * @param name Nuevo nombre de la lista (obligatorio)
     * @param description Nueva descripción de la lista
     * @param color Nuevo color de la lista
     * @return Lista de tareas actualizada
     * @throws ResourceNotFoundException si la lista no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     * @throws IllegalArgumentException si el nombre está vacío o el color es inválido
     */
    public TaskList updateTaskList(Long listId, Long userId, String name, String description, String color) {
        TaskList taskList = getTaskListByIdAndUserId(listId, userId);
        
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
        }
        
        if (color != null && !isValidHexColor(color)) {
            throw new IllegalArgumentException("Color inválido. Debe ser un código hexadecimal válido");
        }
        
        taskList.setName(name.trim());
        taskList.setDescription(description != null ? description.trim() : null);
        if (color != null) {
            taskList.setColor(color);
        }
        
        return storageService.saveTaskList(taskList);
    }
    
    /**
     * Elimina una lista de tareas (soft delete).
     * Valida que no sea la única lista del usuario antes de eliminarla.
     * 
     * @param listId ID de la lista a eliminar
     * @param userId ID del usuario propietario
     * @throws ResourceNotFoundException si la lista no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     * @throws IllegalStateException si es la única lista del usuario
     */
    public void deleteTaskList(Long listId, Long userId) {
        TaskList taskList = getTaskListByIdAndUserId(listId, userId);
        
        List<TaskList> userLists = getTaskListsByUserId(userId);
        if (userLists.size() <= 1) {
            throw new IllegalStateException("No puedes eliminar tu única lista de tareas");
        }
        
        taskList.setIsActive(false);
        storageService.saveTaskList(taskList);
    }
    
    /**
     * Obtiene estadísticas detalladas de una lista de tareas.
     * Incluye conteo de tareas totales, completadas y pendientes.
     * 
     * @param listId ID de la lista
     * @param userId ID del usuario propietario
     * @return Estadísticas de la lista
     * @throws ResourceNotFoundException si la lista no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public TaskListStats getTaskListStats(Long listId, Long userId) {
        TaskList taskList = getTaskListByIdAndUserId(listId, userId);
        
        int totalTasks = taskList.getTotalTasksCount();
        int completedTasks = taskList.getCompletedTasksCount();
        int pendingTasks = totalTasks - completedTasks;
        
        return new TaskListStats(totalTasks, completedTasks, pendingTasks);
    }
    
    /**
     * Crea una copia de una lista de tareas existente.
     * Duplica la lista y todas sus tareas, pero marca las tareas como pendientes.
     * 
     * @param listId ID de la lista a duplicar
     * @param userId ID del usuario propietario
     * @param newName Nombre para la nueva lista (opcional, por defecto agrega " (Copia)")
     * @return Nueva lista duplicada
     * @throws ResourceNotFoundException si la lista no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public TaskList duplicateTaskList(Long listId, Long userId, String newName) {
        TaskList originalList = getTaskListByIdAndUserId(listId, userId);
        
        TaskList duplicatedList = new TaskList();
        duplicatedList.setName(newName != null ? newName : originalList.getName() + " (Copia)");
        duplicatedList.setDescription(originalList.getDescription());
        duplicatedList.setColor(originalList.getColor());
        duplicatedList.setUser(originalList.getUser());
        duplicatedList.setIsActive(true);
        
        TaskList savedList = storageService.saveTaskList(duplicatedList);
        
        List<Task> originalTasks = storageService.findTasksByTaskListId(originalList.getId());
        for (Task originalTask : originalTasks) {
            Task duplicatedTask = new Task();
            duplicatedTask.setTitle(originalTask.getTitle());
            duplicatedTask.setDescription(originalTask.getDescription());
            duplicatedTask.setPriority(originalTask.getPriority());
            duplicatedTask.setDueDate(originalTask.getDueDate());
            duplicatedTask.setIsImportant(originalTask.getIsImportant());
            duplicatedTask.setCompleted(false);
            duplicatedTask.setTaskList(savedList);
            
            storageService.saveTask(duplicatedTask);
            savedList.addTask(duplicatedTask);
        }
        
        return storageService.saveTaskList(savedList);
    }
    
    /**
     * Valida si un color está en formato hexadecimal válido.
     * 
     * @param color Color a validar
     * @return true si el color es válido, false en caso contrario
     */
    private boolean isValidHexColor(String color) {
        return color != null && color.matches("^#[0-9A-Fa-f]{6}$");
    }
    
    /**
     * Clase interna para representar estadísticas de una lista de tareas.
     * Proporciona conteos y porcentajes de completado.
     */
    public static class TaskListStats {
        private final int totalTasks;
        private final int completedTasks;
        private final int pendingTasks;
        
        /**
         * Constructor para crear estadísticas de lista.
         * 
         * @param totalTasks Número total de tareas
         * @param completedTasks Número de tareas completadas
         * @param pendingTasks Número de tareas pendientes
         */
        public TaskListStats(int totalTasks, int completedTasks, int pendingTasks) {
            this.totalTasks = totalTasks;
            this.completedTasks = completedTasks;
            this.pendingTasks = pendingTasks;
        }
        
        /**
         * Obtiene el número total de tareas.
         * 
         * @return Número total de tareas
         */
        public int getTotalTasks() { return totalTasks; }
        
        /**
         * Obtiene el número de tareas completadas.
         * 
         * @return Número de tareas completadas
         */
        public int getCompletedTasks() { return completedTasks; }
        
        /**
         * Obtiene el número de tareas pendientes.
         * 
         * @return Número de tareas pendientes
         */
        public int getPendingTasks() { return pendingTasks; }
        
        /**
         * Calcula el porcentaje de completado de la lista.
         * 
         * @return Porcentaje de completado (0-100)
         */
        public double getCompletionPercentage() {
            return totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        }
    }
}