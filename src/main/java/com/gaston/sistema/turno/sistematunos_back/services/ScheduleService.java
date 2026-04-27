package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.dto.ScheduleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ScheduleRequestDTO;

public interface ScheduleService {
    ScheduleDTO createShopSchedule(ScheduleRequestDTO request, Long ownerId);
    ScheduleDTO createEmployeeSchedule(ScheduleRequestDTO request, Long employeeId);

    ScheduleDTO editShopSchedule(ScheduleRequestDTO request, Long scheduleId, Long ownerId);
    ScheduleDTO editEmployeeSchedule(ScheduleRequestDTO request, Long scheduleId, Long employeeId);

    ScheduleDTO getSchedule(Long scheduleId, Long ownerId);
    ScheduleDTO getEmployeeSchedule(Long scheduleId, Long employeeId);

    List<ScheduleDTO> getSchedules(Long ownerId);
    List<ScheduleDTO> getEmployeeSchedules(Long employeeId);

    void deleteShopSchedule(Long scheduleId, Long ownerId);
    void deleteEmployeeSchedule(Long scheduleId, Long employeeId);
}

