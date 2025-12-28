package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gaston.sistema.turno.sistematunos_back.entities.RefreshToken;
import com.gaston.sistema.turno.sistematunos_back.entities.Usuario;
import com.gaston.sistema.turno.sistematunos_back.repositories.RefreshTokenRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UsuarioRepository usuarioRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long usuarioId){
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(); 
        refreshTokenRepository.deleteByUsuario(usuario);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setFechaExpiracion(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    public Long getDurationInSeconds() {
        return refreshTokenDurationMs / 1000;
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getFechaExpiracion().isBefore(Instant.now())){
            refreshTokenRepository.delete(token);
            throw new RuntimeException("El Refresh Token ha expirado. Por favor inicie sesión de nuevo");
        }
        return token;
    }
}
