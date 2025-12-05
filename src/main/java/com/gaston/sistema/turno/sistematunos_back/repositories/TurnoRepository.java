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
public interface TurnoRepository extends JpaRepository<Turno,Long> {

    boolean existsByClienteIdAndEstado(Long clienteId, EstadoTurno estado);
    List<Turno> findByEmpleadoIdAndEstadoIn(Long empleadoId, List<EstadoTurno> estados);
    List<Turno> findByEmpleadoIdAndEstado(Long empleadoId, EstadoTurno estado);
    
    @Query("SELECT COUNT(t) > 0 FROM Turno t WHERE t.empleado.id = :empleadoId AND " +
           "t.estado NOT IN (EstadoTurno.CANCELADO, EstadoTurno.FINALIZADO) AND " +
           "((t.fechaHoraInicio < :fin AND t.fechaHoraFin > :inicio))")
    boolean existsByEmpleadoAndHorarioSolapado(@Param("empleadoId") Long empleadoId, 
                                              @Param("inicio") LocalDateTime inicio, 
                                              @Param("fin") LocalDateTime fin);
    
}
