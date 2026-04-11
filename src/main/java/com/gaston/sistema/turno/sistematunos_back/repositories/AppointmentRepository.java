package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

       boolean existsByClientIdAndStatus(Long clientId, AppointmentStatus status);
       boolean existsByClientIdAndStatusIn(Long clientId, List<AppointmentStatus> status);
       List<Appointment> findByEmployeeIdAndStatusIn(Long employeeId, List<AppointmentStatus> statuses);
       List<Appointment> findByEmployeeIdAndStatus(Long employeeId, AppointmentStatus status);

       List<Appointment> findAllByStatusAndEndDateTimeBefore(AppointmentStatus status, LocalDateTime date);

       List<Appointment> findByEmployeeIdAndStatusAndStartDateTimeBetween(Long employeeId, AppointmentStatus status,
                     LocalDateTime start, LocalDateTime end);

       List<Appointment> findByStatusAndStartDateTimeBetween(AppointmentStatus status, LocalDateTime start, LocalDateTime end);

       @Query("SELECT t FROM Appointment t WHERE t.employee.id = :employeeId " +
                     "AND t.status NOT IN (com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.COMPLETED, com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.CANCELLED) " +
                     "AND t.startDateTime BETWEEN :start AND :end")
       List<Appointment> findActiveAppointmentsByDate(@Param("employeeId") Long employeeId,
                     @Param("start") LocalDateTime start,
                     @Param("end") LocalDateTime end);

       @Query("SELECT COUNT(t) > 0 FROM Appointment t WHERE t.employee.id = :employeeId AND " +
                     "t.status NOT IN (com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.COMPLETED, com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.CANCELLED) AND " +
                     "((t.startDateTime < :end AND t.endDateTime > :start))")
       boolean existsByEmployeeAndOverlappingSchedule(@Param("employeeId") Long employeeId,
                     @Param("start") LocalDateTime start,
                     @Param("end") LocalDateTime end);

       @Query("SELECT t FROM Appointment t WHERE t.client.id = :clientId " +
                     "AND t.startDateTime > :currentDate " +
                     "AND t.status IN (com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.CONFIRMED, com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.PENDING)")
       List<Appointment> findActiveClientAppointments(@Param("clientId") Long clientId,
                     @Param("currentDate") LocalDateTime currentDate);

       @Query("SELECT t FROM Appointment t WHERE t.client.id = :clientId " +
                     "AND (t.startDateTime < :currentDate " +
                     "OR t.status IN (com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.COMPLETED, com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.CANCELLED))")
       List<Appointment> findClientHistory(@Param("clientId") Long clientId,
                     @Param("currentDate") LocalDateTime currentDate);
}
