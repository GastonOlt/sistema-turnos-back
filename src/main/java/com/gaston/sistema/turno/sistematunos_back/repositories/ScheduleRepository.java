package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByShopId(Long shopId);
    List<Schedule> findByEmployeeId(Long employeeId);
}
