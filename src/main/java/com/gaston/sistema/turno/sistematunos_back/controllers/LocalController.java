package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.LocalService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/dueno/local")
public class LocalController {
    
    private final LocalService localService;

    public LocalController(LocalService localService) {
        this.localService = localService;
    }

    @PostMapping
    public ResponseEntity<LocalDTO> crearLocal(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody Local local) {
        Long duenoId = user.getId();
        LocalDTO localNuevo = localService.crearLocal(local,duenoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(localNuevo);
    }

    @GetMapping
    public ResponseEntity<?> obtenerPorDueno(@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        Local local = localService.obtenerPorDueno(duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(local);
    }
    
    @PutMapping
    public ResponseEntity<Local> editarLocal(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody Local local) {
        Long duenoId = user.getId();
        Local localEdit = localService.editarLocal(local, duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(localEdit);
    }
    
    @GetMapping("{id}")
    public ResponseEntity<Local> obtenerLocalPorId(@PathVariable Long id) {
            Local localDb = localService.obtenerLocalPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(localDb);
    }
}
