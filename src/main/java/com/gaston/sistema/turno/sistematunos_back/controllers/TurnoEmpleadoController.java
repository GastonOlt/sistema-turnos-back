package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.SlotDisponibleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoEmpleadoDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoEmpleadoService;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/empleado/turnos")
public class TurnoEmpleadoController {

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private TurnoEmpleadoService turnoEmpleadoService;

    @GetMapping("/horarios/disponibles")
    public  ResponseEntity<List<SlotDisponibleDTO>> obtenerSlotsDisponibles(@AuthenticationPrincipal UserPrincipal user, @RequestParam Long servicioId,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Long empleadoId = user.getId();
        List<SlotDisponibleDTO> slotsDisponibles = turnoService.obtenerSlotsDisponibles(empleadoId, servicioId, fecha);
        return ResponseEntity.ok(slotsDisponibles);
    }

    @GetMapping("/servicios")
    public ResponseEntity<?> obtenerServicios(@AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId();
        List<ServicioLocal> servicios = turnoEmpleadoService.obtenerServiciosPorEmpleado(empleadoId);
        return ResponseEntity.ok(servicios);
    }
    

    @PostMapping("/crear")
    public ResponseEntity<TurnoResponseDTO> crearTurnoEmpleado(@AuthenticationPrincipal UserPrincipal user ,@RequestBody TurnoRequestDTO request) {
        Long empleadoId = user.getId();
        TurnoResponseDTO nuevoTurno = turnoService.crearTurnoEmpleado(empleadoId, request);
        
        return ResponseEntity.ok(nuevoTurno);
    }

    
    @GetMapping("/confirmados")
    public ResponseEntity<List<TurnoEmpleadoDTO>> obtenerTurnosConfirmados(
            @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId();
        List<TurnoEmpleadoDTO> turnos = turnoEmpleadoService.listadoTurnoConfirmados(empleadoId);
        return ResponseEntity.ok(turnos);
    }
    
    @GetMapping("/pendientes")
    public ResponseEntity<List<TurnoEmpleadoDTO>> obtenerTurnosPendientes(
            @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId();
        List<TurnoEmpleadoDTO> turnos = turnoEmpleadoService.listadoTurnoPendientes(empleadoId);
        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<TurnoEmpleadoDTO>> obtenerHistorialTurnos(
            @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId();
        List<TurnoEmpleadoDTO> historial = turnoEmpleadoService.historialTurnos(empleadoId);
        return ResponseEntity.ok(historial);
    }

    @PatchMapping("/{turnoId}/cancelar")
    public ResponseEntity<Void> cancelarTurno(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long turnoId) {
        Long empleadoId = user.getId();
        turnoEmpleadoService.cancelarTurno(empleadoId, turnoId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{turnoId}/confirmar")
    public ResponseEntity<Void> confirmarTurno(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long turnoId) {
        Long empleadoId = user.getId();
        turnoEmpleadoService.confirmarTurno(empleadoId, turnoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ganancias")
    public ResponseEntity<BigDecimal> ganananciasEmpleado(@AuthenticationPrincipal UserPrincipal user, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        Long empleadoId = user.getId();
        BigDecimal ganancias = turnoEmpleadoService.calcularGanancias(empleadoId, desde, hasta);
        return ResponseEntity.ok(ganancias);
    }    
    
}
