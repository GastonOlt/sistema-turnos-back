package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;

public interface ShopOfferingService {
    ShopOffering createService(ShopOffering service, Long ownerId);
    ShopOffering editService(ShopOffering service, Long serviceId, Long ownerId);
    ShopOffering getService(Long serviceId, Long ownerId);
    ShopOffering getServiceEntity(Long serviceId);
    List<ShopOffering> getServices(Long ownerId);
    void deleteService(Long serviceId, Long ownerId);
}
