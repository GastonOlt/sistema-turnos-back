package com.gaston.sistema.turno.sistematunos_back.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.repositories.ImagenLocalRepository;

@Service
public class ImagenLocalServiceImp  implements ImagenLocalService{

    @Autowired
    private ImagenLocalRepository imagenRepository;

    @Autowired
    private LocalService localService;

    @Override
    @Transactional
    public List<ImagenLocal> gurdarImagenes(Long  duenoId, MultipartFile[] archivos) {

        List<ImagenLocal> imagenesGuardadas = new ArrayList<>();
        
        Local localDb = localService.obtenerPorDueno(duenoId);
 
        int imagenesActuales = localDb.getImagenes().size();
        int imagenesNuevas = archivos.length;
        if(imagenesActuales + imagenesNuevas > 5) {
            throw new IllegalArgumentException("No se pueden subir más de 5 imágenes. Actualmente tienes " + imagenesActuales + " imágenes.");
        }

        for(MultipartFile archivo : archivos){
            try {
                ImagenLocal imagen = new ImagenLocal();
                imagen.setNombreArchivo(archivo.getOriginalFilename());
                imagen.setTipoArchivo(archivo.getContentType());
                imagen.setDatosImagen(archivo.getBytes());
                imagen.setLocal(localDb);

                localDb.getImagenes().add(imagen);
                
                imagenesGuardadas.add(imagenRepository.save(imagen));

            } catch (IOException  e) {
                throw new IllegalArgumentException("Error al procesar la imagen "+archivo.getOriginalFilename() ,e);
            }
        }
        return imagenesGuardadas;
        
    }   

    @Override
    @Transactional
    public List<ImagenLocal> editarImagen(Long duenoId, List<Long> idsAEliminar, MultipartFile[] archivosNuevos) {
     
        try {
               Local localDb = localService.obtenerPorDueno(duenoId);

        if (idsAEliminar != null && !idsAEliminar.isEmpty()) {
        List<ImagenLocal> imagenesAEliminar = imagenRepository.findAllById(idsAEliminar);
        
        for (ImagenLocal imagen : imagenesAEliminar) {
            if (!imagen.getLocal().getId().equals(localDb.getId())) {
                throw new IllegalArgumentException("La imagen no pertenece a este local");
            }
              
        }

        imagenRepository.deleteAll(imagenesAEliminar);
        localDb.getImagenes().removeAll(imagenesAEliminar);
      }

        int imagenesActuales = localDb.getImagenes().size();
        int imagenesNuevas = archivosNuevos != null ? archivosNuevos.length : 0;
        if (imagenesActuales + imagenesNuevas > 5) {
            throw new IllegalArgumentException("No se pueden tener más de 5 imágenes. Actualmente tienes " + imagenesActuales + " imágenes.");
        }

        List<ImagenLocal> imagenesGuardadas = new ArrayList<>();
        if(archivosNuevos != null){
            for(MultipartFile  archivo : archivosNuevos){
                try {
                    ImagenLocal imagen = new ImagenLocal();
                    imagen.setNombreArchivo(archivo.getOriginalFilename());
                    imagen.setTipoArchivo(archivo.getContentType());
                    imagen.setDatosImagen(archivo.getBytes());
                    imagen.setLocal(localDb);

                    localDb.getImagenes().add(imagen);
                    imagenesGuardadas.add(imagenRepository.save(imagen));
            } catch (IOException e) {
                throw new IllegalArgumentException("Error al procesar la imagen " + archivo.getOriginalFilename() + e.getMessage());
                }
            }
        }
        return imagenesGuardadas;


            
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al actualizar la imagen " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImagenLocal> obtenerImagenPorLocal(Long duenoId) {
      try {
        Local localDb = localService.obtenerPorDueno(duenoId);
       return imagenRepository.findByLocalId( localDb.getId());    
    } catch (Exception e) {
        throw new IllegalArgumentException(e.getMessage());
    } 
    }
    
    @Override
    @Transactional(readOnly = true)
    public ImagenLocal findById(Long imagenId) {
        return imagenRepository.findById(imagenId).orElseThrow(() -> new RuntimeException("Imagen no encontrada "));
     
    }

}
