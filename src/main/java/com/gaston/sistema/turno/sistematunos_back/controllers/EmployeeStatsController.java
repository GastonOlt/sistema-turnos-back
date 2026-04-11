package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.EmployeeAppointmentService;

@RestController
@RequestMapping("/employee/stats")
public class EmployeeStatsController {

    private final EmployeeAppointmentService employeeAppointmentService;

    public EmployeeStatsController(EmployeeAppointmentService employeeAppointmentService) {
        this.employeeAppointmentService = employeeAppointmentService;
    }

    @GetMapping("/earnings")
    public ResponseEntity<BigDecimal> getEmployeeEarnings(@AuthenticationPrincipal UserPrincipal user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Long employeeId = user.getId();
        BigDecimal earnings = employeeAppointmentService.calculateEarnings(employeeId, from, to);
        return ResponseEntity.status(HttpStatus.OK).body(earnings);
    }
}
