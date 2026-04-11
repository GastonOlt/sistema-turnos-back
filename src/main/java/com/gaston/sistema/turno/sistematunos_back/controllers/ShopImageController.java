package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.entities.ShopImage;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ShopImageService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/owner/shop/images")
public class ShopImageController {

        private final ShopImageService shopImageService;

        public ShopImageController(ShopImageService shopImageService) {
                this.shopImageService = shopImageService;
        }

        @PostMapping(consumes = {"multipart/form-data"})
        public ResponseEntity<List<ShopImage>> saveImages(@AuthenticationPrincipal UserPrincipal user, @RequestParam("imagenes") MultipartFile[] files) {
                Long ownerId = user.getId();
                List<ShopImage> images = shopImageService.saveImages(ownerId, files);
                return ResponseEntity.status(HttpStatus.CREATED).body(images);
        }

        @GetMapping
        public ResponseEntity<List<ShopImage>> listImages(@AuthenticationPrincipal UserPrincipal user) {
            Long ownerId = user.getId();
            List<ShopImage> images = shopImageService.getImagesByShop(ownerId);
           return ResponseEntity.status(HttpStatus.OK).body(images);
        }

        @PatchMapping(consumes = {"multipart/form-data"})
        public ResponseEntity<List<ShopImage>> editImages(@AuthenticationPrincipal UserPrincipal user,
            @RequestParam(value = "eliminar", required = false) List<Long> idsToDelete,
            @RequestParam(value = "nuevas", required = false) MultipartFile[] newFiles) {

           Long ownerId = user.getId();
           List<ShopImage> images = shopImageService.editImages(ownerId, idsToDelete, newFiles);
           return ResponseEntity.status(HttpStatus.OK).body(images);
        }

        @GetMapping("/{imageId}/file")
        @SecurityRequirements()
        public ResponseEntity<byte[]> getImage(@PathVariable Long imageId) {
        ShopImage image = shopImageService.findById(imageId);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(image.getFileType()))
                .body(image.getImageData());
        }
}
