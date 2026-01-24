package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.HorarioDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.HorarioRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.HorarioService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/dueno/horarios")
public class HorarioLocalController {

    private final HorarioService horarioService;

    public HorarioLocalController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @PostMapping
    public ResponseEntity<HorarioDTO> crearHorarioLocal(@Valid @RequestBody HorarioRequestDTO horarioDto,
            @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        HorarioDTO nuevoHorario = horarioService.crearHorarioLocal(horarioDto, duenoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioDTO> editarHorarioLocal(@PathVariable Long id,
            @Valid @RequestBody HorarioRequestDTO horarioDto,
            @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        HorarioDTO horarioActulizado = horarioService.editarHorarioLocal(horarioDto, id, duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(horarioActulizado);
    }

    @GetMapping
    public ResponseEntity<List<HorarioDTO>> obtenerHorariosLocal(@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        List<HorarioDTO> horarios = horarioService.obtenerHorarios(duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(horarios);

    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioDTO> obtenerHorarioLocal(@PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        HorarioDTO horario = horarioService.obtenerHorario(id, duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(horario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorarioLocal(@PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        horarioService.eliminarHorarioLocal(id, duenoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
