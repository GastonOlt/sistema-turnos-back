package com.gaston.sistema.turno.sistematunos_back.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.repositories.ImagenLocalRepository;

@Service
public class ImagenLocalServiceImp  implements ImagenLocalService{

    private final ImagenLocalRepository imagenRepository;
    private final LocalService localService;

    public ImagenLocalServiceImp(ImagenLocalRepository imagenRepository, LocalService localService) {
        this.imagenRepository = imagenRepository;
        this.localService = localService;
    }

    @Override
    @Transactional
    public List<ImagenLocal> gurdarImagenes(Long  duenoId, MultipartFile[] archivos) {

        List<ImagenLocal> imagenesGuardadas = new ArrayList<>();
        
        Local localDb = localService.obtenerPorDueno(duenoId);
        
        validarLimiteImagenes(localDb, archivos.length);

        for(MultipartFile archivo : archivos){
            try {
                ImagenLocal imagen = crearEntidadImagen(archivo, localDb);
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
     
         Local localDb = localService.obtenerPorDueno(duenoId);

        if (idsAEliminar != null && !idsAEliminar.isEmpty()) {
            List<ImagenLocal> imagenesAEliminar = imagenRepository.findAllById(idsAEliminar);
            
            for (ImagenLocal imagen : imagenesAEliminar) {
                if (!imagen.getLocal().getId().equals(localDb.getId())) {
                    throw new IllegalArgumentException("La imagen no pertenece a este local");
                }
                
            }
            localDb.getImagenes().removeAll(imagenesAEliminar);
            imagenRepository.deleteAll(imagenesAEliminar);
       }

        int cantidadNuevas = (archivosNuevos != null) ? archivosNuevos.length : 0;
        validarLimiteImagenes(localDb, cantidadNuevas);

        List<ImagenLocal> imagenesGuardadas = new ArrayList<>();
        if(archivosNuevos != null){
            for(MultipartFile  archivo : archivosNuevos){
                try {
                    ImagenLocal imagen = crearEntidadImagen(archivo, localDb);
                    imagenesGuardadas.add(imagenRepository.save(imagen));
            } catch (IOException e) {
                throw new IllegalArgumentException("Error al procesar la imagen " + archivo.getOriginalFilename() + e.getMessage());
                }
            }
        }
        return imagenesGuardadas;
    }


    @Override
    @Transactional(readOnly = true)
    public List<ImagenLocal> obtenerImagenPorLocal(Long duenoId) {
        Local localDb = localService.obtenerPorDueno(duenoId);
        return imagenRepository.findByLocalId( localDb.getId());    
    }
    
    @Override
    @Transactional(readOnly = true)
    public ImagenLocal findById(Long imagenId) {
        return imagenRepository.findById(imagenId).orElseThrow(() -> new RuntimeException("Imagen no encontrada "));
     
    }

    private void validarLimiteImagenes(Local local, int nuevas) {
        int actuales = local.getImagenes().size();
        if (actuales + nuevas > 5) {
            throw new IllegalArgumentException("No se pueden tener más de 5 imágenes. Actualmente tienes " + actuales + " y quieres agregar " + nuevas);
        }
    }

    private ImagenLocal crearEntidadImagen(MultipartFile archivo, Local local) throws IOException {
        ImagenLocal imagen = new ImagenLocal();
        imagen.setNombreArchivo(archivo.getOriginalFilename());
        imagen.setTipoArchivo(archivo.getContentType());
        imagen.setDatosImagen(archivo.getBytes());
        imagen.setLocal(local);
        local.getImagenes().add(imagen);
        return imagen;
    }
}
