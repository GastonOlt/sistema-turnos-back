package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado,Long>{

    @Query("Select u From Usuario u where u.email = ?1")
    Optional<Empleado> findByEmail(String email);

    List<Empleado> findByLocalId(Long localId);
}
