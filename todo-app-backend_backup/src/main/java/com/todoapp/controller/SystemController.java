package com.todoapp.controller;

import com.todoapp.service.MemoryStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para operaciones del sistema y utilidades.
 * Proporciona endpoints para obtener información del sistema, estadísticas,
 * estado de salud y operaciones de reinicio.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/system")
@CrossOrigin(origins = "*")
@Tag(name = "System", description = "🔧 Información del sistema y utilidades")
public class SystemController {
    
    @Autowired
    private MemoryStorageService storageService;
    
    /**
     * Obtiene información general del sistema incluyendo versión,
     * modo de almacenamiento y estadísticas básicas.
     * 
     * @return ResponseEntity con la información del sistema
     */
    @GetMapping("/info")
    @Operation(summary = "Información del sistema", description = "Obtiene información general del sistema")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("appName", "Todo App Backend");
        info.put("version", "1.0.0-SNAPSHOT");
        info.put("mode", "DEMO - In Memory Storage");
        info.put("timestamp", LocalDateTime.now());
        info.put("status", "Running");
        info.put("description", "API REST para gestión de tareas - Versión preliminar");
        info.put("storage", storageService.getStorageInfo());
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * Obtiene estadísticas generales del uso del sistema,
     * incluyendo información de almacenamiento y tiempo de actividad.
     * 
     * @return ResponseEntity con las estadísticas del sistema
     */
    @GetMapping("/stats")
    @Operation(summary = "Estadísticas generales", description = "Obtiene estadísticas del uso del sistema")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> stats = storageService.getStorageInfo();
        stats.put("timestamp", LocalDateTime.now());
        stats.put("uptime", "Desde el último reinicio");
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Reinicia todos los datos de demostración del sistema,
     * limpiando el almacenamiento en memoria y recreando datos de ejemplo.
     * 
     * @return ResponseEntity con mensaje de confirmación
     */
    @PostMapping("/reset-demo-data")
    @Operation(summary = "Reiniciar datos demo", description = "Reinicia los datos de demostración")
    public ResponseEntity<Map<String, String>> resetDemoData() {
        storageService.clearAllData();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Datos de demostración reiniciados exitosamente");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("note", "Se han recreado los datos de ejemplo");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Verifica el estado de salud del sistema y todos sus componentes.
     * Útil para monitoreo y verificación de disponibilidad.
     * 
     * @return ResponseEntity con el estado de salud del sistema
     */
    @GetMapping("/health")
    @Operation(summary = "Health check personalizado", description = "Verifica el estado del sistema")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("storage", "IN_MEMORY");
        health.put("components", Map.of(
            "memoryStorage", "UP",
            "controllers", "UP",
            "services", "UP"
        ));
        
        return ResponseEntity.ok(health);
    }
}