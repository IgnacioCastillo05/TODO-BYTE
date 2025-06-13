// UnauthorizedAccessException.java
package com.todoapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un usuario intenta acceder a recursos que no le pertenecen
 * Retorna HTTP 403 Forbidden
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RuntimeException {
    
    private String resource;
    private Long userId;
    private Long resourceId;
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedAccessException(String resource, Long userId, Long resourceId) {
        super(String.format("Usuario %s no tiene permisos para acceder al recurso %s con ID: %s", 
                          userId, resource, resourceId));
        this.resource = resource;
        this.userId = userId;
        this.resourceId = resourceId;
    }
    
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getResource() {
        return resource;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Long getResourceId() {
        return resourceId;
    }
}