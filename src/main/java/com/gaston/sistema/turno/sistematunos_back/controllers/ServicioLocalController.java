package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ServicioLocalDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ServicioLocalRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ServicioLocalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/dueno/servicios")
@Tag(name = "Gestión de Servicios (Dueño)", description = "ABM de servicios ofrecidos por el local")
public class ServicioLocalController {

    private final ServicioLocalService servicioLocalService;

    public ServicioLocalController(ServicioLocalService servicioLocalService) {
        this.servicioLocalService = servicioLocalService;
    }

    @Operation(summary = "Crear Servicio", description = "Agrega un nuevo servicio al local del dueño autenticado.")
    @PostMapping
    public ResponseEntity<ServicioLocalDTO> crearServicio(@Valid @RequestBody ServicioLocalRequestDTO servicioDto,
            @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        ServicioLocalDTO nuevoServicio = servicioLocalService.crearServicio(servicioDto, duenoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioLocalDTO> editarServicio(@PathVariable Long id,
            @Valid @RequestBody ServicioLocalRequestDTO servicioDto,
            @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        ServicioLocalDTO servicioEditado = servicioLocalService.editarServicio(servicioDto, id, duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(servicioEditado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioLocalDTO> obtenerServicio(@PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        ServicioLocalDTO servicioDb = servicioLocalService.obtenerServicio(id, duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(servicioDb);
    }

    @GetMapping
    public ResponseEntity<List<ServicioLocalDTO>> obtenerServicios(@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        List<ServicioLocalDTO> servicioDb = servicioLocalService.obtenerServicios(duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(servicioDb);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        servicioLocalService.eliminarServicio(id, duenoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
