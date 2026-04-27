package com.gaston.sistema.turno.sistematunos_back.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gaston.sistema.turno.sistematunos_back.dto.ChangePasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.OwnerProfileDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Owner;
import com.gaston.sistema.turno.sistematunos_back.repositories.OwnerRepository;
import com.gaston.sistema.turno.sistematunos_back.validation.EmailAlreadyExistsException;

/**
 * Tests for OwnerServiceImpl — Sprint 2 profile management.
 *
 * Key behaviors tested:
 * - getProfile: returns DTO, throws when not found
 * - updateProfile: email uniqueness validation, data update
 * - changePassword: current password verification, password mismatch handling
 */
@ExtendWith(MockitoExtension.class)
class OwnerServiceImplTest {

    @Mock private OwnerRepository ownerRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    private Owner testOwner;

    @BeforeEach
    void setUp() {
        testOwner = new Owner();
        testOwner.setId(1L);
        testOwner.setName("Carlos");
        testOwner.setLastName("Owner");
        testOwner.setEmail("carlos@test.com");
        testOwner.setPassword("encodedPassword");
        testOwner.setRole("DUENO");
        testOwner.setAvailableToAttend(false);
    }

    // ========================================================================
    // getProfile
    // ========================================================================

    @Nested
    @DisplayName("getProfile")
    class GetProfileTests {

        @Test
        @DisplayName("should return OwnerProfileDTO when owner exists")
        void shouldReturnProfileDTO() {
            when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));

            OwnerProfileDTO result = ownerService.getProfile(1L);

            assertEquals(1L, result.getId());
            assertEquals("Carlos", result.getName());
            assertEquals("Owner", result.getLastName());
            assertEquals("carlos@test.com", result.getEmail());
            assertFalse(result.isAvailableToAttend());
        }

        @Test
        @DisplayName("should throw when owner not found")
        void shouldThrowWhenOwnerNotFound() {
            when(ownerRepository.findById(999L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> ownerService.getProfile(999L));

            assertTrue(ex.getMessage().contains("Owner not found"));
        }
    }

    // ========================================================================
    // updateProfile
    // ========================================================================

    @Nested
    @DisplayName("updateProfile")
    class UpdateProfileTests {

        @Test
        @DisplayName("should update profile fields successfully")
        void shouldUpdateProfile() {
            when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
            when(ownerRepository.save(any(Owner.class))).thenReturn(testOwner);

            OwnerProfileDTO updateDTO = new OwnerProfileDTO();
            updateDTO.setName("Carlos Updated");
            updateDTO.setLastName("Owner Updated");
            updateDTO.setEmail("carlos@test.com"); // same email — no conflict

            OwnerProfileDTO result = ownerService.updateProfile(1L, updateDTO);

            assertEquals("Carlos Updated", result.getName());
            verify(ownerRepository).save(testOwner);
        }

        @Test
        @DisplayName("should throw EmailAlreadyExistsException when email is taken by another user")
        void shouldThrowWhenEmailTaken() {
            Owner otherOwner = new Owner();
            otherOwner.setId(2L);
            otherOwner.setEmail("taken@test.com");

            when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
            when(ownerRepository.findByEmail("taken@test.com")).thenReturn(Optional.of(otherOwner));

            OwnerProfileDTO updateDTO = new OwnerProfileDTO();
            updateDTO.setName("Carlos");
            updateDTO.setLastName("Owner");
            updateDTO.setEmail("taken@test.com"); // different email, already taken

            assertThrows(EmailAlreadyExistsException.class,
                    () -> ownerService.updateProfile(1L, updateDTO));

            verify(ownerRepository, never()).save(any());
        }

        @Test
        @DisplayName("should allow keeping the same email without conflict")
        void shouldAllowSameEmail() {
            when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
            when(ownerRepository.save(any(Owner.class))).thenReturn(testOwner);

            OwnerProfileDTO updateDTO = new OwnerProfileDTO();
            updateDTO.setName("Carlos");
            updateDTO.setLastName("Owner");
            updateDTO.setEmail("carlos@test.com"); // same email

            assertDoesNotThrow(() -> ownerService.updateProfile(1L, updateDTO));
        }
    }

    // ========================================================================
    // changePassword
    // ========================================================================

    @Nested
    @DisplayName("changePassword")
    class ChangePasswordTests {

        @Test
        @DisplayName("should change password when current password matches")
        void shouldChangePassword() {
            when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
            when(passwordEncoder.matches("currentPass", "encodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newSecurePass")).thenReturn("encodedNewPass");

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCurrentPassword("currentPass");
            request.setNewPassword("newSecurePass");
            request.setConfirmNewPassword("newSecurePass");

            ownerService.changePassword(1L, request);

            assertEquals("encodedNewPass", testOwner.getPassword());
            verify(ownerRepository).save(testOwner);
        }

        @Test
        @DisplayName("should throw when new password and confirmation do not match")
        void shouldThrowWhenPasswordsMismatch() {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCurrentPassword("currentPass");
            request.setNewPassword("newPass1");
            request.setConfirmNewPassword("newPass2");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> ownerService.changePassword(1L, request));

            assertEquals("New password and confirmation do not match", ex.getMessage());
            verify(ownerRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when current password is incorrect")
        void shouldThrowWhenCurrentPasswordIncorrect() {
            when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));
            when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCurrentPassword("wrongPass");
            request.setNewPassword("newSecurePass");
            request.setConfirmNewPassword("newSecurePass");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> ownerService.changePassword(1L, request));

            assertEquals("Current password is incorrect", ex.getMessage());
        }
    }
}
