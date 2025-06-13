package com.todoapp.service;

import com.todoapp.entity.User;
import com.todoapp.entity.TaskList;
import com.todoapp.entity.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servicio de almacenamiento en memoria para simular una base de datos.
 * Utiliza estructuras de datos thread-safe para garantizar la consistencia
 * en entornos concurrentes. Proporciona operaciones CRUD básicas para
 * todas las entidades del sistema.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
@Service
public class MemoryStorageService {
    
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, TaskList> taskLists = new ConcurrentHashMap<>();
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    
    private final AtomicLong userIdGenerator = new AtomicLong(1);
    private final AtomicLong taskListIdGenerator = new AtomicLong(1);
    private final AtomicLong taskIdGenerator = new AtomicLong(1);
    
    /**
     * Constructor que inicializa el servicio con datos de ejemplo.
     */
    public MemoryStorageService() {
        initializeSampleData();
    }
    
    /**
     * Guarda un usuario en el almacenamiento.
     * Si el usuario no tiene ID, se le asigna uno nuevo automáticamente.
     * 
     * @param user Usuario a guardar
     * @return Usuario guardado con ID asignado
     */
    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setId(userIdGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    /**
     * Busca un usuario por su ID.
     * 
     * @param id ID del usuario a buscar
     * @return Optional conteniendo el usuario si existe
     */
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    /**
     * Busca un usuario por su dirección de email.
     * La búsqueda es case-insensitive.
     * 
     * @param email Email del usuario a buscar
     * @return Optional conteniendo el usuario si existe
     */
    public Optional<User> findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
    
    /**
     * Obtiene todos los usuarios activos del sistema.
     * 
     * @return Lista de usuarios con estado activo
     */
    public List<User> findAllActiveUsers() {
        return users.values().stream()
                .filter(User::getIsActive)
                .toList();
    }
    
    /**
     * Verifica si existe un usuario con el email especificado.
     * 
     * @param email Email a verificar
     * @return true si el email ya existe, false en caso contrario
     */
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
    
    /**
     * Elimina un usuario del almacenamiento.
     * 
     * @param id ID del usuario a eliminar
     */
    public void deleteUser(Long id) {
        users.remove(id);
    }
    
    /**
     * Guarda una lista de tareas en el almacenamiento.
     * Si la lista no tiene ID, se le asigna uno nuevo automáticamente.
     * 
     * @param taskList Lista de tareas a guardar
     * @return Lista de tareas guardada con ID asignado
     */
    public TaskList saveTaskList(TaskList taskList) {
        if (taskList.getId() == null) {
            taskList.setId(taskListIdGenerator.getAndIncrement());
        }
        taskLists.put(taskList.getId(), taskList);
        return taskList;
    }
    
    /**
     * Busca una lista de tareas por su ID.
     * 
     * @param id ID de la lista a buscar
     * @return Optional conteniendo la lista si existe
     */
    public Optional<TaskList> findTaskListById(Long id) {
        return Optional.ofNullable(taskLists.get(id));
    }
    
    /**
     * Obtiene todas las listas de tareas activas de un usuario específico.
     * 
     * @param userId ID del usuario propietario
     * @return Lista de listas de tareas del usuario
     */
    public List<TaskList> findTaskListsByUserId(Long userId) {
        return taskLists.values().stream()
                .filter(list -> list.getUser().getId().equals(userId))
                .filter(TaskList::getIsActive)
                .toList();
    }
    
    /**
     * Elimina una lista de tareas del almacenamiento.
     * 
     * @param id ID de la lista a eliminar
     */
    public void deleteTaskList(Long id) {
        taskLists.remove(id);
    }
    
    /**
     * Guarda una tarea en el almacenamiento.
     * Si la tarea no tiene ID, se le asigna uno nuevo automáticamente.
     * 
     * @param task Tarea a guardar
     * @return Tarea guardada con ID asignado
     */
    public Task saveTask(Task task) {
        if (task.getId() == null) {
            task.setId(taskIdGenerator.getAndIncrement());
        }
        tasks.put(task.getId(), task);
        return task;
    }
    
    /**
     * Busca una tarea por su ID.
     * 
     * @param id ID de la tarea a buscar
     * @return Optional conteniendo la tarea si existe
     */
    public Optional<Task> findTaskById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }
    
