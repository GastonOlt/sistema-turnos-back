package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gaston.sistema.turno.sistematunos_back.entities.RefreshToken;
import com.gaston.sistema.turno.sistematunos_back.entities.Usuario;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    
    Optional<RefreshToken> findByToken(String token);
    void deleteByUsuario(Usuario usuario);
}
