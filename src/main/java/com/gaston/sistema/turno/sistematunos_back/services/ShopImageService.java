package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.entities.ShopImage;

public interface ShopImageService {
    List<ShopImage> saveImages(Long ownerId, MultipartFile[] files);
    List<ShopImage> getImagesByShop(Long ownerId);
    List<ShopImage> editImages(Long ownerId, List<Long> idsToDelete, MultipartFile[] files);
    ShopImage findById(Long imageId);
}
