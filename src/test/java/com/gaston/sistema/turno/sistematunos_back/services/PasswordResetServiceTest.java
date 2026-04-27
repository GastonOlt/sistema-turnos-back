package com.gaston.sistema.turno.sistematunos_back.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gaston.sistema.turno.sistematunos_back.dto.ForgotPasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.ResetPasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.entities.Client;
import com.gaston.sistema.turno.sistematunos_back.entities.PasswordResetToken;
import com.gaston.sistema.turno.sistematunos_back.repositories.PasswordResetTokenRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.UserRepository;

/**
 * Tests for PasswordResetService — Sprint 2 feature.
 *
 * Key behaviors tested:
 * - forgotPassword: user enumeration prevention, token generation, single active token per user
 * - resetPassword: token validation, expiry check, password match, single-use deletion
 */
@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private Client testUser;
    private ForgotPasswordRequest forgotRequest;

    @BeforeEach
    void setUp() {
        testUser = new Client();
        testUser.setId(1L);
        testUser.setName("Juan");
        testUser.setLastName("Test");
        testUser.setEmail("juan@test.com");
        testUser.setPassword("encodedOldPassword");
        testUser.setRole("CLIENTE");

        forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setEmail("juan@test.com");
    }

    // ========================================================================
    // forgotPassword
    // ========================================================================

    @Nested
    @DisplayName("forgotPassword")
    class ForgotPasswordTests {

        @Test
        @DisplayName("should generate token and send email when user exists")
        void shouldGenerateTokenAndSendEmail() {
            when(userRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(testUser));

            passwordResetService.forgotPassword(forgotRequest);

            // Should delete any existing token before creating a new one
            verify(tokenRepository).deleteByUser(testUser);

            // Should save a new token
            ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
            verify(tokenRepository).save(tokenCaptor.capture());

            PasswordResetToken savedToken = tokenCaptor.getValue();
            assertEquals(testUser, savedToken.getUser());
            assertNotNull(savedToken.getToken());
            assertTrue(savedToken.getExpirationDate().isAfter(Instant.now()));

            // Should send email
            verify(emailService).sendPasswordResetEmail(eq("juan@test.com"), eq("Juan"), anyString());
        }

        @Test
        @DisplayName("should NOT throw when email does not exist — prevents user enumeration")
        void shouldNotThrowWhenEmailDoesNotExist() {
            when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

            forgotRequest.setEmail("nonexistent@test.com");

            // Must NOT throw — generic response regardless of email existence
            assertDoesNotThrow(() -> passwordResetService.forgotPassword(forgotRequest));

            // Should NOT send email or create token
            verify(tokenRepository, never()).save(any());
            verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("should delete old token before creating new one — single active token per user")
        void shouldDeleteOldTokenBeforeCreatingNew() {
            when(userRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(testUser));

            passwordResetService.forgotPassword(forgotRequest);

            // Delete must happen BEFORE save
            var inOrder = inOrder(tokenRepository);
            inOrder.verify(tokenRepository).deleteByUser(testUser);
            inOrder.verify(tokenRepository).save(any(PasswordResetToken.class));
        }
    }

    // ========================================================================
    // resetPassword
    // ========================================================================

    @Nested
    @DisplayName("resetPassword")
    class ResetPasswordTests {

        private ResetPasswordRequest resetRequest;
        private PasswordResetToken validToken;

        @BeforeEach
        void setUp() {
            resetRequest = new ResetPasswordRequest();
            resetRequest.setToken("valid-uuid-token");
            resetRequest.setNewPassword("newSecurePass");
            resetRequest.setConfirmNewPassword("newSecurePass");

            validToken = new PasswordResetToken();
            validToken.setId(1L);
            validToken.setToken("valid-uuid-token");
            validToken.setUser(testUser);
            validToken.setExpirationDate(Instant.now().plusSeconds(3600)); // expires in 1h
        }

        @Test
        @DisplayName("should reset password and delete token on success")
        void shouldResetPasswordAndDeleteToken() {
            when(tokenRepository.findByToken("valid-uuid-token")).thenReturn(Optional.of(validToken));
            when(passwordEncoder.encode("newSecurePass")).thenReturn("encodedNewPass");

            passwordResetService.resetPassword(resetRequest);

            // Password should be updated
            verify(userRepository).save(testUser);
            assertEquals("encodedNewPass", testUser.getPassword());

            // Token should be deleted (single-use)
            verify(tokenRepository).delete(validToken);
        }

        @Test
        @DisplayName("should throw when passwords do not match")
        void shouldThrowWhenPasswordsDoNotMatch() {
            resetRequest.setConfirmNewPassword("differentPassword");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> passwordResetService.resetPassword(resetRequest));

            assertEquals("New password and confirmation do not match", ex.getMessage());
            verify(tokenRepository, never()).findByToken(anyString());
        }

        @Test
        @DisplayName("should throw when token is invalid")
        void shouldThrowWhenTokenIsInvalid() {
            when(tokenRepository.findByToken("valid-uuid-token")).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> passwordResetService.resetPassword(resetRequest));

            assertEquals("Invalid or expired reset token", ex.getMessage());
        }

        @Test
        @DisplayName("should throw and delete token when it is expired")
        void shouldThrowAndDeleteExpiredToken() {
            validToken.setExpirationDate(Instant.now().minusSeconds(60)); // expired 1 min ago
            when(tokenRepository.findByToken("valid-uuid-token")).thenReturn(Optional.of(validToken));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> passwordResetService.resetPassword(resetRequest));

            assertTrue(ex.getMessage().contains("expired"));
            // Expired token should be cleaned up
            verify(tokenRepository).delete(validToken);
            // Password should NOT be changed
            verify(userRepository, never()).save(any());
        }
    }
}
