package com.gaston.sistema.turno.sistematunos_back.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentEmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;

public interface EmployeeAppointmentService {
    List<AppointmentEmployeeDTO> listConfirmedAppointments(Long employeeId);
    List<AppointmentEmployeeDTO> listPendingAppointments(Long employeeId);
    List<AppointmentEmployeeDTO> appointmentHistory(Long employeeId);
    void cancelAppointment(Long employeeId, Long appointmentId);
    void confirmAppointment(Long employeeId, Long appointmentId);
    BigDecimal calculateEarnings(Long employeeId, LocalDate from, LocalDate to);
    List<ShopOffering> getServicesByEmployee(Long employeeId);
}
