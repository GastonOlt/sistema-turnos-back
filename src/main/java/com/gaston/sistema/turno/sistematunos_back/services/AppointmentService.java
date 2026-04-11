package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDate;
import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.dto.AvailableSlotDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentResponseDTO;

public interface AppointmentService {
    List<AvailableSlotDTO> getAvailableSlots(Long shopId, Long employeeId, Long serviceId, LocalDate date);
    List<AvailableSlotDTO> getAvailableSlots(Long employeeId, Long serviceId, LocalDate date);
    AppointmentResponseDTO bookAppointment(Long clientId, AppointmentRequestDTO request);
    AppointmentResponseDTO createEmployeeAppointment(Long employeeId, AppointmentRequestDTO request);
    void updateCompletedAppointments();
    void sendAppointmentReminder();
}
