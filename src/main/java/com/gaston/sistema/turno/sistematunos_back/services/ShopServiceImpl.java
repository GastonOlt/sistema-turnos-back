package com.gaston.sistema.turno.sistematunos_back.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.ShopDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Owner;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopRepository;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final OwnerServiceImpl ownerService;

    public ShopServiceImpl(ShopRepository shopRepository, OwnerServiceImpl ownerService) {
        this.shopRepository = shopRepository;
        this.ownerService = ownerService;
    }

    @Override
    @Transactional
    public ShopDTO createShop(Shop shop, Long ownerId) {
            Owner owner = ownerService.findById(ownerId).orElseThrow( ()->
                                        new IllegalArgumentException("Dueno no encontrado"));
            owner.setShop(shop);
            shop.setOwner(owner);
            Shop newShop = shopRepository.save(shop);
            return new ShopDTO(newShop);
    }

    @Override
    @Transactional
    public ShopDTO editShop(Shop shop, Long ownerId) {
        Shop shopDb = getByOwner(ownerId);
        shopDb.updateShopData(shop);
        Shop updatedShop = shopRepository.save(shopDb);
        return new ShopDTO(updatedShop);
    }

    @Override
    @Transactional(readOnly = true)
    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Local no encontrado con ese id "+id));
    }

    @Override
    @Transactional(readOnly = true)
    public Shop getByOwner(Long ownerId) {
      return shopRepository.findByOwnerId(ownerId).orElseThrow(() ->
                                new IllegalArgumentException("local no econtrado con este Id de dueño: "+ownerId));
    }

    @Override
    public Page<ShopDTO> getShops(
        String shopType, String province, String name, Pageable pageable) {

        if (shopType == null && province == null && name == null) {
            return shopRepository.findAll(pageable)
                           .map(this::convertToDTO);
        }

        Specification<Shop> spec = Specification.allOf();

        if (shopType != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("shopType"), shopType));
        }

        if (province != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("province"), province));
        }

        if (name != null) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

       return shopRepository.findAll(spec, pageable)
                .map(this::convertToDTO);
    }

    private ShopDTO convertToDTO(Shop shop) {
             return new ShopDTO(shop);
    }
}
