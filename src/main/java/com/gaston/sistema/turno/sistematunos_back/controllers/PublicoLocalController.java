package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.services.ImagenLocalService;
import com.gaston.sistema.turno.sistematunos_back.services.LocalService;

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
@RequestMapping("/publico/locales")
public class PublicoLocalController {

    private final LocalService localService;
    private final ImagenLocalService imagenLocalService;

    public PublicoLocalController(LocalService localService, ImagenLocalService imagenLocalService) {
        this.localService = localService;
        this.imagenLocalService = imagenLocalService;
    }

    @GetMapping("{id}")
    @SecurityRequirements()
    public ResponseEntity<Local> obtenerLocalPorId(@PathVariable Long id) {
            Local localDb = localService.obtenerLocalPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(localDb);
    }
    
    @GetMapping
    @SecurityRequirements()
    public ResponseEntity<Page<LocalDTO>> obtenerLocalesDisponiblesPorTipoOProvinciaONombre(
                                                      @RequestParam(required = false) String tipoLocal ,
                                                      @RequestParam(required = false) String provincia,
                                                      @RequestParam(required = false) String nombre,
                                                      @PageableDefault(page = 0, size = 10)Pageable pageable) {

       Page<LocalDTO> locales = localService.obtenerLocales(tipoLocal, provincia, nombre, pageable);
       return ResponseEntity.status(HttpStatus.OK).body(locales);
    }
    
    @GetMapping("/imagenes/{id}")
    @SecurityRequirements()
    public ResponseEntity<byte[]> descargarImagen(@PathVariable Long id) {
        ImagenLocal imagen = imagenLocalService.findById(id);
            
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(imagen.getTipoArchivo()))
                .body(imagen.getDatosImagen());
    }
}
