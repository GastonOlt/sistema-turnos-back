package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ChangePasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.EmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.UpdateEmployeeProfileRequest;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/employee/profile")
@Tag(name = "Employee - Profile", description = "Employee self-service profile management.")
public class EmployeeProfileController {

    private final EmployeeService employeeService;

    public EmployeeProfileController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Get own profile", description = "Returns the authenticated employee's current profile data.")
    @GetMapping
    public ResponseEntity<EmployeeDTO> getProfile(@AuthenticationPrincipal UserPrincipal user) {
        EmployeeDTO dto = toDTO(employeeService.getEmployeeEntity(user.getId()));
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Operation(summary = "Update own profile", description = "Allows the employee to update their name, last name, email and specialty.")
    @PutMapping
    public ResponseEntity<EmployeeDTO> updateProfile(@AuthenticationPrincipal UserPrincipal user,
                                                     @Valid @RequestBody UpdateEmployeeProfileRequest request) {
        EmployeeDTO updated = employeeService.updateOwnProfile(user.getId(), request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @Operation(summary = "Change password", description = "Requires the current password before setting the new one.")
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserPrincipal user,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        employeeService.changePassword(user.getId(), request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private EmployeeDTO toDTO(com.gaston.sistema.turno.sistematunos_back.entities.Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setRole(employee.getRole());
        dto.setSpecialty(employee.getSpecialty());
        if (employee.getEmployeeImage() != null) {
            dto.setImageUrl(employee.getEmployeeImage().getImageUrl());
        }
        return dto;
    }
}
