package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;

@Repository
public interface ImagenLocalRepository extends JpaRepository<ImagenLocal,Long>{
    void deleteAllByIdIn(List<Long> ids);
    List<ImagenLocal> findByLocalId(Long localId);
}
