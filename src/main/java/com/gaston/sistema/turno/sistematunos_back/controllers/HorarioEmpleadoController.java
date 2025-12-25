package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.entities.Horario;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.HorarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/empleado/horarios")
public class HorarioEmpleadoController {

    private final HorarioService horarioService;
    
    public HorarioEmpleadoController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    
    @PostMapping
    public ResponseEntity<Horario> crearHorarioEmpleado(@Valid @RequestBody Horario horario,@AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId(); 
        Horario nuevoHorario = horarioService.crearHorarioEmpleado(horario, empleadoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Horario> obtenerHorarioEmpleado(@PathVariable Long id , @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId(); 
        Horario horario = horarioService.obtenerHorarioEmpleado(id,empleadoId);
        return ResponseEntity.status(HttpStatus.OK).body(horario);
    }

    @GetMapping
    public ResponseEntity<List<Horario>> obtenerHorariosEmpleado(@AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId(); 
        List<Horario> horarios = horarioService.obtenerHorariosEmpleado(empleadoId);
        return ResponseEntity.status(HttpStatus.OK).body(horarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Horario> editarHorarioEmpleado(@PathVariable Long id, @RequestBody Horario horario,
                                                @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId(); 
        Horario horarioActulizado = horarioService.editarHorarioEmpleado(horario, id,empleadoId);
        return ResponseEntity.status(HttpStatus.OK).body(horarioActulizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorarioEmpleado(@PathVariable Long id , @AuthenticationPrincipal UserPrincipal user){
         Long empleadoId = user.getId();
         horarioService.eliminarHorarioEmpleado(id, empleadoId);
          return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

