package com.gaston.sistema.turno.sistematunos_back.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gaston.sistema.turno.sistematunos_back.dto.ShopDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;

public interface ShopService {
        ShopDTO createShop(Shop shop, Long ownerId);
        ShopDTO editShop(Shop shop, Long ownerId);
        Shop getShopById(Long id);
        ShopDTO getShopDTOById(Long id);
        Shop getByOwner(Long ownerId);
        ShopDTO getByOwnerDTO(Long ownerId);
        Page<ShopDTO> getShops(String shopType, String province, String name, Pageable pageable);
}
