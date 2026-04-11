package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentEmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.EmployeeAppointmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/employee/appointments")
public class EmployeeAppointmentController {

    private EmployeeAppointmentService employeeAppointmentService;

    public EmployeeAppointmentController(EmployeeAppointmentService employeeAppointmentService) {
        this.employeeAppointmentService = employeeAppointmentService;
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
}
