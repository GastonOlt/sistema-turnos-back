package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;

public interface ImagenLocalService {
    List<ImagenLocal> gurdarImagenes(Long duenoId, MultipartFile[] archivos);
    List<ImagenLocal> obtenerImagenPorLocal(Long localId);

    List<ImagenLocal> editarImagen(Long duenoId,List<Long> idsAEliminar, MultipartFile[] archivos);
    
    ImagenLocal findById(Long imagenId);
}
