package com.gaston.sistema.turno.sistematunos_back.controllers;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.gaston.sistema.turno.sistematunos_back.dto.TurnoEmpleadoDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoEmpleadoService;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;






@RestController
@RequestMapping("/empleado/turnos")
public class TurnoGestionEmpleadoController {

    private TurnoEmpleadoService turnoEmpleadoService;

    public TurnoGestionEmpleadoController(TurnoService turnoService, TurnoEmpleadoService turnoEmpleadoService) {
        this.turnoEmpleadoService = turnoEmpleadoService;
    }

    @GetMapping("/confirmados")
    public ResponseEntity<List<TurnoEmpleadoDTO>> obtenerTurnosConfirmados(
            @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId();
        List<TurnoEmpleadoDTO> turnos = turnoEmpleadoService.listadoTurnoConfirmados(empleadoId);
        return ResponseEntity.status(HttpStatus.OK).body(turnos);
    }
    
    @GetMapping("/pendientes")
    public ResponseEntity<List<TurnoEmpleadoDTO>> obtenerTurnosPendientes(
            @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId();
        List<TurnoEmpleadoDTO> turnos = turnoEmpleadoService.listadoTurnoPendientes(empleadoId);
        return ResponseEntity.status(HttpStatus.OK).body(turnos);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<TurnoEmpleadoDTO>> obtenerHistorialTurnos(
            @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId();
        List<TurnoEmpleadoDTO> historial = turnoEmpleadoService.historialTurnos(empleadoId);
        return ResponseEntity.status(HttpStatus.OK).body(historial);
    }

    @PatchMapping("/{turnoId}/cancelar")
    public ResponseEntity<Void> cancelarTurno(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long turnoId) {
        Long empleadoId = user.getId();
        turnoEmpleadoService.cancelarTurno(empleadoId, turnoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{turnoId}/confirmar")
    public ResponseEntity<Void> confirmarTurno(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long turnoId) {
        Long empleadoId = user.getId();
        turnoEmpleadoService.confirmarTurno(empleadoId, turnoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
}
