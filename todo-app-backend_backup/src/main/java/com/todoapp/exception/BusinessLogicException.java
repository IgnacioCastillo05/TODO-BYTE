// BusinessLogicException.java
package com.todoapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando se viola una regla de negocio
 * Retorna HTTP 422 Unprocessable Entity
 */
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class BusinessLogicException extends RuntimeException {
    
    private String operation;
    private String rule;
    
    public BusinessLogicException(String message) {
        super(message);
    }
    
    public BusinessLogicException(String operation, String rule, String message) {
        super(message);
        this.operation = operation;
        this.rule = rule;
    }
    
    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getRule() {
        return rule;
    }
}