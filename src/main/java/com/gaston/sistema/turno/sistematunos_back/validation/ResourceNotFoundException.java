package com.gaston.sistema.turno.sistematunos_back.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource is not found in the system.
 * Maps to HTTP 404 NOT FOUND via GlobalExceptionHandler.
 *
 * Use this instead of IllegalArgumentException for "entity not found" cases
 * to ensure correct HTTP semantics (404 vs 400).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }
}
