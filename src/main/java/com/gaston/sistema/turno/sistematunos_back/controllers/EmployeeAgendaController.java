package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.AvailableSlotDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.EmployeeAppointmentService;
import com.gaston.sistema.turno.sistematunos_back.services.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/employee/agenda")
@Tag(name = "Appointments - Employee", description = "Booking and query operations for employees")
public class EmployeeAgendaController {

    private final AppointmentService appointmentService;
    private final EmployeeAppointmentService employeeAppointmentService;

    public EmployeeAgendaController(AppointmentService appointmentService, EmployeeAppointmentService employeeAppointmentService) {
        this.appointmentService = appointmentService;
        this.employeeAppointmentService = employeeAppointmentService;
    }

    @Operation(summary = "Create an appointment as employee", description = "Registers an appointment for the authenticated employee if the slot is available.")
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createEmployeeAppointment(@AuthenticationPrincipal UserPrincipal user, @RequestBody AppointmentRequestDTO request) {
        Long employeeId = user.getId();
        AppointmentResponseDTO newAppointment = appointmentService.createEmployeeAppointment(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAppointment);
    }

    @Operation(summary = "Check availability", description = "Returns free time slots for a specific service and date.")
    @GetMapping("/availability")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(@AuthenticationPrincipal UserPrincipal user, @RequestParam Long serviceId,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long employeeId = user.getId();
        List<AvailableSlotDTO> availableSlots = appointmentService.getAvailableSlots(employeeId, serviceId, date);
        return ResponseEntity.status(HttpStatus.OK).body(availableSlots);
    }

    @GetMapping("/my-services")
    public ResponseEntity<List<ShopOffering>> getMyServices(@AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        List<ShopOffering> services = employeeAppointmentService.getServicesByEmployee(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(services);
    }
}
