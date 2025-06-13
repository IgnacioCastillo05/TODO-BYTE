package com.todoapp.controller;

import com.todoapp.entity.User;
import com.todoapp.service.UserService;
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
 * Controlador REST para la gestión de usuarios.
 * Proporciona endpoints para crear, leer, actualizar y eliminar usuarios,
 * así como operaciones de autenticación básica.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Users", description = "👥 Gestión básica de usuarios")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param request Datos del nuevo usuario
     * @return ResponseEntity con el usuario creado
     */
    @PostMapping
    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario en el sistema")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request.getEmail(), request.getName(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
    }
    
    /**
     * Obtiene la información de un usuario específico por su ID.
     * 
     * @param userId ID del usuario a buscar
     * @return ResponseEntity con la información del usuario
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Obtener usuario", description = "Obtiene la información de un usuario por ID")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(new UserResponse(user));
    }
    
    /**
     * Obtiene la lista de todos los usuarios activos registrados en el sistema.
     * 
     * @return ResponseEntity con la lista de usuarios activos
     */
    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllActiveUsers();
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .toList();
        return ResponseEntity.ok(userResponses);
    }
    
    /**
     * Elimina un usuario específico del sistema.
     * 
     * @param userId ID del usuario a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado exitosamente"));
    }
    
    /**
     * Clase DTO para las solicitudes de creación de usuarios.
     * Contiene todos los campos necesarios para registrar un nuevo usuario.
     */
    public static class UserCreateRequest {
        @jakarta.validation.constraints.Email(message = "Email debe tener formato válido")
        @jakarta.validation.constraints.NotBlank(message = "Email es obligatorio")
        private String email;
        
        @jakarta.validation.constraints.NotBlank(message = "Nombre es obligatorio")
        @jakarta.validation.constraints.Size(min = 2, max = 100, message = "Nombre debe tener entre 2 y 100 caracteres")
        private String name;
        
        @jakarta.validation.constraints.NotBlank(message = "Contraseña es obligatoria")
        @jakarta.validation.constraints.Size(min = 6, message = "Contraseña debe tener al menos 6 caracteres")
        private String password;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    /**
     * Clase DTO para las respuestas de usuarios.
     * Contiene la información segura de un usuario para ser enviada al cliente.
     */
    public static class UserResponse {
        private Long id;
        private String email;
        private String name;
        private String createdAt;
        
        /**
         * Constructor que convierte una entidad User en UserResponse.
         * 
         * @param user La entidad User a convertir
         */
        public UserResponse(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.name = user.getName();
            this.createdAt = user.getCreatedAt().toString();
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}