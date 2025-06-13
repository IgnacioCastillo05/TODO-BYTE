package com.todoapp.controller;

import com.todoapp.entity.Task;
import com.todoapp.entity.Priority;
import com.todoapp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de tareas.
 * Proporciona endpoints para crear, leer, actualizar y eliminar tareas,
 * así como operaciones específicas de listas y usuarios.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Tasks", description = "✅ Gestión básica de tareas")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    /**
     * Crea una nueva tarea en la lista especificada para el usuario dado.
     * 
     * @param listId ID de la lista donde se creará la tarea
     * @param userId ID del usuario propietario
     * @param request Datos de la nueva tarea
     * @return ResponseEntity con la tarea creada
     */
    @PostMapping("/list/{listId}/user/{userId}")
    @Operation(summary = "Crear tarea", description = "Crea una nueva tarea")
    public ResponseEntity<TaskResponse> createTask(
            @Parameter(description = "ID de la lista") @PathVariable Long listId,
            @Parameter(description = "ID del usuario") @PathVariable Long userId,
            @Valid @RequestBody TaskCreateRequest request) {
        Task task = taskService.createTask(
                listId,
                userId,
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getDueDate(),
                request.getIsImportant()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskResponse(task));
    }
    
    /**
     * Obtiene una tarea específica por su ID.
     * 
     * @param taskId ID de la tarea a buscar
     * @return ResponseEntity con la tarea encontrada
     */
    @GetMapping("/{taskId}")
    @Operation(summary = "Obtener tarea", description = "Obtiene una tarea específica")
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "ID de la tarea") @PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(new TaskResponse(task));
    }
    
    /**
     * Obtiene todas las tareas de una lista específica para un usuario.
     * 
     * @param listId ID de la lista
     * @param userId ID del usuario propietario
     * @return ResponseEntity con la lista de tareas
     */
    @GetMapping("/list/{listId}/user/{userId}")
    @Operation(summary = "Obtener tareas de lista", description = "Obtiene todas las tareas de una lista")
    public ResponseEntity<List<TaskResponse>> getTasksByList(
            @Parameter(description = "ID de la lista") @PathVariable Long listId,
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksByListId(listId, userId);
        List<TaskResponse> responses = tasks.stream()
                .map(TaskResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Obtiene todas las tareas de todas las listas de un usuario.
     * 
     * @param userId ID del usuario
     * @return ResponseEntity con todas las tareas del usuario
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener todas las tareas del usuario", description = "Obtiene todas las tareas de todas las listas del usuario")
    public ResponseEntity<List<TaskResponse>> getAllTasksByUser(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        List<Task> tasks = taskService.getAllTasksByUserId(userId);
        List<TaskResponse> responses = tasks.stream()
                .map(TaskResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Elimina una tarea específica del usuario.
     * 
     * @param taskId ID de la tarea a eliminar
     * @param userId ID del usuario propietario
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{taskId}/user/{userId}")
    @Operation(summary = "Eliminar tarea", description = "Elimina una tarea")
    public ResponseEntity<Map<String, String>> deleteTask(
            @Parameter(description = "ID de la tarea") @PathVariable Long taskId,
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.ok(Map.of("message", "Tarea eliminada exitosamente"));
    }
    
    /**
     * Clase DTO para las solicitudes de creación de tareas.
     * Contiene todos los campos necesarios para crear una nueva tarea.
     */
    public static class TaskCreateRequest {
        @jakarta.validation.constraints.NotBlank(message = "Título es obligatorio")
        @jakarta.validation.constraints.Size(max = 200, message = "Título no puede tener más de 200 caracteres")
        private String title;
        
        @jakarta.validation.constraints.Size(max = 1000, message = "Descripción no puede tener más de 1000 caracteres")
        private String description;
        
        private Priority priority = Priority.MEDIUM;
        
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime dueDate;
        
        private Boolean isImportant = false;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Priority getPriority() { return priority; }
        public void setPriority(Priority priority) { this.priority = priority; }
        
        public LocalDateTime getDueDate() { return dueDate; }
        public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
        
        public Boolean getIsImportant() { return isImportant; }
        public void setIsImportant(Boolean isImportant) { this.isImportant = isImportant; }
    }
    
    /**
     * Clase DTO para las respuestas de tareas.
     * Contiene toda la información de una tarea para ser enviada al cliente.
     */
    public static class TaskResponse {
        private Long id;
        private String title;
        private String description;
        private Boolean completed;
        private Priority priority;
        private LocalDateTime dueDate;
        private LocalDateTime createdAt;
        private Boolean isImportant;
        private Long taskListId;
        private String taskListName;
        
        /**
         * Constructor que convierte una entidad Task en TaskResponse.
         * 
         * @param task La entidad Task a convertir
         */
        public TaskResponse(Task task) {
            this.id = task.getId();
            this.title = task.getTitle();
            this.description = task.getDescription();
            this.completed = task.getCompleted();
            this.priority = task.getPriority();
            this.dueDate = task.getDueDate();
            this.createdAt = task.getCreatedAt();
            this.isImportant = task.getIsImportant();
            this.taskListId = task.getTaskList().getId();
            this.taskListName = task.getTaskList().getName();
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
        
        public Boolean getIsImportant() { return isImportant; }
        public void setIsImportant(Boolean isImportant) { this.isImportant = isImportant; }
        
        public Long getTaskListId() { return taskListId; }
        public void setTaskListId(Long taskListId) { this.taskListId = taskListId; }
        
        public String getTaskListName() { return taskListName; }
        public void setTaskListName(String taskListName) { this.taskListName = taskListName; }
    }
}