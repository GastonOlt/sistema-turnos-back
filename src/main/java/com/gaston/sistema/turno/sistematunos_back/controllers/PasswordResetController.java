package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ForgotPasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.ResetPasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.services.PasswordResetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/auth/password")
@Tag(name = "Authentication", description = "Password recovery flow (public endpoints).")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @Operation(
        summary = "Request password reset",
        description = "Sends a reset token to the provided email. Always returns 200 to prevent user enumeration."
    )
    @SecurityRequirements()
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request);
        // Generic message — same response regardless of whether the email exists
        return ResponseEntity.ok(Map.of("message",
                "If that email is registered, you will receive a password reset link shortly."));
    }

    @Operation(
        summary = "Reset password with token",
        description = "Validates the reset token and sets the new password. The token is single-use and expires in 1 hour."
    )
    @SecurityRequirements()
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password successfully reset. You can now log in with your new password."));
    }
}
