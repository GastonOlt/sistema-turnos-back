package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentEmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AvailableSlotDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.AppointmentService;
import com.gaston.sistema.turno.sistematunos_back.services.EmployeeAppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/employee/appointments")
@Tag(name = "Appointments - Employee", description = "Booking and query operations for employees")
public class EmployeeAppointmentController {

    private final EmployeeAppointmentService employeeAppointmentService;
    private final AppointmentService appointmentService;

    public EmployeeAppointmentController(EmployeeAppointmentService employeeAppointmentService, AppointmentService appointmentService) {
        this.employeeAppointmentService = employeeAppointmentService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/confirmed")
    public ResponseEntity<List<AppointmentEmployeeDTO>> getConfirmedAppointments(
            @AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        List<AppointmentEmployeeDTO> appointments = employeeAppointmentService.listConfirmedAppointments(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<AppointmentEmployeeDTO>> getPendingAppointments(
            @AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        List<AppointmentEmployeeDTO> appointments = employeeAppointmentService.listPendingAppointments(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AppointmentEmployeeDTO>> getAppointmentHistory(
            @AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        List<AppointmentEmployeeDTO> history = employeeAppointmentService.appointmentHistory(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(history);
    }

    /** Paginated appointment history — recommended for production use. Default page size: 10. */
    @GetMapping("/history/paged")
    public ResponseEntity<Page<AppointmentEmployeeDTO>> getAppointmentHistoryPaged(
            @AuthenticationPrincipal UserPrincipal user,
            @PageableDefault(size = 10, sort = "startDateTime") Pageable pageable) {
        Long employeeId = user.getId();
        Page<AppointmentEmployeeDTO> history = employeeAppointmentService.appointmentHistoryPaged(employeeId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(history);
    }

    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<Void> cancelAppointment(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long appointmentId) {
        Long employeeId = user.getId();
        employeeAppointmentService.cancelAppointment(employeeId, appointmentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{appointmentId}/confirm")
    public ResponseEntity<Void> confirmAppointment(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long appointmentId) {
        Long employeeId = user.getId();
        employeeAppointmentService.confirmAppointment(employeeId, appointmentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Create an appointment as employee", description = "Registers an appointment for the authenticated employee if the slot is available.")
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createEmployeeAppointment(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody AppointmentRequestDTO request) {
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
