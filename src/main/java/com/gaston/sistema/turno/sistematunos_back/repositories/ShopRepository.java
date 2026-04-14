package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>, JpaSpecificationExecutor<Shop> {
    Optional<Shop> findByOwnerId(Long ownerId);
    List<Shop> findByProvince(String province);
    List<Shop> findByShopType(String shopType);
    List<Shop> findByNameStartingWithIgnoreCase(String name);

    @Modifying
    @Query("UPDATE Shop s SET s.averageRating = :rating WHERE s.id = :shopId")
    void updateAverageRating(@Param("shopId") Long shopId, @Param("rating") Double rating);
}
