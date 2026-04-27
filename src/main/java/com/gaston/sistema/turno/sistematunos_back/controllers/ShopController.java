package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ShopDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ShopService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/owner/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping
    public ResponseEntity<ShopDTO> createShop(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody Shop shop) {
        Long ownerId = user.getId();
        ShopDTO newShop = shopService.createShop(shop, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newShop);
    }

    /** Returns the shop owned by the authenticated owner as a DTO (no JPA entity exposed). */
    @GetMapping
    public ResponseEntity<ShopDTO> getByOwner(@AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        ShopDTO shop = shopService.getByOwnerDTO(ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(shop);
    }

    @PutMapping
    public ResponseEntity<ShopDTO> editShop(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody Shop shop) {
        Long ownerId = user.getId();
        ShopDTO editedShop = shopService.editShop(shop, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(editedShop);
    }

    /** Returns shop by ID as a DTO. Prefer GET /public/shops/{id} for unauthenticated access. */
    @GetMapping("{id}")
    public ResponseEntity<ShopDTO> getShopById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(shopService.getShopDTOById(id));
    }
}
