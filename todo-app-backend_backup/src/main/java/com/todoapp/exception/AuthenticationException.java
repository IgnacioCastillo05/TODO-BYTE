// AuthenticationException.java
package com.todoapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando las credenciales de autenticación son inválidas
 * Retorna HTTP 401 Unauthorized
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {
    
    private String email;
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String email, String message) {
        super(message);
        this.email = email;
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getEmail() {
        return email;
    }
}