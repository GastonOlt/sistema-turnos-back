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

       /** Checks if a client already has an active appointment in a SPECIFIC shop — prevents multiple bookings per shop. */
       boolean existsByClientIdAndShopIdAndStatusIn(Long clientId, Long shopId, List<AppointmentStatus> status);

       List<Appointment> findAllByStatusAndEndDateTimeBefore(AppointmentStatus status, LocalDateTime date);

       // ─── Availability check ───────────────────────────────────────────────────
       @Query("SELECT a FROM Appointment a WHERE a.employee.id = :employeeId " +
                     "AND a.status NOT IN (com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.COMPLETED, com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.CANCELLED) " +
                     "AND a.startDateTime BETWEEN :start AND :end")
       List<Appointment> findActiveAppointmentsByDate(@Param("employeeId") Long employeeId,
                     @Param("start") LocalDateTime start,
                     @Param("end") LocalDateTime end);

       @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.employee.id = :employeeId AND " +
                     "a.status NOT IN (com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.COMPLETED, com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.CANCELLED) AND " +
                     "((a.startDateTime < :end AND a.endDateTime > :start))")
       boolean existsByEmployeeAndOverlappingSchedule(@Param("employeeId") Long employeeId,
                     @Param("start") LocalDateTime start,
                     @Param("end") LocalDateTime end);

       // ─── Employee appointment listings (client + service pre-loaded) ──────────
       @Query("SELECT a FROM Appointment a " +
              "JOIN FETCH a.client " +
              "JOIN FETCH a.service " +
              "WHERE a.employee.id = :employeeId AND a.status = :status")
       List<Appointment> findByEmployeeIdAndStatusWithRelations(
              @Param("employeeId") Long employeeId,
              @Param("status") AppointmentStatus status);

       // ─── Employee earnings (only service.price needed) ────────────────────────
       @Query("SELECT a FROM Appointment a " +
              "JOIN FETCH a.service " +
              "WHERE a.employee.id = :employeeId AND a.status = :status " +
              "AND a.startDateTime BETWEEN :start AND :end")
       List<Appointment> findByEmployeeIdAndStatusAndDateRangeWithService(
              @Param("employeeId") Long employeeId,
              @Param("status") AppointmentStatus status,
              @Param("start") LocalDateTime start,
              @Param("end") LocalDateTime end);

       // ─── Client active appointments (shop + service pre-loaded) ──────────────
       @Query("SELECT a FROM Appointment a " +
              "JOIN FETCH a.shop " +
              "JOIN FETCH a.service " +
              "WHERE a.client.id = :clientId " +
              "AND a.startDateTime > :currentDate " +
              "AND a.status IN (com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.CONFIRMED, " +
              "com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.PENDING)")
       List<Appointment> findActiveClientAppointmentsWithRelations(
              @Param("clientId") Long clientId,
              @Param("currentDate") LocalDateTime currentDate);

       // ─── Client history (shop + service pre-loaded) ───────────────────────────
       @Query("SELECT a FROM Appointment a " +
              "JOIN FETCH a.shop " +
              "JOIN FETCH a.service " +
              "WHERE a.client.id = :clientId " +
              "AND (a.startDateTime < :currentDate " +
              "OR a.status IN (com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.COMPLETED, " +
              "com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus.CANCELLED))")
       List<Appointment> findClientHistoryWithRelations(
              @Param("clientId") Long clientId,
              @Param("currentDate") LocalDateTime currentDate);

       // ─── Email reminder (client + service + shop pre-loaded) ─────────────────
       @Query("SELECT a FROM Appointment a " +
              "JOIN FETCH a.client " +
              "JOIN FETCH a.service " +
              "JOIN FETCH a.shop " +
              "WHERE a.status = :status AND a.startDateTime BETWEEN :start AND :end")
       List<Appointment> findByStatusAndDateRangeWithRelations(
              @Param("status") AppointmentStatus status,
              @Param("start") LocalDateTime start,
              @Param("end") LocalDateTime end);
        // ─── Cancel with full relations loaded (avoids N+1 when sending emails) ─────
        @Query("SELECT a FROM Appointment a " +
               "JOIN FETCH a.client " +
               "JOIN FETCH a.employee " +
               "JOIN FETCH a.service " +
               "JOIN FETCH a.shop " +
               "WHERE a.id = :appointmentId")
        java.util.Optional<Appointment> findByIdWithRelations(@Param("appointmentId") Long appointmentId);
}
