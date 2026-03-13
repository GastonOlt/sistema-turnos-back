package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Local;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long>, JpaSpecificationExecutor<Local> {
    Optional<Local> findByDuenoId(Long duenoId);

    List<Local> findByProvincia(String provincia);

    List<Local> findByTipoLocal(String tipoLocal);

    List<Local> findByNombreStartingWithIgnoreCase(String nombre);

    @Query("SELECT l FROM Local l LEFT JOIN FETCH l.imagenes LEFT JOIN FETCH l.dueno WHERE l.id = :id")
    Optional<Local> findByIdConImagenes(@Param("id") Long id);

    @Query("SELECT DISTINCT l FROM Local l LEFT JOIN FETCH l.imagenes LEFT JOIN FETCH l.horarios LEFT JOIN FETCH l.servicios LEFT JOIN FETCH l.dueno WHERE l.id = :id")
    Optional<Local> findByIdConDetalles(@Param("id") Long id);
}
