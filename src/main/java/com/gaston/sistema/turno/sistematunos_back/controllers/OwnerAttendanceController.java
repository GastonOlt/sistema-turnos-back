package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.AttendanceToggleRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.EmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.OwnerAttendanceService;

import jakarta.validation.Valid;

/**
 * Controller for managing the owner's ability to personally attend appointments.
 *
 * PUT /owner/attendance — toggles the ghost Employee profile on/off.
 */
@RestController
@RequestMapping("/owner/attendance")
public class OwnerAttendanceController {

    private final OwnerAttendanceService ownerAttendanceService;

    public OwnerAttendanceController(OwnerAttendanceService ownerAttendanceService) {
        this.ownerAttendanceService = ownerAttendanceService;
    }

    /**
     * Toggles the owner's attendance availability.
     *
     * @param user    authenticated owner principal
     * @param request JSON body with a single boolean field: { "available": true/false }
     * @return EmployeeDTO of the ghost profile (useful for frontend to display the owner as a provider)
     */
    @PutMapping
    public ResponseEntity<EmployeeDTO> toggleAttendance(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody AttendanceToggleRequest request) {
        Long ownerId = user.getId();
        EmployeeDTO ghostProfile = ownerAttendanceService.toggleOwnerAttendance(ownerId, request.isAvailable());
        return ResponseEntity.status(HttpStatus.OK).body(ghostProfile);
    }
}
