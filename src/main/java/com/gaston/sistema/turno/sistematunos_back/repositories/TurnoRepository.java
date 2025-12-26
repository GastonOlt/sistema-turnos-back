package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Turno;
import com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

       boolean existsByClienteIdAndEstado(Long clienteId, EstadoTurno estado);
       boolean existsByClienteIdAndEstadoIn(Long clienteId, List<EstadoTurno> estado);
       List<Turno> findByEmpleadoIdAndEstadoIn(Long empleadoId, List<EstadoTurno> estados);
       List<Turno> findByEmpleadoIdAndEstado(Long empleadoId, EstadoTurno estado);

       // para enecontrar todos los turnos que esten confirmados antes de la fecha actual
       List<Turno> findAllByEstadoAndFechaHoraFinBefore(EstadoTurno estado, LocalDateTime fecha);

       // para traer todos los turnos de un empleado con estado finalizados entre un periodo , para calcular ganancias
       List<Turno> findByEmpleadoIdAndEstadoAndFechaHoraInicioBetween(Long empleadoId, EstadoTurno estado,
                     LocalDateTime inicio, LocalDateTime fin);

       // trear todos los turnos para enviar notificacion de recordatorio de turno por email
       List<Turno> findByEstadoAndFechaHoraInicioBetween(EstadoTurno estado, LocalDateTime inicio, LocalDateTime fin);

       // para la calculadora de disponibilidad , traer los turnos que no esten
       // cancelados o finalizados en una fecha determinada
       @Query("SELECT t FROM Turno t WHERE t.empleado.id = :empleadoId " +
                     "AND t.estado NOT IN (com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno.FINALIZADO, com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno.CANCELADO) " +
                     "AND t.fechaHoraInicio BETWEEN :inicio AND :fin")
       List<Turno> findTurnosActivosPorFecha(@Param("empleadoId") Long empleadoId,
                     @Param("inicio") LocalDateTime inicio,
                     @Param("fin") LocalDateTime fin);

       @Query("SELECT COUNT(t) > 0 FROM Turno t WHERE t.empleado.id = :empleadoId AND " +
                     "t.estado NOT IN (com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno.FINALIZADO, com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno.CANCELADO) AND " +
                     "((t.fechaHoraInicio < :fin AND t.fechaHoraFin > :inicio))")
       boolean existsByEmpleadoAndHorarioSolapado(@Param("empleadoId") Long empleadoId,
                     @Param("inicio") LocalDateTime inicio,
                     @Param("fin") LocalDateTime fin);


       // Para turnos Activos confirmados/pendientes
       @Query("SELECT t FROM Turno t WHERE t.cliente.id = :clienteId " +
                     "AND t.fechaHoraInicio > :fechaActual " +
                     "AND t.estado IN (com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno.CONFIRMADO, com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno.PENDIENTE)")
       List<Turno> buscarTurnosActivosCliente(@Param("clienteId") Long clienteId,
                     @Param("fechaActual") LocalDateTime fechaActual);

       // Para el Historial Finalizados/Cancelados
       @Query("SELECT t FROM Turno t WHERE t.cliente.id = :clienteId " +
                     "AND (t.fechaHoraInicio < :fechaActual " +
                     "OR t.estado IN (com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno.FINALIZADO, com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno.CANCELADO))")
       List<Turno> buscarHistorialCliente(@Param("clienteId") Long clienteId,
                     @Param("fechaActual") LocalDateTime fechaActual);
}
