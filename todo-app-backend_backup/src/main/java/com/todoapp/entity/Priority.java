package com.todoapp.entity;

/**
 * Enumeración que define los niveles de prioridad para las tareas.
 * Cada prioridad incluye un nombre para mostrar y un color asociado
 * para la representación visual en la interfaz de usuario.
 * 
 * @author TodoApp Team
 * @version 1.0.0
 */
public enum Priority {
    /**
     * Prioridad baja - Para tareas que pueden realizarse cuando haya tiempo disponible.
     */
    LOW("Baja", "#28a745"),
    
    /**
     * Prioridad media - Para tareas de importancia normal.
     */
    MEDIUM("Media", "#ffc107"),
    
    /**
     * Prioridad alta - Para tareas importantes que requieren atención pronto.
     */
    HIGH("Alta", "#fd7e14"),
    
    /**
     * Prioridad urgente - Para tareas críticas que requieren atención inmediata.
     */
    URGENT("Urgente", "#dc3545");
    
    private final String displayName;
    private final String color;
    
    /**
     * Constructor privado para inicializar las propiedades de cada prioridad.
     * 
     * @param displayName Nombre legible para mostrar al usuario
     * @param color Código de color hexadecimal para la representación visual
     */
    Priority(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }
    
    /**
     * Obtiene el nombre legible de la prioridad.
     * 
     * @return String con el nombre para mostrar al usuario
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Obtiene el código de color hexadecimal asociado a la prioridad.
     * 
     * @return String con el código de color en formato hexadecimal
     */
    public String getColor() {
        return color;
    }
}