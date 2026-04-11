package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.EmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.EmployeeService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/owner/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestPart("employee") Employee employee,
                        @AuthenticationPrincipal UserPrincipal user,
                        @RequestPart(value = "image", required = false) MultipartFile file) {
        Long ownerId = user.getId();
        EmployeeDTO newEmployee = employeeService.createEmployee(employee, ownerId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEmployee);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long employeeId, @AuthenticationPrincipal UserPrincipal user){
        Long ownerId = user.getId();
        employeeService.deleteEmployee(employeeId, ownerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping(value = "/{employeeId}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> editEmployee(@RequestPart("employee") Employee employee,
                        @RequestPart(value = "image", required = false) MultipartFile file,
                        @PathVariable Long employeeId,
                        @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        EmployeeDTO editedEmployee = employeeService.editEmployee(employee, file, employeeId, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(editedEmployee);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getEmployees(@AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        List<EmployeeDTO> employees = employeeService.getEmployees(ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(employees);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long employeeId, @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        EmployeeDTO employee = employeeService.getEmployee(employeeId, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(employee);
    }
}
