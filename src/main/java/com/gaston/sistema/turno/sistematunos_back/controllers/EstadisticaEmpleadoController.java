package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.TurnoEmpleadoService;

@RestController
@RequestMapping("/empleado/estadisticas")
public class EstadisticaEmpleadoController {

    private final TurnoEmpleadoService turnoEmpleadoService;

    public EstadisticaEmpleadoController(TurnoEmpleadoService turnoEmpleadoService) {
        this.turnoEmpleadoService = turnoEmpleadoService;
    }
    
    @GetMapping("/ganancias")
    public ResponseEntity<BigDecimal> ganananciasEmpleado(@AuthenticationPrincipal UserPrincipal user, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        Long empleadoId = user.getId();
        BigDecimal ganancias = turnoEmpleadoService.calcularGanancias(empleadoId, desde, hasta);
        return ResponseEntity.status(HttpStatus.OK).body(ganancias);
    }    
}
