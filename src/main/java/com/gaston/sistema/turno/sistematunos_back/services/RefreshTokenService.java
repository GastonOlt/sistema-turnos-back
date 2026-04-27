package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gaston.sistema.turno.sistematunos_back.entities.RefreshToken;
import com.gaston.sistema.turno.sistematunos_back.entities.User;
import com.gaston.sistema.turno.sistematunos_back.repositories.RefreshTokenRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId){
        User user = userRepository.findById(userId).orElseThrow();
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpirationDate(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    public Long getDurationInSeconds() {
        return refreshTokenDurationMs / 1000;
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpirationDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(token);
            throw new RuntimeException("El Refresh Token ha expirado. Por favor inicie sesión de nuevo");
        }
        return token;
    }

    /**
     * Deletes a RefreshToken from the database.
     * Used during logout to prevent reuse of a captured token.
     */
    @Transactional
    public void deleteByToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }
}
