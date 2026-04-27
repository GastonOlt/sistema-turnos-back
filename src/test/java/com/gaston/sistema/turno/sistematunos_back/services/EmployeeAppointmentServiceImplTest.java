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

import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus;
import com.gaston.sistema.turno.sistematunos_back.entities.Client;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.repositories.AppointmentRepository;

/**
 * Tests for EmployeeAppointmentServiceImpl — Sprint 3 cancellation with email.
 *
 * Key behaviors tested:
 * - cancelAppointment: ownership check, status update, email notification to client
 * - cancelAppointment: email failure does not rollback the cancellation
 */
@ExtendWith(MockitoExtension.class)
class EmployeeAppointmentServiceImplTest {

    @Mock private EmployeeService employeeService;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private EmployeeAppointmentServiceImpl employeeAppointmentService;

    private Employee employee;
    private Client client;
    private Shop shop;
    private ShopOffering service;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        shop = new Shop();
        shop.setId(10L);
        shop.setName("Test Shop");

        employee = new Employee();
        employee.setId(20L);
        employee.setName("Pedro");
        employee.setLastName("Barber");
        employee.setEmail("pedro@test.com");
        employee.setShop(shop);

        client = new Client();
        client.setId(30L);
        client.setName("Maria");
        client.setLastName("Client");
        client.setEmail("maria@test.com");

        service = new ShopOffering();
        service.setId(40L);
        service.setName("Haircut");

        appointment = new Appointment();
        appointment.setId(100L);
        appointment.setEmployee(employee);
        appointment.setClient(client);
        appointment.setService(service);
        appointment.setShop(shop);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setStartDateTime(LocalDateTime.now().plusDays(1));
    }

    // ========================================================================
    // cancelAppointment
    // ========================================================================

    @Nested
    @DisplayName("cancelAppointment")
    class CancelAppointmentTests {

        @Test
        @DisplayName("should cancel appointment and notify client via email")
        void shouldCancelAndNotifyClient() {
            when(appointmentRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(appointment));

            employeeAppointmentService.cancelAppointment(20L, 100L);

            assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
            verify(appointmentRepository).save(appointment);

            // Verify email sent TO THE CLIENT (not the employee)
            verify(emailService).sendCancellationNotification(
                    eq("maria@test.com"),   // client's email
                    eq("Maria"),             // client's name
                    any(LocalDateTime.class),
                    eq("Haircut"),            // service name
                    eq("Test Shop"),          // shop name
                    contains("Pedro"));      // cancelled by employee name
        }

        @Test
        @DisplayName("should throw when appointment not found")
        void shouldThrowWhenNotFound() {
            when(appointmentRepository.findByIdWithRelations(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> employeeAppointmentService.cancelAppointment(20L, 999L));
        }

        @Test
        @DisplayName("should throw when appointment does not belong to employee")
        void shouldThrowWhenNotOwner() {
            when(appointmentRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(appointment));

            // Employee 99L is not the owner of this appointment (owned by 20L)
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> employeeAppointmentService.cancelAppointment(99L, 100L));

            assertTrue(ex.getMessage().contains("does not belong"));
        }

        @Test
        @DisplayName("should still cancel even if email notification fails — no rollback")
        void shouldCancelEvenIfEmailFails() {
            when(appointmentRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(appointment));
            doThrow(new RuntimeException("SMTP error")).when(emailService)
                    .sendCancellationNotification(anyString(), anyString(), any(), anyString(), anyString(), anyString());

            // Should NOT throw — email failure must not break the cancellation
            assertDoesNotThrow(() -> employeeAppointmentService.cancelAppointment(20L, 100L));

            // Appointment should still be cancelled
            assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
            verify(appointmentRepository).save(appointment);
        }
    }
}
