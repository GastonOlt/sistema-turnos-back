package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ShopDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopImage;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.services.ShopImageService;
import com.gaston.sistema.turno.sistematunos_back.services.ShopService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/public/shops")
public class PublicShopController {

    private final ShopService shopService;
    private final ShopImageService shopImageService;

    public PublicShopController(ShopService shopService, ShopImageService shopImageService) {
        this.shopService = shopService;
        this.shopImageService = shopImageService;
    }

    @GetMapping("{id}")
    @SecurityRequirements()
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {
            Shop shopDb = shopService.getShopById(id);
            return ResponseEntity.status(HttpStatus.OK).body(shopDb);
    }

    @GetMapping
    @SecurityRequirements()
    public ResponseEntity<Page<ShopDTO>> getShopsByTypeOrProvinceOrName(
                                                      @RequestParam(required = false) String tipoLocal,
                                                      @RequestParam(required = false) String provincia,
                                                      @RequestParam(required = false) String nombre,
                                                      @PageableDefault(page = 0, size = 10) Pageable pageable) {

       Page<ShopDTO> shops = shopService.getShops(tipoLocal, provincia, nombre, pageable);
       return ResponseEntity.status(HttpStatus.OK).body(shops);
    }

    @GetMapping("/images/{id}")
    @SecurityRequirements()
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) {
        ShopImage image = shopImageService.findById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(image.getFileType()))
                .body(image.getImageData());
    }
}
