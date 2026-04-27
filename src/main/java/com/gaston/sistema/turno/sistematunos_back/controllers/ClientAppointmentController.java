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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.AvailableSlotDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentClientDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ClientService;
import com.gaston.sistema.turno.sistematunos_back.services.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/client/appointments")
@Tag(name = "Appointments - Client", description = "Booking and query operations for clients")
public class ClientAppointmentController {

    private final AppointmentService appointmentService;
    private final ClientService clientService;

    public ClientAppointmentController(AppointmentService appointmentService, ClientService clientService) {
        this.appointmentService = appointmentService;
        this.clientService = clientService;
    }

    @Operation(summary = "Book an appointment", description = "Registers an appointment if the slot is available. Requires CLIENT role.")
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(@Valid @RequestBody AppointmentRequestDTO request, @AuthenticationPrincipal UserPrincipal user) {
       Long clientId = user.getId();
       AppointmentResponseDTO appointment = appointmentService.bookAppointment(clientId, request);
       return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @Operation(summary = "Check availability", description = "Returns free time slots for a specific service and date.")
    @GetMapping("/availability")
    @SecurityRequirements()
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(@RequestParam Long shopId, @RequestParam Long employeeId, @RequestParam Long serviceId,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<AvailableSlotDTO> slots = appointmentService.getAvailableSlots(shopId, employeeId, serviceId, date);
        return ResponseEntity.status(HttpStatus.OK).body(slots);
    }

    @GetMapping("/active")
    public ResponseEntity<List<AppointmentClientDTO>> getActiveAppointments(@AuthenticationPrincipal UserPrincipal user) {
        Long clientId = user.getId();
        List<AppointmentClientDTO> appointments = clientService.getActiveAppointments(clientId);
        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AppointmentClientDTO>> getAppointmentHistory(@AuthenticationPrincipal UserPrincipal user) {
        Long clientId = user.getId();
        List<AppointmentClientDTO> appointments = clientService.getAppointmentHistory(clientId);
        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    /** Paginated appointment history — recommended for production use. Default page size: 10. */
    @GetMapping("/history/paged")
    public ResponseEntity<Page<AppointmentClientDTO>> getAppointmentHistoryPaged(
            @AuthenticationPrincipal UserPrincipal user,
            @PageableDefault(size = 10, sort = "startDateTime") Pageable pageable) {
        Long clientId = user.getId();
        Page<AppointmentClientDTO> appointments = clientService.getAppointmentHistoryPaged(clientId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @PatchMapping("/{appointmentId}/cancel")
        public ResponseEntity<Void> cancelAppointment(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long appointmentId) {
        Long clientId = user.getId();
        clientService.cancelAppointment(clientId, appointmentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
