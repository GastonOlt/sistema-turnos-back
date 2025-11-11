package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ImagenLocalService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;



@RestController
@RequestMapping("/dueno")
public class ImagenLocalController {

        @Autowired
        private ImagenLocalService imagenLocalService;

        @PostMapping("/locales/imagenes")
        public ResponseEntity<?> guardarImagenes(@AuthenticationPrincipal UserPrincipal user, @RequestParam("imagenes") MultipartFile[] archivos) {
                Long duenoId = user.getId();
                List<ImagenLocal> imagenes = imagenLocalService.gurdarImagenes(duenoId, archivos);
                return ResponseEntity.status(HttpStatus.CREATED).body(imagenes);
        }

        @GetMapping("/locales/imagenes")
        public ResponseEntity<List<ImagenLocal>> listarImagenes(@AuthenticationPrincipal UserPrincipal user ) {
            Long duenoId = user.getId();
            List<ImagenLocal> imagenes = imagenLocalService.obtenerImagenPorLocal(duenoId); 
            return ResponseEntity.ok(imagenes);
        }

        @PatchMapping("/locales/imagenes")
        public ResponseEntity<List<ImagenLocal>> editarImagenesParcial(@AuthenticationPrincipal UserPrincipal user,
            @RequestParam(value = "eliminar", required = false) List<Long> idsAEliminar,
            @RequestParam(value = "nuevas", required = false) MultipartFile[] archivosNuevos) {
        
           Long duenoId = user.getId();
           List<ImagenLocal> imagenes = imagenLocalService.editarImagen(duenoId, idsAEliminar, archivosNuevos);
           return ResponseEntity.ok(imagenes);
        }

        
        @GetMapping("/{imagenId}/imagen")
        public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long imagenId) {
        ImagenLocal imagen = imagenLocalService.findById(imagenId);
                
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(imagen.getTipoArchivo()))
                .body(imagen.getDatosImagen());
        }
        
        
}