    /**
     * Obtiene todas las tareas de una lista específica.
     * Las tareas se ordenan por fecha de creación descendente.
     * 
     * @param taskListId ID de la lista de tareas
     * @return Lista de tareas ordenadas por fecha de creación
     */
    public List<Task> findTasksByTaskListId(Long taskListId) {
        return tasks.values().stream()
                .filter(task -> task.getTaskList().getId().equals(taskListId))
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .toList();
    }
    
    /**
     * Obtiene todas las tareas pendientes de una lista específica.
     * Las tareas se ordenan por fecha límite, con las que no tienen fecha al final.
     * 
     * @param taskListId ID de la lista de tareas
     * @return Lista de tareas pendientes ordenadas por fecha límite
     */
    public List<Task> findPendingTasksByTaskListId(Long taskListId) {
        return tasks.values().stream()
                .filter(task -> task.getTaskList().getId().equals(taskListId))
                .filter(task -> !task.getCompleted())
                .sorted((t1, t2) -> {
                    if (t1.getDueDate() == null && t2.getDueDate() == null) return 0;
                    if (t1.getDueDate() == null) return 1;
                    if (t2.getDueDate() == null) return -1;
                    return t1.getDueDate().compareTo(t2.getDueDate());
                })
                .toList();
    }
    
    /**
     * Obtiene todas las tareas completadas de una lista específica.
     * Las tareas se ordenan por fecha de completado descendente.
     * 
     * @param taskListId ID de la lista de tareas
     * @return Lista de tareas completadas ordenadas por fecha de completado
     */
    public List<Task> findCompletedTasksByTaskListId(Long taskListId) {
        return tasks.values().stream()
                .filter(task -> task.getTaskList().getId().equals(taskListId))
                .filter(Task::getCompleted)
                .sorted((t1, t2) -> {
                    if (t1.getCompletedAt() == null && t2.getCompletedAt() == null) return 0;
                    if (t1.getCompletedAt() == null) return 1;
                    if (t2.getCompletedAt() == null) return -1;
                    return t2.getCompletedAt().compareTo(t1.getCompletedAt());
                })
                .toList();
    }
    
    /**
     * Obtiene todas las tareas importantes y pendientes de un usuario.
     * Las tareas se ordenan por fecha límite y luego por prioridad.
     * 
     * @param userId ID del usuario
     * @return Lista de tareas importantes pendientes
     */
    public List<Task> findImportantTasksByUserId(Long userId) {
        return tasks.values().stream()
                .filter(task -> task.getTaskList().getUser().getId().equals(userId))
                .filter(Task::getIsImportant)
                .filter(task -> !task.getCompleted())
                .sorted((t1, t2) -> {
                    if (t1.getDueDate() == null && t2.getDueDate() == null) {
                        return t2.getPriority().compareTo(t1.getPriority());
                    }
                    if (t1.getDueDate() == null) return 1;
                    if (t2.getDueDate() == null) return -1;
                    return t1.getDueDate().compareTo(t2.getDueDate());
                })
                .toList();
    }
    
    /**
     * Elimina una tarea del almacenamiento.
     * 
     * @param id ID de la tarea a eliminar
     */
    public void deleteTask(Long id) {
        tasks.remove(id);
    }
    
    /**
     * Cuenta el número de usuarios activos en el sistema.
     * 
     * @return Número de usuarios activos
     */
    public long countActiveUsers() {
        return users.values().stream()
                .filter(User::getIsActive)
                .count();
    }
    
    /**
     * Cuenta el número de listas de tareas activas de un usuario.
     * 
     * @param userId ID del usuario
     * @return Número de listas activas del usuario
     */
    public long countTaskListsByUserId(Long userId) {
        return taskLists.values().stream()
                .filter(list -> list.getUser().getId().equals(userId))
                .filter(TaskList::getIsActive)
                .count();
    }
    
