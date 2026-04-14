package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopOfferingRepository;

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
    public ShopOffering createService(ShopOffering service, Long ownerId) {
            Shop shopDb = shopService.getByOwner(ownerId);
            service.setShop(shopDb);
            shopDb.getServices().add(service);
            ShopOffering newService = shopOfferingRepository.save(service);
            return newService;
    }

    @Override
    @Transactional
    public ShopOffering editService(ShopOffering service, Long serviceId, Long ownerId) {
            ShopOffering serviceDb = shopOfferingRepository.findById(serviceId).orElseThrow(()->
                                         new IllegalArgumentException("error al encontar el servicio con id: "+serviceId));

            if(!serviceDb.getShop().getOwner().getId().equals(ownerId)){
                 throw new AccessDeniedException("No tienes permisos para editar este servicio");
            }
            serviceDb.setDescription(service.getDescription());
            serviceDb.setName(service.getName());
            serviceDb.setPrice(service.getPrice());
            serviceDb.setDuration(service.getDuration());
            return shopOfferingRepository.save(serviceDb);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopOffering getService(Long serviceId, Long ownerId) {
        ShopOffering serviceDb = shopOfferingRepository.findById(serviceId).orElseThrow(()->
                                new IllegalArgumentException("error al encontar el servicio con id: "+serviceId));

        if(!serviceDb.getShop().getOwner().getId().equals(ownerId)){
            throw new AccessDeniedException("no tienes permisos para ver este servicio");
        }
        return serviceDb;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopOffering> getServices(Long ownerId) {
         Shop shopDb = shopService.getByOwner(ownerId);
         return shopOfferingRepository.findByShopId(shopDb.getId());
    }

    @Override
    public void deleteService(Long serviceId, Long ownerId) {
        ShopOffering serviceDb = shopOfferingRepository.findById(serviceId).orElseThrow(()->
                                    new IllegalArgumentException("no se encontro el servicio con ese id: "+serviceId));
        if(!serviceDb.getShop().getOwner().getId().equals(ownerId)){
            throw new AccessDeniedException("no tienes permitido eliminar este servcio");
        }
        shopOfferingRepository.delete(serviceDb);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopOffering getServiceEntity(Long serviceId) {
           ShopOffering serviceDb = shopOfferingRepository.findById(serviceId).orElseThrow(()->
                                new IllegalArgumentException("error al encontar el servicio con id: "+serviceId));
        return serviceDb;
    }
}
