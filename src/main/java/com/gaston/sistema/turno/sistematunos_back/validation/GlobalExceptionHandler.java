package com.gaston.sistema.turno.sistematunos_back.validation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
            @ExceptionHandler(MethodArgumentNotValidException.class)
            public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
    
                Map<String, String> errores = new HashMap<>();
    
                ex.getBindingResult().getFieldErrors().forEach(error -> 
                    errores.put(error.getField(), error.getDefaultMessage())
                );
                return ResponseEntity.badRequest().body(errores);
            }
    
            @ExceptionHandler(EmailExistenteException.class)
            public ResponseEntity<Map<String, String>> handleEmailExistente(EmailExistenteException ex) {
                return ResponseEntity.badRequest().body(Map.of("error",ex.getMessage()));
            }
    
            @ExceptionHandler(CredencialesInvalidasException.class)
            public ResponseEntity<Map<String, String>> handleCredencialesInvalidasException(CredencialesInvalidasException ex) {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
            }

            
            @ExceptionHandler(RuntimeException.class)
            public ResponseEntity<?> manejarRuntimeException(RuntimeException ex) {
                Map<String, String> error = new HashMap<>();
                error.put("error", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            @ExceptionHandler(IllegalArgumentException.class)
            public ResponseEntity<?> manejarIllegalArgumentException(IllegalArgumentException ex) {
                Map<String, String> error = new HashMap<>();
                error.put("error", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            @ExceptionHandler(Exception.class)
            public ResponseEntity<?> manejarException(Exception ex) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ocurrió un error inesperado: " + ex.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
              
            @ExceptionHandler(AccessDeniedException.class)
            public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }
}