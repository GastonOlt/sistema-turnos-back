package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long>{

        @Query("Select u From Usuario u Where u.email = ?1")
        Optional<Cliente> findByEmail(String email);
}
