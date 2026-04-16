package com.gaston.sistema.turno.sistematunos_back.services;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    /**
     * Uploads an image to Cloudinary.
     * @param file The file to upload.
     * @param folder The folder in Cloudinary where the file should be stored.
     * @return A string array where index 0 is the secure URL and index 1 is the public ID.
     */
    String[] uploadImage(MultipartFile file, String folder);

    /**
     * Deletes an image from Cloudinary.
     * @param publicId The public ID of the image to delete.
     */
    void deleteImage(String publicId);
}
