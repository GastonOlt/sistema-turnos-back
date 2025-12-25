package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.SlotDisponibleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoClienteDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ClienteService;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/cliente/turnos")
public class TurnoClienteController {


    private final TurnoService turnoService;
    private final ClienteService clienteService;

    public TurnoClienteController(TurnoService turnoService, ClienteService clienteService) {
        this.turnoService = turnoService;
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<TurnoResponseDTO> reservarTurno(@RequestBody TurnoRequestDTO request, @AuthenticationPrincipal UserPrincipal user) {
       Long clienteId = user.getId();
       TurnoResponseDTO turno = turnoService.reservarTurno(clienteId, request);
       return ResponseEntity.status(HttpStatus.CREATED).body(turno);
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<List<SlotDisponibleDTO>> obtenerSlotsDisponibles(@RequestParam Long localId,@RequestParam Long empleadoId, @RequestParam Long servicioId,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<SlotDisponibleDTO> slots = turnoService.obtenerSlotsDisponibles(localId, empleadoId, servicioId, fecha);
        return ResponseEntity.status(HttpStatus.OK).body(slots);
    }
    
    
    @GetMapping("/activos")
    public ResponseEntity<List<TurnoClienteDTO>> obtenerTurnosActivos(@AuthenticationPrincipal UserPrincipal user) {
        Long clienteId = user.getId();
        List<TurnoClienteDTO> turnos = clienteService.obtenerTurnosActivos(clienteId);
        return ResponseEntity.status(HttpStatus.OK).body(turnos);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<TurnoClienteDTO>> obtenerHistorialTurnos(@AuthenticationPrincipal UserPrincipal user) {
        Long clienteId = user.getId();
        List<TurnoClienteDTO> turnos = clienteService.obtenerHistorialTurnos(clienteId);
        return ResponseEntity.status(HttpStatus.OK).body(turnos);
    }

    @PatchMapping("/{turnoId}/cancelar")
        public ResponseEntity<Void> cancelarTurno(@AuthenticationPrincipal UserPrincipal user,@PathVariable Long turnoId) {
        Long clienteId = user.getId();
        clienteService.cancelarTurno(clienteId,turnoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
