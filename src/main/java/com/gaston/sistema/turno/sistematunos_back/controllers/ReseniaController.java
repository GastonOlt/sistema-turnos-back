package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ReseniaRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ReseniaResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ReseniaService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;

@RestController
public class ReseniaController {

    private final ReseniaService reseniaService;

    public ReseniaController(ReseniaService reseniaService) {
        this.reseniaService = reseniaService;
    }

    @PostMapping("/cliente/resenias")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ReseniaResponseDTO> publicarResenia(@AuthenticationPrincipal UserPrincipal user, 
                                          @Valid @RequestBody ReseniaRequestDTO request) {
        Long clienteId = user.getId();
        ReseniaResponseDTO resenia = reseniaService.publicarResenia(clienteId, request);
        return ResponseEntity.status(HttpStatus.OK).body(resenia);
    }

    @GetMapping("/publico/locales/{localId}/resenias")
    @SecurityRequirements()
    public ResponseEntity<List<ReseniaResponseDTO>> verReseniasLocal(@PathVariable Long localId) {
        List<ReseniaResponseDTO> resenias = reseniaService.obtenerReseniasPorLocal(localId);
        return ResponseEntity.status(HttpStatus.OK).body(resenias);
    }

    @GetMapping("/publico/locales/{localId}/resenias/promedio")
    @SecurityRequirements()
    public ResponseEntity<Double> verPromedioLocal(@PathVariable Long localId) {
        Double promedio = reseniaService.obtenerPromedioLocal(localId);
        return ResponseEntity.status(HttpStatus.OK).body(promedio);
    }
}
