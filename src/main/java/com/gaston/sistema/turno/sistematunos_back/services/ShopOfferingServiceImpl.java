package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.ShopOfferingDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ShopOfferingRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopOfferingRepository;
import com.gaston.sistema.turno.sistematunos_back.validation.ResourceNotFoundException;

@Service
public class ShopOfferingServiceImpl implements ShopOfferingService {

    private final ShopOfferingRepository shopOfferingRepository;
    private final ShopService shopService;

    public ShopOfferingServiceImpl(ShopOfferingRepository shopOfferingRepository, ShopService shopService) {
        this.shopOfferingRepository = shopOfferingRepository;
        this.shopService = shopService;
    }

    @Override
    @Transactional
    public ShopOfferingDTO createService(ShopOfferingRequestDTO request, Long ownerId) {
        Shop shopDb = shopService.getByOwner(ownerId);
        ShopOffering service = toEntity(request);
        service.setShop(shopDb);
        shopDb.getServices().add(service);
        return toDTO(shopOfferingRepository.save(service));
    }

    @Override
    @Transactional
    public ShopOfferingDTO editService(ShopOfferingRequestDTO request, Long serviceId, Long ownerId) {
        ShopOffering serviceDb = shopOfferingRepository.findById(serviceId).orElseThrow(() ->
                new ResourceNotFoundException("ShopOffering", serviceId));

        if (!serviceDb.getShop().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have permission to edit this service");
        }
        serviceDb.setName(request.getName());
        serviceDb.setDescription(request.getDescription());
        serviceDb.setPrice(request.getPrice());
        serviceDb.setDuration(request.getDuration());
        return toDTO(shopOfferingRepository.save(serviceDb));
    }

    @Override
    @Transactional(readOnly = true)
    public ShopOfferingDTO getService(Long serviceId, Long ownerId) {
        ShopOffering serviceDb = shopOfferingRepository.findById(serviceId).orElseThrow(() ->
                new ResourceNotFoundException("ShopOffering", serviceId));

        if (!serviceDb.getShop().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have permission to view this service");
        }
        return toDTO(serviceDb);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopOfferingDTO> getServices(Long ownerId) {
        Shop shopDb = shopService.getByOwner(ownerId);
        return shopOfferingRepository.findByShopId(shopDb.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public void deleteService(Long serviceId, Long ownerId) {
        ShopOffering serviceDb = shopOfferingRepository.findById(serviceId).orElseThrow(() ->
                new ResourceNotFoundException("ShopOffering", serviceId));

        if (!serviceDb.getShop().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have permission to delete this service");
        }
        shopOfferingRepository.delete(serviceDb);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopOffering getServiceEntity(Long serviceId) {
        return shopOfferingRepository.findById(serviceId).orElseThrow(() ->
                new ResourceNotFoundException("ShopOffering", serviceId));
    }

    // ===================== PRIVATE MAPPERS =====================

    private ShopOffering toEntity(ShopOfferingRequestDTO request) {
        ShopOffering entity = new ShopOffering();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setDuration(request.getDuration());
        entity.setPrice(request.getPrice());
        return entity;
    }

    private ShopOfferingDTO toDTO(ShopOffering entity) {
        ShopOfferingDTO dto = new ShopOfferingDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setDuration(entity.getDuration());
        dto.setPrice(entity.getPrice());
        return dto;
    }
}
