package com.todoapp.service;

import com.todoapp.entity.Task;
import com.todoapp.entity.TaskList;
import com.todoapp.entity.Priority;
import com.todoapp.exception.ResourceNotFoundException;
import com.todoapp.exception.UnauthorizedAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para la gestión de tareas.
 * Proporciona operaciones CRUD y funcionalidades específicas para tareas,
 * incluyendo validaciones de seguridad y reglas de negocio.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
@Service
public class TaskService {
    
    @Autowired
    private MemoryStorageService storageService;
    
    /**
     * Crea una nueva tarea en la lista especificada.
     * Valida que el usuario tenga permisos sobre la lista y que los datos sean válidos.
     * 
     * @param taskListId ID de la lista donde crear la tarea
     * @param userId ID del usuario propietario
     * @param title Título de la tarea (obligatorio)
     * @param description Descripción de la tarea (opcional)
     * @param priority Prioridad de la tarea (por defecto MEDIUM)
     * @param dueDate Fecha límite de la tarea (opcional)
     * @param isImportant Si la tarea es importante (por defecto false)
     * @return Tarea creada
     * @throws ResourceNotFoundException si la lista no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     * @throws IllegalArgumentException si el título está vacío
     */
    public Task createTask(Long taskListId, Long userId, String title, String description, 
                          Priority priority, LocalDateTime dueDate, Boolean isImportant) {
        
        TaskList taskList = validateTaskListAccess(taskListId, userId);
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la tarea no puede estar vacío");
        }
        
        Task task = new Task();
        task.setTitle(title.trim());
        task.setDescription(description != null ? description.trim() : null);
        task.setPriority(priority != null ? priority : Priority.MEDIUM);
        task.setDueDate(dueDate);
        task.setIsImportant(isImportant != null ? isImportant : false);
        task.setCompleted(false);
        task.setTaskList(taskList);
        
