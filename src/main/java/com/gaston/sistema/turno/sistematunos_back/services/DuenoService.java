package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Optional;


import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;

public interface DuenoService {
    Dueno crearDueno (Dueno dueno);
    Optional<Dueno> findByEmail(String email);
    Optional<Dueno> findById(Long id);
}
