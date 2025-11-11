package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.services.LocalService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/publico/local")
public class PublicoLocalController {

    @Autowired
    private LocalService localService;

    @GetMapping("/obtener/todos")
    public ResponseEntity<?> obtenerLocalesDisponibles(@PageableDefault(page = 0, size = 10)Pageable pageable) {
        Page<LocalDTO> localesDisponibles = localService.obtenerLocalesDisponibles(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(localesDisponibles);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> obtenerLocalPorId(@PathVariable Long id) {
            Local localDb = localService.obtenerLocalPorId(id);
            return ResponseEntity.ok(localDb);
    }
    
    @GetMapping("/obtener")
    public ResponseEntity<?> obtenerLocalesDisponiblesPorTipoOProvinciaONombre(@RequestParam(required = false) String tipoLocal ,
                                                      @RequestParam(required = false) String provincia,
                                                      @RequestParam(required = false) String nombre,
                                                      @PageableDefault(page = 0, size = 10)Pageable pageable) {
        Page<LocalDTO> localesDisponibles = localService.obtnerLocalPorTipoOProvicinciaONombre(tipoLocal,provincia,nombre,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(localesDisponibles);
    }
    
}
