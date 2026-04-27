package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.dto.ShopOfferingDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ShopOfferingRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;

public interface ShopOfferingService {
    ShopOfferingDTO createService(ShopOfferingRequestDTO request, Long ownerId);
    ShopOfferingDTO editService(ShopOfferingRequestDTO request, Long serviceId, Long ownerId);
    ShopOfferingDTO getService(Long serviceId, Long ownerId);
    ShopOffering getServiceEntity(Long serviceId);
    List<ShopOfferingDTO> getServices(Long ownerId);
    void deleteService(Long serviceId, Long ownerId);
}
