package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;

public interface ScheduleService {
    Schedule createShopSchedule(Schedule schedule, Long ownerId);
    Schedule createBarberSchedule(Schedule schedule, Long barberId);

    Schedule editShopSchedule(Schedule schedule, Long scheduleId, Long ownerId);
    Schedule editBarberSchedule(Schedule schedule, Long scheduleId, Long barberId);

    Schedule getSchedule(Long scheduleId, Long ownerId);
    Schedule getBarberSchedule(Long scheduleId, Long barberId);

    List<Schedule> getSchedules(Long ownerId);
    List<Schedule> getBarberSchedules(Long barberId);

    void deleteShopSchedule(Long scheduleId, Long ownerId);
    void deleteBarberSchedule(Long scheduleId, Long barberId);
}
