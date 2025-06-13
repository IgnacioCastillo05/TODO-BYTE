package com.todoapp.service;

import com.todoapp.entity.User;
import com.todoapp.entity.TaskList;
import com.todoapp.exception.ResourceNotFoundException;
import com.todoapp.exception.EmailAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para la gestión de usuarios.
 * Proporciona operaciones CRUD y funcionalidades específicas para usuarios,
 * incluyendo autenticación básica y gestión de cuentas.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
@Service
public class UserService {
    
    @Autowired
    private MemoryStorageService storageService;
    
    /**
     * Crea un nuevo usuario en el sistema.
     * Valida que el email no exista previamente y crea una lista de tareas predeterminada.
     * 
     * @param email Dirección de correo electrónico (debe ser única)
     * @param name Nombre completo del usuario
     * @param password Contraseña del usuario (en producción se encriptaría)
     * @return Usuario creado con lista predeterminada
     * @throws EmailAlreadyExistsException si el email ya está registrado
     */
    public User createUser(String email, String name, String password) {
        if (storageService.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("El email " + email + " ya está registrado");
        }
        
        User user = new User();
        user.setEmail(email.toLowerCase().trim());
        user.setName(name.trim());
        user.setPassword(password);
        user.setIsActive(true);
        
        User savedUser = storageService.saveUser(user);
        createDefaultTaskList(savedUser);
        
        return savedUser;
    }
    
    /**
     * Busca un usuario específico por su ID.
     * 
     * @param id ID del usuario a buscar
     * @return Usuario encontrado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public User getUserById(Long id) {
        return storageService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }
    
    /**
     * Busca un usuario por su dirección de correo electrónico.
     * La búsqueda es case-insensitive.
     * 
     * @param email Email del usuario a buscar
     * @return Usuario encontrado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public User getUserByEmail(String email) {
        return storageService.findUserByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }
    
    /**
     * Obtiene todos los usuarios activos del sistema.
     * 
     * @return Lista de usuarios con estado activo
     */
    public List<User> getAllActiveUsers() {
        return storageService.findAllActiveUsers();
    }
    
    /**
     * Actualiza la información básica de un usuario.
     * Valida que el nuevo email no esté en uso por otro usuario.
     * 
     * @param id ID del usuario a actualizar
     * @param name Nuevo nombre del usuario
     * @param email Nuevo email del usuario
     * @return Usuario actualizado
     * @throws ResourceNotFoundException si el usuario no existe
     * @throws EmailAlreadyExistsException si el email ya está en uso
     */
    public User updateUser(Long id, String name, String email) {
        User user = getUserById(id);
        
        if (!user.getEmail().equals(email.toLowerCase()) && storageService.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("El email " + email + " ya está en uso");
        }
        
        user.setName(name.trim());
        user.setEmail(email.toLowerCase().trim());
        
        return storageService.saveUser(user);
    }
    
    /**
     * Cambia la contraseña de un usuario.
     * Valida que la contraseña actual sea correcta antes de cambiarla.
     * 
     * @param userId ID del usuario
     * @param currentPassword Contraseña actual del usuario
     * @param newPassword Nueva contraseña
     * @throws ResourceNotFoundException si el usuario no existe
     * @throws IllegalArgumentException si la contraseña actual es incorrecta o la nueva es muy corta
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (!user.getPassword().equals(currentPassword)) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        
        user.setPassword(newPassword);
        storageService.saveUser(user);
    }
    
    /**
     * Desactiva un usuario (soft delete).
     * El usuario permanece en el sistema pero marcado como inactivo.
     * 
     * @param id ID del usuario a desactivar
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(false);
        storageService.saveUser(user);
    }
    
    /**
     * Valida las credenciales de login de un usuario.
     * Verifica que el email exista, la contraseña sea correcta y el usuario esté activo.
     * 
     * @param email Email del usuario
     * @param password Contraseña proporcionada
     * @return true si las credenciales son válidas, false en caso contrario
     */
    public boolean validateCredentials(String email, String password) {
        Optional<User> userOpt = storageService.findUserByEmail(email.toLowerCase());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getIsActive() && user.getPassword().equals(password);
        }
        return false;
    }
    
    /**
     * Elimina completamente un usuario del sistema (hard delete).
     * Esta operación es irreversible.
     * 
     * @param id ID del usuario a eliminar
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public void deleteUser(Long id) {
        User user = getUserById(id);
        storageService.deleteUser(id);
    }
    
    /**
     * Crea una lista de tareas predeterminada para un nuevo usuario.
     * Esta lista se crea automáticamente al registrar un usuario.
     * 
     * @param user Usuario para el cual crear la lista predeterminada
     */
    private void createDefaultTaskList(User user) {
        TaskList defaultList = new TaskList();
        defaultList.setName("Mis Tareas");
        defaultList.setDescription("Lista principal de tareas");
        defaultList.setColor("#007ACC");
        defaultList.setUser(user);
        
        storageService.saveTaskList(defaultList);
        user.addTaskList(defaultList);
        storageService.saveUser(user);
    }
}