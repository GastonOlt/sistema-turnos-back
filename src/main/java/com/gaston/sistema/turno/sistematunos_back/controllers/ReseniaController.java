package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ReseniaRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ReseniaResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ReseniaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping
public class ReseniaController {

    @Autowired
    private ReseniaService reseniaService;

    @PostMapping("/cliente/resenia/publicar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> publicarResenia(@AuthenticationPrincipal UserPrincipal user, 
                                          @Valid @RequestBody ReseniaRequestDTO request) {
        Long clienteId = user.getId();
        ReseniaResponseDTO resenia = reseniaService.publicarResenia(clienteId, request);
        return ResponseEntity.ok(resenia);
    }

    @GetMapping("/publico/resenia/local/{localId}")
    public ResponseEntity<List<ReseniaResponseDTO>> verReseniasLocal(@PathVariable Long localId) {
        return ResponseEntity.ok(reseniaService.obtenerReseniasPorLocal(localId));
    }

    @GetMapping("/publico/resenia/local/{localId}/promedio")
    public ResponseEntity<?> verPromedioLocal(@PathVariable Long localId) {
        return ResponseEntity.ok(Map.of("promedio", reseniaService.obtenerPromedioLocal(localId)));
    }
}
