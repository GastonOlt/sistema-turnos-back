package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.SlotDisponibleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoEmpleadoService;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/empleado/agenda")
@Tag(name = "Turnos - Empleado", description = "Operaciones de reserva y consulta para empleados")
public class AgendaEmpleadoController {

    private final TurnoService turnoService;
    private final TurnoEmpleadoService turnoEmpleadoService;

    public AgendaEmpleadoController(TurnoService turnoService, TurnoEmpleadoService turnoEmpleadoService) {
        this.turnoService = turnoService;
        this.turnoEmpleadoService = turnoEmpleadoService;
    }

    @Operation(summary = "Crear un turno como empleado", description = "Registra un turno para el empleado autenticado si el slot está disponible.")
    @PostMapping
    public ResponseEntity<TurnoResponseDTO> crearTurnoEmpleado(@AuthenticationPrincipal UserPrincipal user ,@RequestBody TurnoRequestDTO request) {
        Long empleadoId = user.getId();
        TurnoResponseDTO nuevoTurno = turnoService.crearTurnoEmpleado(empleadoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTurno);
    }

    @Operation(summary = "Consultar disponibilidad", description = "Devuelve los rangos horarios libres para un servicio y fecha específicos.")
    @GetMapping("/disponibilidad")
    public  ResponseEntity<List<SlotDisponibleDTO>> obtenerSlotsDisponibles(@AuthenticationPrincipal UserPrincipal user, @RequestParam Long servicioId,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Long empleadoId = user.getId();
        List<SlotDisponibleDTO> slotsDisponibles = turnoService.obtenerSlotsDisponibles(empleadoId, servicioId, fecha);
        return ResponseEntity.status(HttpStatus.OK).body(slotsDisponibles);
    }

    @GetMapping("/mis-servicios")
    public ResponseEntity<List<ServicioLocal>> obtenerMisServicios(@AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId();
        List<ServicioLocal> servicios = turnoEmpleadoService.obtenerServiciosPorEmpleado(empleadoId);
        return ResponseEntity.status(HttpStatus.OK).body(servicios);
    }
    
}
