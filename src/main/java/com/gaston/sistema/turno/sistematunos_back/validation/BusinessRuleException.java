package com.gaston.sistema.turno.sistematunos_back.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a business rule is violated during a user-triggered operation.
 * Examples: booking a slot that is already taken, trying to confirm a cancelled
 * appointment, or attempting an action outside business hours.
 *
 * Maps to HTTP 422 Unprocessable Entity — the request is syntactically valid
 * but cannot be processed due to domain/business constraints.
 *
 * Use this instead of IllegalArgumentException for domain-rule violations
 * to ensure correct HTTP semantics (422 vs 400).
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
