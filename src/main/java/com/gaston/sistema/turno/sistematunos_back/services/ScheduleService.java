package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;

public interface ScheduleService {
    Schedule createShopSchedule(Schedule schedule, Long ownerId);
    Schedule createEmployeeSchedule(Schedule schedule, Long employeeId);

    Schedule editShopSchedule(Schedule schedule, Long scheduleId, Long ownerId);
    Schedule editEmployeeSchedule(Schedule schedule, Long scheduleId, Long employeeId);

    Schedule getSchedule(Long scheduleId, Long ownerId);
    Schedule getEmployeeSchedule(Long scheduleId, Long employeeId);

    List<Schedule> getSchedules(Long ownerId);
    List<Schedule> getEmployeeSchedules(Long employeeId);

    void deleteShopSchedule(Long scheduleId, Long ownerId);
    void deleteEmployeeSchedule(Long scheduleId, Long employeeId);
}