    /**
     * Busca tareas por contenido (título o descripción) para un usuario específico.
     * La búsqueda es case-insensitive y busca coincidencias parciales.
     * 
     * @param userId ID del usuario
     * @param searchTerm Término de búsqueda
     * @return Lista de tareas que coinciden con el término de búsqueda
     */
    public List<Task> searchTasksByContent(Long userId, String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return tasks.values().stream()
                .filter(task -> task.getTaskList().getUser().getId().equals(userId))
                .filter(task -> 
                    task.getTitle().toLowerCase().contains(lowerSearchTerm) ||
                    (task.getDescription() != null && task.getDescription().toLowerCase().contains(lowerSearchTerm)))
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .toList();
    }
    
    /**
     * Obtiene todas las tareas de todas las listas de un usuario.
     * Las tareas se ordenan por fecha de creación descendente.
     * 
     * @param userId ID del usuario
     * @return Lista de todas las tareas del usuario
     */
    public List<Task> findAllTasksByUserId(Long userId) {
        return tasks.values().stream()
                .filter(task -> task.getTaskList().getUser().getId().equals(userId))
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .toList();
    }
    
    /**
     * Inicializa el sistema con datos de ejemplo para facilitar las pruebas.
     * Crea un usuario demo, una lista de ejemplo y varias tareas de muestra.
     */
    private void initializeSampleData() {
        User sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("demo@todoapp.com");
        sampleUser.setName("Usuario Demo");
        sampleUser.setPassword("demo123");
        users.put(1L, sampleUser);
        
        TaskList sampleList = new TaskList();
        sampleList.setId(1L);
        sampleList.setName("Mis Tareas");
        sampleList.setDescription("Lista principal de tareas");
        sampleList.setColor("#007ACC");
        sampleList.setUser(sampleUser);
        taskLists.put(1L, sampleList);
        
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Probar la API");
        task1.setDescription("Verificar que todos los endpoints funcionen correctamente");
        task1.setPriority(com.todoapp.entity.Priority.HIGH);
        task1.setIsImportant(true);
        task1.setTaskList(sampleList);
        tasks.put(1L, task1);
        
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Documentar el proyecto");
        task2.setDescription("Crear documentación para la entrega");
        task2.setPriority(com.todoapp.entity.Priority.MEDIUM);
        task2.setIsImportant(false);
        task2.setTaskList(sampleList);
        tasks.put(2L, task2);
        
        Task task3 = new Task();
        task3.setId(3L);
        task3.setTitle("Revisar código");
        task3.setDescription("Code review antes de la entrega");
        task3.setPriority(com.todoapp.entity.Priority.LOW);
        task3.setCompleted(true);
        task3.setTaskList(sampleList);
        tasks.put(3L, task3);
        
        userIdGenerator.set(2L);
        taskListIdGenerator.set(2L);
        taskIdGenerator.set(4L);
    }
    
    /**
     * Obtiene información estadística del almacenamiento para debugging y monitoreo.
     * 
     * @return Mapa con estadísticas del almacenamiento
     */
    public Map<String, Object> getStorageInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("totalUsers", users.size());
        info.put("totalTaskLists", taskLists.size());
        info.put("totalTasks", tasks.size());
        info.put("nextUserId", userIdGenerator.get());
        info.put("nextTaskListId", taskListIdGenerator.get());
        info.put("nextTaskId", taskIdGenerator.get());
        return info;
    }
    
    /**
     * Limpia todos los datos del almacenamiento y reinicia los generadores de ID.
     * Útil para pruebas y reinicio del sistema demo.
     */
    public void clearAllData() {
        users.clear();
        taskLists.clear();
        tasks.clear();
        userIdGenerator.set(1L);
        taskListIdGenerator.set(1L);
        taskIdGenerator.set(1L);
    }
}