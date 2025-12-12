package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Resenia;

@Repository
public interface ReseniaRepository extends JpaRepository<Resenia,Long> {

    Optional<Resenia> findByLocalIdAndClienteId(Long localId, Long clienteId);
    Optional<Resenia> findByTurnoId(Long turnoId);
    List<Resenia> findByLocalIdOrderByFechaUltimaModificacionDesc(Long localId);

    @Query("SELECT AVG(r.calificacion) FROM Resenia r WHERE r.local.id = :localId")
    Double obtenerPromedioCalificacion(Long localId);
}
