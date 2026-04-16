package com.gaston.sistema.turno.sistematunos_back.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.entities.ShopImage;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopImageRepository;

@Service
public class ShopImageServiceImpl implements ShopImageService {

    private final ShopImageRepository imageRepository;
    private final ShopService shopService;
    private final CloudinaryService cloudinaryService;

    public ShopImageServiceImpl(ShopImageRepository imageRepository, ShopService shopService, CloudinaryService cloudinaryService) {
        this.imageRepository = imageRepository;
        this.shopService = shopService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @Transactional
    public List<ShopImage> saveImages(Long ownerId, MultipartFile[] files) {
        Shop shopDb = shopService.getByOwner(ownerId);
        validateImageLimit(shopDb, files.length);

        List<ShopImage> images = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                images.add(createImageEntity(file, shopDb));
            } catch (IOException e) {
                throw new IllegalArgumentException("Error al procesar la imagen " + file.getOriginalFilename(), e);
            }
        }
        return imageRepository.saveAll(images);
    }

    @Override
    @Transactional
    public List<ShopImage> editImages(Long ownerId, List<Long> idsToDelete, MultipartFile[] newFiles) {
         Shop shopDb = shopService.getByOwner(ownerId);

        if (idsToDelete != null && !idsToDelete.isEmpty()) {
            List<ShopImage> imagesToDelete = imageRepository.findAllById(idsToDelete);

            for (ShopImage image : imagesToDelete) {
                if (!image.getShop().getId().equals(shopDb.getId())) {
                    throw new IllegalArgumentException("La imagen no pertenece a este local");
                }
                // Delete from Cloudinary before deleting from DB
                if (image.getCloudinaryPublicId() != null) {
                    cloudinaryService.deleteImage(image.getCloudinaryPublicId());
                }
            }
            shopDb.getImages().removeAll(imagesToDelete);
            imageRepository.deleteAll(imagesToDelete);
       }

        int newCount = (newFiles != null) ? newFiles.length : 0;
        validateImageLimit(shopDb, newCount);

        List<ShopImage> savedImages = new ArrayList<>();
        if(newFiles != null){
            for(MultipartFile file : newFiles){
                try {
                    ShopImage image = createImageEntity(file, shopDb);
                    savedImages.add(imageRepository.save(image));
            } catch (IOException e) {
                throw new IllegalArgumentException("Error al procesar la imagen " + file.getOriginalFilename() + e.getMessage());
                }
            }
        }
        return savedImages;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopImage> getImagesByShop(Long ownerId) {
        Shop shopDb = shopService.getByOwner(ownerId);
        return imageRepository.findByShopId(shopDb.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ShopImage findById(Long imageId) {
        return imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Imagen no encontrada "));
    }

    private void validateImageLimit(Shop shop, int newCount) {
        long current = imageRepository.countByShopId(shop.getId());
        if (current + newCount > 5) {
            throw new IllegalArgumentException(
                "No se pueden tener más de 5 imágenes. Actualmente tienes " + current + " y quieres agregar " + newCount);
        }
    }

    private ShopImage createImageEntity(MultipartFile file, Shop shop) throws IOException {
        String[] uploadResult = cloudinaryService.uploadImage(file, "shop-images");
        String imageUrl = uploadResult[0];
        String publicId = uploadResult[1];

        ShopImage image = new ShopImage();
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setImageUrl(imageUrl);
        image.setCloudinaryPublicId(publicId);
        image.setShop(shop);
        
        shop.getImages().add(image);
        return image;
    }
}