        return storageService.saveTask(task);
    }
    
    /**
     * Obtiene todas las tareas de una lista específica.
     * Valida que el usuario tenga permisos sobre la lista.
     * 
     * @param taskListId ID de la lista
     * @param userId ID del usuario propietario
     * @return Lista de tareas ordenadas por fecha de creación
     * @throws ResourceNotFoundException si la lista no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public List<Task> getTasksByListId(Long taskListId, Long userId) {
        validateTaskListAccess(taskListId, userId);
        return storageService.findTasksByTaskListId(taskListId);
    }
    
    /**
     * Obtiene todas las tareas pendientes de una lista específica.
     * Las tareas se ordenan por fecha límite.
     * 
     * @param taskListId ID de la lista
     * @param userId ID del usuario propietario
     * @return Lista de tareas pendientes ordenadas por fecha límite
     * @throws ResourceNotFoundException si la lista no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public List<Task> getPendingTasksByListId(Long taskListId, Long userId) {
        validateTaskListAccess(taskListId, userId);
        return storageService.findPendingTasksByTaskListId(taskListId);
    }
    
    /**
     * Obtiene todas las tareas completadas de una lista específica.
     * Las tareas se ordenan por fecha de completado.
     * 
     * @param taskListId ID de la lista
     * @param userId ID del usuario propietario
     * @return Lista de tareas completadas ordenadas por fecha de completado
     * @throws ResourceNotFoundException si la lista no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public List<Task> getCompletedTasksByListId(Long taskListId, Long userId) {
        validateTaskListAccess(taskListId, userId);
        return storageService.findCompletedTasksByTaskListId(taskListId);
    }
    
    /**
     * Obtiene una tarea específica por su ID.
     * 
     * @param taskId ID de la tarea
     * @return Tarea encontrada
     * @throws ResourceNotFoundException si la tarea no existe
     */
    public Task getTaskById(Long taskId) {
        return storageService.findTaskById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + taskId));
    }
    
    /**
     * Obtiene una tarea específica validando permisos del usuario.
     * 
     * @param taskId ID de la tarea
     * @param userId ID del usuario
     * @return Tarea encontrada
     * @throws ResourceNotFoundException si la tarea no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public Task getTaskByIdAndUserId(Long taskId, Long userId) {
        Task task = getTaskById(taskId);
        
        if (!task.getTaskList().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("No tienes permisos para acceder a esta tarea");
        }
        
        return task;
    }
    
    /**
     * Actualiza los datos de una tarea existente.
     * Valida permisos del usuario y que los datos sean válidos.
     * 
     * @param taskId ID de la tarea a actualizar
     * @param userId ID del usuario propietario
     * @param title Nuevo título de la tarea (obligatorio)
     * @param description Nueva descripción de la tarea
     * @param priority Nueva prioridad de la tarea
     * @param dueDate Nueva fecha límite de la tarea
     * @param isImportant Si la tarea es importante
     * @return Tarea actualizada
     * @throws ResourceNotFoundException si la tarea no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     * @throws IllegalArgumentException si el título está vacío
     */
    public Task updateTask(Long taskId, Long userId, String title, String description, 
                          Priority priority, LocalDateTime dueDate, Boolean isImportant) {
        
        Task task = getTaskByIdAndUserId(taskId, userId);
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la tarea no puede estar vacío");
        }
        
        task.setTitle(title.trim());
        task.setDescription(description != null ? description.trim() : null);
        task.setPriority(priority != null ? priority : task.getPriority());
        task.setDueDate(dueDate);
        task.setIsImportant(isImportant != null ? isImportant : task.getIsImportant());
        
        return storageService.saveTask(task);
    }
    
    /**
     * Alterna el estado de completado de una tarea.
     * Si está completada la marca como pendiente y viceversa.
     * 
     * @param taskId ID de la tarea
     * @param userId ID del usuario propietario
     * @return Tarea con estado actualizado
     * @throws ResourceNotFoundException si la tarea no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public Task toggleTaskCompletion(Long taskId, Long userId) {
        Task task = getTaskByIdAndUserId(taskId, userId);
        task.setCompleted(!task.getCompleted());
        return storageService.saveTask(task);
    }
    
    /**
     * Alterna el estado de importancia de una tarea.
     * Si es importante la marca como normal y viceversa.
     * 
     * @param taskId ID de la tarea
     * @param userId ID del usuario propietario
     * @return Tarea con estado actualizado
     * @throws ResourceNotFoundException si la tarea no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public Task toggleTaskImportance(Long taskId, Long userId) {
        Task task = getTaskByIdAndUserId(taskId, userId);
        task.setIsImportant(!task.getIsImportant());
        return storageService.saveTask(task);
    }
    
    /**
     * Elimina una tarea del sistema.
     * Valida que el usuario tenga permisos sobre la tarea.
     * 
     * @param taskId ID de la tarea a eliminar
     * @param userId ID del usuario propietario
     * @throws ResourceNotFoundException si la tarea no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public void deleteTask(Long taskId, Long userId) {
        Task task = getTaskByIdAndUserId(taskId, userId);
        storageService.deleteTask(taskId);
    }
    
    /**
     * Mueve una tarea de una lista a otra.
     * Valida que el usuario tenga permisos sobre ambas listas.
     * 
     * @param taskId ID de la tarea a mover
     * @param newTaskListId ID de la lista destino
     * @param userId ID del usuario propietario
     * @return Tarea con nueva lista asignada
     * @throws ResourceNotFoundException si la tarea o lista no existen
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public Task moveTaskToList(Long taskId, Long newTaskListId, Long userId) {
        Task task = getTaskByIdAndUserId(taskId, userId);
        TaskList newTaskList = validateTaskListAccess(newTaskListId, userId);
        
        task.setTaskList(newTaskList);
        return storageService.saveTask(task);
    }
    
    /**
     * Crea una copia de una tarea existente.
     * La tarea duplicada se crea en la misma lista y con estado pendiente.
     * 
     * @param taskId ID de la tarea a duplicar
     * @param userId ID del usuario propietario
     * @return Nueva tarea duplicada
     * @throws ResourceNotFoundException si la tarea no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    public Task duplicateTask(Long taskId, Long userId) {
        Task originalTask = getTaskByIdAndUserId(taskId, userId);
        
        Task duplicatedTask = new Task();
        duplicatedTask.setTitle(originalTask.getTitle() + " (Copia)");
        duplicatedTask.setDescription(originalTask.getDescription());
        duplicatedTask.setPriority(originalTask.getPriority());
        duplicatedTask.setDueDate(originalTask.getDueDate());
        duplicatedTask.setIsImportant(originalTask.getIsImportant());
        duplicatedTask.setCompleted(false);
        duplicatedTask.setTaskList(originalTask.getTaskList());
        
        return storageService.saveTask(duplicatedTask);
    }
    
    /**
     * Obtiene todas las tareas importantes y pendientes de un usuario.
     * Las tareas se ordenan por fecha límite y prioridad.
     * 
     * @param userId ID del usuario
     * @return Lista de tareas importantes pendientes
     */
    public List<Task> getImportantTasks(Long userId) {
        return storageService.findImportantTasksByUserId(userId);
    }
    
    /**
     * Obtiene todas las tareas que vencen en el día actual.
     * Solo incluye tareas pendientes con fecha límite en el día de hoy.
     * 
     * @param userId ID del usuario
     * @return Lista de tareas que vencen hoy
     */
    public List<Task> getTasksDueToday(Long userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        return storageService.findImportantTasksByUserId(userId).stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> !task.getCompleted())
                .filter(task -> {
                    LocalDateTime dueDate = task.getDueDate();
                    return dueDate.isAfter(startOfDay) && dueDate.isBefore(endOfDay);
                })
                .toList();
    }
    
    /**
     * Obtiene todas las tareas vencidas (con fecha límite pasada) de un usuario.
     * Solo incluye tareas pendientes cuya fecha límite ya pasó.
     * 
     * @param userId ID del usuario
     * @return Lista de tareas vencidas
     */
    public List<Task> getOverdueTasks(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        
        return storageService.findImportantTasksByUserId(userId).stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> !task.getCompleted())
                .filter(task -> task.getDueDate().isBefore(now))
                .toList();
    }
    
    /**
     * Obtiene todas las tareas de todas las listas de un usuario.
     * Las tareas se ordenan por fecha de creación descendente.
     * 
     * @param userId ID del usuario
     * @return Lista de todas las tareas del usuario
     */
    public List<Task> getAllTasksByUserId(Long userId) {
        return storageService.findAllTasksByUserId(userId);
    }
    
    /**
     * Valida que un usuario tenga acceso a una lista de tareas específica.
     * 
     * @param taskListId ID de la lista a validar
     * @param userId ID del usuario
     * @return Lista de tareas si el acceso es válido
     * @throws ResourceNotFoundException si la lista no existe
     * @throws UnauthorizedAccessException si el usuario no tiene permisos
     */
    private TaskList validateTaskListAccess(Long taskListId, Long userId) {
        TaskList taskList = storageService.findTaskListById(taskListId)
                .orElseThrow(() -> new ResourceNotFoundException("Lista no encontrada"));
        
        if (!taskList.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("No tienes permisos para acceder a esta lista");
        }
        
        return taskList;
    }
}