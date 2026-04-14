package com.gaston.sistema.turno.sistematunos_back.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private ShopOfferingService shopOfferingService;
    @Mock
    private ShopService shopService;
    @Mock
    private ClientService clientService;
    @Mock
    private EmailService emailService;
    @Mock
    private AvailabilityCalculatorService availabilityCalculatorService;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Shop shop;
    private Employee employee;
    private ShopOffering service;
    private Client clientRef;
    private Shop shopRef;

    @BeforeEach
    void setUp() {
        shop = new Shop();
        shop.setId(1L);
        shop.setName("Test Shop");

        employee = new Employee();
        employee.setId(10L);
        employee.setName("Emp");
        employee.setLastName("Test");
        employee.setShop(shop);

        service = new ShopOffering();
        service.setId(100L);
        service.setName("Haircut");
        service.setDuration(30);
        service.setShop(shop);

        clientRef = new Client();
        clientRef.setId(5L);

        shopRef = new Shop();
        shopRef.setId(1L);
        shopRef.setName("Test Shop");
    }

    @Test
    void bookAppointment_Success() {
        // Arrange
        Long clientId = 5L;
        AppointmentRequestDTO request = new AppointmentRequestDTO();
        request.setEmployeeId(10L);
        request.setServiceId(100L);
        request.setShopId(1L);
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        request.setStartDateTime(startTime);

        when(appointmentRepository.existsByClientIdAndStatusIn(clientId, Arrays.asList(AppointmentStatus.CONFIRMED, AppointmentStatus.PENDING)))
                .thenReturn(false);
        when(employeeService.getEmployeeEntity(10L)).thenReturn(employee);
        when(shopOfferingService.getServiceEntity(100L)).thenReturn(service);
        
        // Verificamos N+1 Logic: The service MUST use getReferenceById
        when(shopRepository.getReferenceById(1L)).thenReturn(shopRef);
        when(clientRepository.getReferenceById(5L)).thenReturn(clientRef);
        
        when(appointmentRepository.existsByEmployeeAndOverlappingSchedule(
                eq(10L), eq(startTime), eq(startTime.plusMinutes(30)))).thenReturn(false);

        Appointment savedAppointment = new Appointment();
        savedAppointment.setId(500L);
        savedAppointment.setClient(clientRef);
        savedAppointment.setEmployee(employee);
        savedAppointment.setService(service);
        savedAppointment.setShop(shopRef);
        savedAppointment.setStartDateTime(startTime);
        savedAppointment.setEndDateTime(startTime.plusMinutes(30));
        savedAppointment.setStatus(AppointmentStatus.PENDING);

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // Act
        AppointmentResponseDTO response = appointmentService.bookAppointment(clientId, request);

        // Assert
        assertThat(response.getId()).isEqualTo(500L);
        assertThat(response.getStatus()).isEqualTo("PENDING");

        // Verify that getReferenceById was indeed used instead of findById
        verify(shopRepository).getReferenceById(1L);
        verify(clientRepository).getReferenceById(5L);
        verify(shopRepository, never()).findById(any());
        verify(clientRepository, never()).findById(any());
    }

    @Test
    void bookAppointment_FailsIfOverlapping() {
        // Arrange
        Long clientId = 5L;
        AppointmentRequestDTO request = new AppointmentRequestDTO();
        request.setEmployeeId(10L);
        request.setServiceId(100L);
        request.setShopId(1L);
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        request.setStartDateTime(startTime);

        when(appointmentRepository.existsByClientIdAndStatusIn(clientId, Arrays.asList(AppointmentStatus.CONFIRMED, AppointmentStatus.PENDING)))
                .thenReturn(false);
        when(employeeService.getEmployeeEntity(10L)).thenReturn(employee);
        when(shopOfferingService.getServiceEntity(100L)).thenReturn(service);
        when(shopRepository.getReferenceById(1L)).thenReturn(shopRef);
        when(clientRepository.getReferenceById(5L)).thenReturn(clientRef);
        
        // Simulating overlapping schedule
        when(appointmentRepository.existsByEmployeeAndOverlappingSchedule(
                eq(10L), eq(startTime), eq(startTime.plusMinutes(30)))).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(clientId, request);
        });

        assertThat(exception.getMessage()).isEqualTo("El empleado no está disponible en ese horario");
        verify(appointmentRepository, never()).save(any());
    }
}
