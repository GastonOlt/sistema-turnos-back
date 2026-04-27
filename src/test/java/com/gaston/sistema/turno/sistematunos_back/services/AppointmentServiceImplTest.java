package com.gaston.sistema.turno.sistematunos_back.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus;
import com.gaston.sistema.turno.sistematunos_back.entities.Client;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.repositories.AppointmentRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ClientRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopRepository;

/**
 * Tests for AppointmentServiceImpl — Sprint 3 booking rules.
 *
 * Key behaviors tested:
 * - bookAppointment: per-shop uniqueness rule (not global), employee/service shop validation
 * - bookAppointment: past date prevention, overlapping schedule detection
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private EmployeeService employeeService;
    @Mock private ShopOfferingService shopOfferingService;
    @Mock private ShopService shopService;
    @Mock private ClientService clientService;
    @Mock private EmailService emailService;
    @Mock private AvailabilityCalculatorService availabilityCalculatorService;
    @Mock private ShopRepository shopRepository;
    @Mock private ClientRepository clientRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Shop shop;
    private Employee employee;
    private ShopOffering service;
    private Client client;
    private AppointmentRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        shop = new Shop();
        shop.setId(10L);
        shop.setName("Test Shop");

        employee = new Employee();
        employee.setId(20L);
        employee.setName("Pedro");
        employee.setLastName("Emp");
        employee.setShop(shop);

        service = new ShopOffering();
        service.setId(30L);
        service.setName("Haircut");
        service.setDuration(30);
        service.setShop(shop);

        client = new Client();
        client.setId(40L);
        client.setName("Maria");

        validRequest = new AppointmentRequestDTO();
        validRequest.setEmployeeId(20L);
        validRequest.setServiceId(30L);
        validRequest.setShopId(10L);
        validRequest.setStartDateTime(LocalDateTime.now().plusDays(1));
    }

    // ========================================================================
    // bookAppointment — per-shop uniqueness (Sprint 3 fix)
    // ========================================================================

    @Nested
    @DisplayName("bookAppointment — per-shop uniqueness rule")
    class BookAppointmentPerShopTests {

        @Test
        @DisplayName("should reject booking when client already has active appointment at SAME shop")
        void shouldRejectWhenActiveAppointmentAtSameShop() {
            List<AppointmentStatus> statuses = Arrays.asList(AppointmentStatus.CONFIRMED, AppointmentStatus.PENDING);

            when(appointmentRepository.existsByClientIdAndShopIdAndStatusIn(40L, 10L, statuses)).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> appointmentService.bookAppointment(40L, validRequest));

            assertTrue(ex.getMessage().contains("turno activo en este local"));
        }

        @Test
        @DisplayName("should allow booking when client has active appointment at DIFFERENT shop")
        void shouldAllowBookingAtDifferentShop() {
            when(appointmentRepository.existsByClientIdAndShopIdAndStatusIn(eq(40L), eq(10L), anyList())).thenReturn(false);
            when(employeeService.getEmployeeEntity(20L)).thenReturn(employee);
            when(shopOfferingService.getServiceEntity(30L)).thenReturn(service);
            when(shopRepository.getReferenceById(10L)).thenReturn(shop);
            when(clientRepository.getReferenceById(40L)).thenReturn(client);
            when(appointmentRepository.existsByEmployeeAndOverlappingSchedule(eq(20L), any(), any())).thenReturn(false);
            when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment a = invocation.getArgument(0);
                a.setId(100L);
                return a;
            });

            AppointmentResponseDTO result = appointmentService.bookAppointment(40L, validRequest);

            assertNotNull(result);
            assertEquals("PENDING", result.getStatus());
        }
    }

    // ========================================================================
    // bookAppointment — validation rules
    // ========================================================================

    @Nested
    @DisplayName("bookAppointment — validation rules")
    class BookAppointmentValidationTests {

        @Test
        @DisplayName("should reject when employee does not belong to the shop")
        void shouldRejectWhenEmployeeNotInShop() {
            Shop otherShop = new Shop();
            otherShop.setId(99L);
            employee.setShop(otherShop);

            when(appointmentRepository.existsByClientIdAndShopIdAndStatusIn(eq(40L), eq(10L), anyList())).thenReturn(false);
            when(employeeService.getEmployeeEntity(20L)).thenReturn(employee);
            when(shopOfferingService.getServiceEntity(30L)).thenReturn(service);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> appointmentService.bookAppointment(40L, validRequest));

            assertTrue(ex.getMessage().contains("empleado no pertenece"));
        }

        @Test
        @DisplayName("should reject when service does not belong to the shop")
        void shouldRejectWhenServiceNotInShop() {
            Shop otherShop = new Shop();
            otherShop.setId(99L);
            service.setShop(otherShop);

            when(appointmentRepository.existsByClientIdAndShopIdAndStatusIn(eq(40L), eq(10L), anyList())).thenReturn(false);
            when(employeeService.getEmployeeEntity(20L)).thenReturn(employee);
            when(shopOfferingService.getServiceEntity(30L)).thenReturn(service);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> appointmentService.bookAppointment(40L, validRequest));

            assertTrue(ex.getMessage().contains("servicio no pertenece"));
        }

        @Test
        @DisplayName("should reject when start time is in the past")
        void shouldRejectWhenStartTimeInPast() {
            validRequest.setStartDateTime(LocalDateTime.now().minusDays(1));

            when(appointmentRepository.existsByClientIdAndShopIdAndStatusIn(eq(40L), eq(10L), anyList())).thenReturn(false);
            when(employeeService.getEmployeeEntity(20L)).thenReturn(employee);
            when(shopOfferingService.getServiceEntity(30L)).thenReturn(service);
            when(shopRepository.getReferenceById(10L)).thenReturn(shop);
            when(clientRepository.getReferenceById(40L)).thenReturn(client);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> appointmentService.bookAppointment(40L, validRequest));

            assertTrue(ex.getMessage().contains("pasado"));
        }

        @Test
        @DisplayName("should reject when employee slot is occupied")
        void shouldRejectWhenSlotOccupied() {
            when(appointmentRepository.existsByClientIdAndShopIdAndStatusIn(eq(40L), eq(10L), anyList())).thenReturn(false);
            when(employeeService.getEmployeeEntity(20L)).thenReturn(employee);
            when(shopOfferingService.getServiceEntity(30L)).thenReturn(service);
            when(shopRepository.getReferenceById(10L)).thenReturn(shop);
            when(clientRepository.getReferenceById(40L)).thenReturn(client);
            when(appointmentRepository.existsByEmployeeAndOverlappingSchedule(eq(20L), any(), any())).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> appointmentService.bookAppointment(40L, validRequest));

            assertTrue(ex.getMessage().contains("no est\u00e1 disponible"));
        }
    }
}
