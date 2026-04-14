package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByShopIdAndClientId(Long shopId, Long clientId);
    Optional<Review> findByAppointmentId(Long appointmentId);
    List<Review> findByShopIdOrderByLastModifiedDateDesc(Long shopId);

    @Query("SELECT r FROM Review r " +
           "JOIN FETCH r.client " +
           "JOIN FETCH r.appointment a " +
           "JOIN FETCH a.service " +
           "WHERE r.shop.id = :shopId " +
           "ORDER BY r.lastModifiedDate DESC")
    List<Review> findByShopIdWithRelations(@Param("shopId") Long shopId);

    // Batch load reviews for a set of appointment IDs — eliminates N+1 in history
    @Query("SELECT r FROM Review r JOIN FETCH r.appointment WHERE r.appointment.id IN :ids")
    List<Review> findByAppointmentIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.shop.id = :shopId")
    Double getAverageRating(Long shopId);
}
