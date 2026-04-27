package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.ChangePasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.EmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.UpdateEmployeeProfileRequest;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;

public interface EmployeeService {
    EmployeeDTO createEmployee(Employee employee, Long ownerId, MultipartFile file);
    EmployeeDTO editEmployee(Employee employee, MultipartFile file, Long employeeId, Long ownerId);
    void deleteEmployee(Long employeeId, Long ownerId);
    EmployeeDTO getEmployee(Long employeeId, Long ownerId);
    Employee getEmployeeEntity(Long employeeId);
    List<EmployeeDTO> getEmployees(Long ownerId);
    /** Allows an employee to update their own basic info (name, lastName, email, specialty). */
    EmployeeDTO updateOwnProfile(Long employeeId, UpdateEmployeeProfileRequest request);
    /** Allows an employee to change their own password after verifying the current one. */
    void changePassword(Long employeeId, ChangePasswordRequest request);
    /** Converts an Employee entity to its DTO representation. */
    EmployeeDTO toEmployeeDTO(Employee employee);
}
