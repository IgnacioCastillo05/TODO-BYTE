// InvalidDataException.java
package com.todoapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando los datos proporcionados no son válidos
 * Retorna HTTP 400 Bad Request
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidDataException extends RuntimeException {
    
    private String field;
    private Object value;
    private String reason;
    
    public InvalidDataException(String message) {
        super(message);
    }
    
    public InvalidDataException(String field, Object value, String reason) {
        super(String.format("Datos inválidos en campo '%s' con valor '%s': %s", field, value, reason));
        this.field = field;
        this.value = value;
        this.reason = reason;
    }
    
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
    
    public String getReason() {
        return reason;
    }
}