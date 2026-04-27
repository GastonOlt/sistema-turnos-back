package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.ForgotPasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.ResetPasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.entities.PasswordResetToken;
import com.gaston.sistema.turno.sistematunos_back.entities.User;
import com.gaston.sistema.turno.sistematunos_back.repositories.PasswordResetTokenRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.UserRepository;

/**
 * Handles the two-step "forgot password" flow:
 *  1. forgotPassword: generates a UUID token, stores it with 1h expiry and sends it by email.
 *  2. resetPassword: validates the token (existence + expiry + password match), sets the new password.
 *
 * Security notes:
 *  - Even if the email is not found, we return a generic message to prevent user enumeration attacks.
 *  - Tokens are single-use: deleted immediately after a successful reset.
 *  - Only one active token per user is kept (old ones are deleted before creating a new one).
 */
@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final long EXPIRATION_HOURS = 1;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Step 1: User requests a password reset.
     * Sends a token to the registered email. Returns a generic message regardless of whether
     * the email exists to prevent user enumeration.
     */
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            // Delete any existing token for this user before creating a new one
            tokenRepository.deleteByUser(user);

            String rawToken = UUID.randomUUID().toString();

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setToken(rawToken);
            resetToken.setExpirationDate(Instant.now().plusSeconds(EXPIRATION_HOURS * 3600));
            tokenRepository.save(resetToken);

            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), rawToken);
            log.info("Password reset token generated for user id={}", user.getId());
        });
        // Generic response — do NOT reveal if email exists or not
    }

    /**
     * Step 2: User submits the token + new password.
     * Validates token existence, expiry and password confirmation before updating.
     * Deletes the token after successful use (single-use).
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (resetToken.getExpirationDate().isBefore(Instant.now())) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Reset token has expired. Please request a new one.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Single-use: delete token immediately after successful reset
        tokenRepository.delete(resetToken);
        log.info("Password successfully reset for user id={}", user.getId());
    }
}
