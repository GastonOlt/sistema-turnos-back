package com.gaston.sistema.turno.sistematunos_back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Horario;

@Repository
public interface HorarioRepository extends JpaRepository<Horario,Long>{

}
