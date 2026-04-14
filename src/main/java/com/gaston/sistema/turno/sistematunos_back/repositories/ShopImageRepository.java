package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.ShopImage;

@Repository
public interface ShopImageRepository extends JpaRepository<ShopImage, Long> {
    void deleteAllByIdIn(List<Long> ids);
    List<ShopImage> findByShopId(Long shopId);
    long countByShopId(Long shopId);
}
