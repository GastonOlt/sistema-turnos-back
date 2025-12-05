package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.SlotDisponibleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/cliente/turnos")
public class TurnoClienteController {

    @Autowired
    private TurnoService turnoService;

    @PostMapping("/reservar")
    public ResponseEntity<?> reservarTurno(@RequestBody TurnoRequestDTO request, @AuthenticationPrincipal UserPrincipal user) {
       Long clienteId = user.getId();
       TurnoResponseDTO turno = turnoService.reservarTurno(clienteId, request);
       return ResponseEntity.status(HttpStatus.OK).body(turno);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<SlotDisponibleDTO>> obtenerSlotsDisponibles(@RequestParam Long localId,@RequestParam Long empleadoId, @RequestParam Long servicioId,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<SlotDisponibleDTO> slots = turnoService.obtenerSlotsDisponibles(localId, empleadoId, servicioId, fecha);
        return ResponseEntity.status(HttpStatus.OK).body(slots);
    }
        
    }
