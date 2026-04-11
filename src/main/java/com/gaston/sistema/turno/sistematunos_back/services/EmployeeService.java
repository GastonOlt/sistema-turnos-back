package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.EmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;

public interface EmployeeService {
    EmployeeDTO createEmployee(Employee employee, Long ownerId, MultipartFile file);
    EmployeeDTO editEmployee(Employee employee, MultipartFile file, Long employeeId, Long ownerId);
    void deleteEmployee(Long employeeId, Long ownerId);
    EmployeeDTO getEmployee(Long employeeId, Long ownerId);
    Employee getEmployeeEntity(Long employeeId);
    List<EmployeeDTO> getEmployees(Long ownerId);
}
