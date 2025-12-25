package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.services.LocalService;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/publico/locales")
public class PublicoLocalController {

    private final LocalService localService;

    public PublicoLocalController(LocalService localService) {
        this.localService = localService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Local> obtenerLocalPorId(@PathVariable Long id) {
            Local localDb = localService.obtenerLocalPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(localDb);
    }
    
    @GetMapping
    public ResponseEntity<Page<LocalDTO>> obtenerLocalesDisponiblesPorTipoOProvinciaONombre(
                                                      @RequestParam(required = false) String tipoLocal ,
                                                      @RequestParam(required = false) String provincia,
                                                      @RequestParam(required = false) String nombre,
                                                      @PageableDefault(page = 0, size = 10)Pageable pageable) {

       Page<LocalDTO> locales = localService.obtenerLocales(tipoLocal, provincia, nombre, pageable);
       return ResponseEntity.status(HttpStatus.OK).body(locales);
    }
    
}
