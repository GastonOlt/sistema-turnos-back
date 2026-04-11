package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

        @Query("Select u From User u Where u.email = ?1")
        Optional<Client> findByEmail(String email);
}
