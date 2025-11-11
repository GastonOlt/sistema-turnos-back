package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Local;

@Repository
public interface LocalRepository  extends JpaRepository<Local,Long>, JpaSpecificationExecutor<Local>{        
    Optional<Local> findByDuenoId(Long duenoId);
    List<Local> findByProvincia(String provincia);
    List<Local> findByTipoLocal(String tipoLocal);
    List<Local> findByNombreStartingWithIgnoreCase(String nombre);
    
}
