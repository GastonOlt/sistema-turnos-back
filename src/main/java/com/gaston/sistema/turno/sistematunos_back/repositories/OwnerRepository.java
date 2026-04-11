package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    @Query("Select u From User u Where u.email = ?1")
    Optional<Owner> findByEmail(String email);
}
