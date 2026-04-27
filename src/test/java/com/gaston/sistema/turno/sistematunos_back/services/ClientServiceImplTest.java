package com.gaston.sistema.turno.sistematunos_back.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus;
import com.gaston.sistema.turno.sistematunos_back.entities.Client;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.repositories.AppointmentRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ClientRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ReviewRepository;

/**
 * Tests for ClientServiceImpl — Sprint 2 (changePassword) + Sprint 3 (cancelAppointment).
 *
 * Key behaviors tested:
 * - changePassword: password mismatch, wrong current password, success
 * - cancelAppointment: ownership check, status check, 2-hour cancellation window, email notification
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock private ClientRepository clientRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setName("Maria");
        testClient.setLastName("Test");
        testClient.setEmail("maria@test.com");
        testClient.setPassword("encodedPassword");
        testClient.setRole("CLIENTE");
    }

    // ========================================================================
    // changePassword (Sprint 2)
    // ========================================================================

    @Nested
    @DisplayName("changePassword")
    class ChangePasswordTests {

        @Test
        @DisplayName("should change password successfully")
        void shouldChangePassword() {
            when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
            when(passwordEncoder.matches("currentPass", "encodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newPass123")).thenReturn("encodedNewPass");

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCurrentPassword("currentPass");
            request.setNewPassword("newPass123");
            request.setConfirmNewPassword("newPass123");

            clientService.changePassword(1L, request);

            assertEquals("encodedNewPass", testClient.getPassword());
            verify(clientRepository).save(testClient);
        }

        @Test
        @DisplayName("should throw when passwords do not match")
        void shouldThrowWhenPasswordsMismatch() {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCurrentPassword("currentPass");
            request.setNewPassword("pass1");
            request.setConfirmNewPassword("pass2");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> clientService.changePassword(1L, request));

            assertEquals("New password and confirmation do not match", ex.getMessage());
        }

        @Test
        @DisplayName("should throw when current password is incorrect")
        void shouldThrowWhenCurrentPasswordWrong() {
            when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
            when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setCurrentPassword("wrongPass");
            request.setNewPassword("newPass123");
            request.setConfirmNewPassword("newPass123");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> clientService.changePassword(1L, request));

            assertEquals("Current password is incorrect", ex.getMessage());
        }
    }

    // ========================================================================
    // cancelAppointment (Sprint 3)
    // ========================================================================

    @Nested
    @DisplayName("cancelAppointment")
    class CancelAppointmentTests {

        private Appointment appointment;
        private Employee employee;
        private Shop shop;
        private ShopOffering service;

        @BeforeEach
        void setUp() {
            shop = new Shop();
            shop.setId(10L);
            shop.setName("Test Shop");

            employee = new Employee();
            employee.setId(20L);
            employee.setName("Pedro");
            employee.setEmail("pedro@test.com");

            service = new ShopOffering();
            service.setId(30L);
            service.setName("Haircut");

            appointment = new Appointment();
            appointment.setId(100L);
            appointment.setClient(testClient);
            appointment.setEmployee(employee);
            appointment.setService(service);
            appointment.setShop(shop);
            appointment.setStatus(AppointmentStatus.PENDING);
            // 1 day in the future (well within the 2-hour window)
            appointment.setStartDateTime(LocalDateTime.now().plusDays(1));
        }

        @Test
        @DisplayName("should cancel appointment and notify employee")
        void shouldCancelAndNotify() {
            when(appointmentRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(appointment));

            clientService.cancelAppointment(1L, 100L);

            assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
            verify(appointmentRepository).save(appointment);
            verify(emailService).sendCancellationNotification(
                    eq("pedro@test.com"), eq("Pedro"), any(), eq("Haircut"), eq("Test Shop"), contains("Maria"));
        }

        @Test
        @DisplayName("should throw when appointment not found")
        void shouldThrowWhenAppointmentNotFound() {
            when(appointmentRepository.findByIdWithRelations(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> clientService.cancelAppointment(1L, 999L));
        }

        @Test
        @DisplayName("should throw when appointment does not belong to client")
        void shouldThrowWhenNotOwner() {
            Client otherClient = new Client();
            otherClient.setId(99L);
            appointment.setClient(otherClient);

            when(appointmentRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(appointment));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> clientService.cancelAppointment(1L, 100L));

            assertTrue(ex.getMessage().contains("no pertenece"));
        }

        @Test
        @DisplayName("should throw when appointment is already cancelled")
        void shouldThrowWhenAlreadyCancelled() {
            appointment.setStatus(AppointmentStatus.CANCELLED);
            when(appointmentRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(appointment));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> clientService.cancelAppointment(1L, 100L));

            assertTrue(ex.getMessage().contains("no puede cancelarse"));
        }

        @Test
        @DisplayName("should throw when appointment is already completed")
        void shouldThrowWhenCompleted() {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            when(appointmentRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(appointment));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> clientService.cancelAppointment(1L, 100L));

            assertTrue(ex.getMessage().contains("no puede cancelarse"));
        }

        @Test
        @DisplayName("should throw when cancelling less than 2 hours before appointment — cancellation window")
        void shouldThrowWhenWithinCancellationWindow() {
            // Set start time to 1 hour from now (within the 2-hour window)
            appointment.setStartDateTime(LocalDateTime.now().plusHours(1));
            when(appointmentRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(appointment));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> clientService.cancelAppointment(1L, 100L));

            assertTrue(ex.getMessage().contains("2 horas"));
            verify(appointmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("should allow cancelling appointment exactly at 2+ hours before")
        void shouldAllowCancellingOutsideWindow() {
            // Set start time to 3 hours from now (outside the window)
            appointment.setStartDateTime(LocalDateTime.now().plusHours(3));
            when(appointmentRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(appointment));

            clientService.cancelAppointment(1L, 100L);

            assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
            verify(appointmentRepository).save(appointment);
        }
    }
}
