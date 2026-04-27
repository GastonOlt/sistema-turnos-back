package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.PasswordResetToken;
import com.gaston.sistema.turno.sistematunos_back.entities.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    /** Deletes all existing tokens for a user before creating a new one (prevents token accumulation). */
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.user = :user")
    void deleteByUser(User user);

    /** Purges expired tokens — can be called by a scheduled task for housekeeping. */
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expirationDate < :now")
    void deleteExpiredTokens(Instant now);
}
