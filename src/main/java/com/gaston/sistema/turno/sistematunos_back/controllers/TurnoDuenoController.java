package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gaston.sistema.turno.sistematunos_back.dto.SlotDisponibleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoEmpleadoDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.DuenoService;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoEmpleadoService;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoService;

/**
 * Controlador adaptador: delega toda la lógica a
 * TurnoEmpleadoService/TurnoService
 * resolviendo la identidad del Dueño → su perfil fantasma de Empleado.
 */
@RestController
@RequestMapping("/dueno/turnos")
public class TurnoDuenoController {

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private TurnoEmpleadoService turnoEmpleadoService;

    @Autowired
    private DuenoService duenoService;

    /**
     * Extrae el ID del perfil fantasma Empleado vinculado al Dueño autenticado.
     */
    private Long resolverPerfilId(Long duenoId) {
        Dueno dueno = duenoService.findById(duenoId)
                .orElseThrow(() -> new IllegalArgumentException("Dueño no encontrado"));
        Empleado perfil = dueno.getEmpleadoPerfil();
        if (perfil == null || !perfil.isActivoParaTurnos()) {
            throw new IllegalStateException("El dueño no tiene activada la atención de turnos");
        }
        return perfil.getId();
    }

    @GetMapping("/horarios/disponibles")
    public ResponseEntity<List<SlotDisponibleDTO>> obtenerSlotsDisponibles(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam Long servicioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Long perfilId = resolverPerfilId(user.getId());
        List<SlotDisponibleDTO> slots = turnoService.obtenerSlotsDisponibles(perfilId, servicioId, fecha);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/servicios")
    public ResponseEntity<?> obtenerServicios(@AuthenticationPrincipal UserPrincipal user) {
        Long perfilId = resolverPerfilId(user.getId());
        List<ServicioLocal> servicios = turnoEmpleadoService.obtenerServiciosPorEmpleado(perfilId);
        return ResponseEntity.ok(servicios);
    }

    @PostMapping("/crear")
    public ResponseEntity<TurnoResponseDTO> crearTurno(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody TurnoRequestDTO request) {
        Long perfilId = resolverPerfilId(user.getId());
        TurnoResponseDTO nuevoTurno = turnoService.crearTurnoEmpleado(perfilId, request);
        return ResponseEntity.ok(nuevoTurno);
    }

    @GetMapping("/confirmados")
    public ResponseEntity<List<TurnoEmpleadoDTO>> obtenerTurnosConfirmados(
            @AuthenticationPrincipal UserPrincipal user) {
        Long perfilId = resolverPerfilId(user.getId());
        List<TurnoEmpleadoDTO> turnos = turnoEmpleadoService.listadoTurnoConfirmados(perfilId);
        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<TurnoEmpleadoDTO>> obtenerTurnosPendientes(
            @AuthenticationPrincipal UserPrincipal user) {
        Long perfilId = resolverPerfilId(user.getId());
        List<TurnoEmpleadoDTO> turnos = turnoEmpleadoService.listadoTurnoPendientes(perfilId);
        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<TurnoEmpleadoDTO>> obtenerHistorialTurnos(
            @AuthenticationPrincipal UserPrincipal user) {
        Long perfilId = resolverPerfilId(user.getId());
        List<TurnoEmpleadoDTO> historial = turnoEmpleadoService.historialTurnos(perfilId);
        return ResponseEntity.ok(historial);
    }

    @PatchMapping("/{turnoId}/cancelar")
    public ResponseEntity<Void> cancelarTurno(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long turnoId) {
        Long perfilId = resolverPerfilId(user.getId());
        turnoEmpleadoService.cancelarTurno(perfilId, turnoId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{turnoId}/confirmar")
    public ResponseEntity<Void> confirmarTurno(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long turnoId) {
        Long perfilId = resolverPerfilId(user.getId());
        turnoEmpleadoService.confirmarTurno(perfilId, turnoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ganancias")
    public ResponseEntity<BigDecimal> gananciasDueno(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        Long perfilId = resolverPerfilId(user.getId());
        BigDecimal ganancias = turnoEmpleadoService.calcularGanancias(perfilId, desde, hasta);
        return ResponseEntity.ok(ganancias);
    }
}
