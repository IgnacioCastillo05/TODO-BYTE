package com.todoapp.controller;

import com.todoapp.entity.TaskList;
import com.todoapp.service.TaskListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de listas de tareas.
 * Proporciona endpoints para crear, leer, actualizar y eliminar listas de tareas,
 * asociadas a usuarios específicos.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/lists")
@CrossOrigin(origins = "*")
@Tag(name = "Task Lists", description = "📋 Gestión básica de listas de tareas")
public class TaskListController {
    
    @Autowired
    private TaskListService taskListService;
    
    /**
     * Crea una nueva lista de tareas para el usuario especificado.
     * 
     * @param userId ID del usuario propietario
     * @param request Datos de la nueva lista
     * @return ResponseEntity con la lista creada
     */
    @PostMapping("/user/{userId}")
    @Operation(summary = "Crear lista", description = "Crea una nueva lista de tareas")
    public ResponseEntity<TaskListResponse> createTaskList(
            @Parameter(description = "ID del usuario") @PathVariable Long userId,
            @Valid @RequestBody TaskListCreateRequest request) {
        TaskList taskList = taskListService.createTaskList(
                userId, 
                request.getName(), 
                request.getDescription(), 
                request.getColor()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskListResponse(taskList));
    }
    
    /**
     * Obtiene una lista de tareas específica por su ID.
     * 
     * @param listId ID de la lista a buscar
     * @return ResponseEntity con la lista encontrada
     */
    @GetMapping("/{listId}")
    @Operation(summary = "Obtener lista", description = "Obtiene una lista de tareas específica")
    public ResponseEntity<TaskListResponse> getTaskListById(
            @Parameter(description = "ID de la lista") @PathVariable Long listId) {
        TaskList taskList = taskListService.getTaskListById(listId);
        return ResponseEntity.ok(new TaskListResponse(taskList));
    }
    
    /**
     * Obtiene todas las listas de tareas de un usuario específico.
     * 
     * @param userId ID del usuario
     * @return ResponseEntity con las listas del usuario
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener listas del usuario", description = "Obtiene todas las listas de un usuario")
    public ResponseEntity<List<TaskListResponse>> getTaskListsByUserId(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        List<TaskList> taskLists = taskListService.getTaskListsByUserId(userId);
        List<TaskListResponse> responses = taskLists.stream()
                .map(TaskListResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Elimina una lista de tareas específica del usuario.
     * 
     * @param listId ID de la lista a eliminar
     * @param userId ID del usuario propietario
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{listId}/user/{userId}")
    @Operation(summary = "Eliminar lista", description = "Elimina una lista de tareas")
    public ResponseEntity<Map<String, String>> deleteTaskList(
            @Parameter(description = "ID de la lista") @PathVariable Long listId,
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        taskListService.deleteTaskList(listId, userId);
        return ResponseEntity.ok(Map.of("message", "Lista eliminada exitosamente"));
    }
    
    /**
     * Clase DTO para las solicitudes de creación de listas de tareas.
     * Contiene todos los campos necesarios para crear una nueva lista.
     */
    public static class TaskListCreateRequest {
        @jakarta.validation.constraints.NotBlank(message = "Nombre es obligatorio")
        @jakarta.validation.constraints.Size(max = 100, message = "Nombre no puede tener más de 100 caracteres")
        private String name;
        
        @jakarta.validation.constraints.Size(max = 500, message = "Descripción no puede tener más de 500 caracteres")
        private String description;
        
        @jakarta.validation.constraints.Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color debe ser un código hexadecimal válido")
        private String color = "#007ACC";
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }
    
    /**
     * Clase DTO para las respuestas de listas de tareas.
     * Contiene toda la información de una lista para ser enviada al cliente.
     */
    public static class TaskListResponse {
        private Long id;
        private String name;
        private String description;
        private String color;
        private String createdAt;
        private Long userId;
        private String userName;
        
        /**
         * Constructor que convierte una entidad TaskList en TaskListResponse.
         * 
         * @param taskList La entidad TaskList a convertir
         */
        public TaskListResponse(TaskList taskList) {
            this.id = taskList.getId();
            this.name = taskList.getName();
            this.description = taskList.getDescription();
            this.color = taskList.getColor();
            this.createdAt = taskList.getCreatedAt().toString();
            this.userId = taskList.getUser().getId();
            this.userName = taskList.getUser().getName();
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }
}