package com.todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Todo App (Versión Preliminar).
 * 
 * Esta aplicación proporciona una API REST completa para la gestión de tareas
 * utilizando almacenamiento en memoria para simplicidad en el desarrollo.
 * 
 * Características principales:
 * - Gestión de usuarios con autenticación básica
 * - Listas de tareas organizadas por usuario
 * - Tareas con prioridades, fechas límite y estado de importancia
 * - API REST documentada con Swagger/OpenAPI
 * - Manejo global de errores y validaciones
 * - Almacenamiento en memoria thread-safe
 * 
 * @author TodoApp Team
 * @version 1.0.0-SNAPSHOT
 */
@SpringBootApplication
public class TodoAppApplication {

    /**
     * Punto de entrada principal de la aplicación.
     * Inicia el servidor Spring Boot y muestra información de configuración.
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(TodoAppApplication.class, args);
        
        System.out.println("\n" +
            "╔══════════════════════════════════════╗\n" +
            "║       TODO APP BACKEND (DEMO)       ║\n" +
            "║            ¡INICIADO!                ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "║  🌐 Servidor: http://localhost:8080   ║\n" +
            "║  📚 Swagger:  /swagger-ui.html       ║\n" +
            "║  ❤️  Health: /actuator/health        ║\n" +
            "║  📊 Info:    /actuator/info          ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "║  ⚠️  MODO: Almacenamiento en memoria  ║\n" +
            "║  👤 Usuario demo: demo@todoapp.com   ║\n" +
            "║  🔑 Password: demo123                ║\n" +
            "╚══════════════════════════════════════╝\n"
        );
    }
}