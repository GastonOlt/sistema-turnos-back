package com.gaston.sistema.turno.sistematunos_back.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentEmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ShopOfferingDTO;

public interface EmployeeAppointmentService {
    List<AppointmentEmployeeDTO> listConfirmedAppointments(Long employeeId);
    List<AppointmentEmployeeDTO> listPendingAppointments(Long employeeId);
    List<AppointmentEmployeeDTO> appointmentHistory(Long employeeId);
    Page<AppointmentEmployeeDTO> appointmentHistoryPaged(Long employeeId, Pageable pageable);
    void cancelAppointment(Long employeeId, Long appointmentId);
    void confirmAppointment(Long employeeId, Long appointmentId);
    BigDecimal calculateEarnings(Long employeeId, LocalDate from, LocalDate to);
    List<ShopOfferingDTO> getServicesByEmployee(Long employeeId);
}
