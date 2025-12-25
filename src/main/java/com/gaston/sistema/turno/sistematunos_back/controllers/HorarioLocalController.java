package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.entities.Horario;
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
    public ResponseEntity<Horario> crearHorarioLocal(@Valid @RequestBody Horario horario,@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId(); 
        Horario nuevoHorario = horarioService.crearHorarioLocal(horario, duenoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Horario> editarHorarioLocal(@PathVariable Long id, @RequestBody Horario horario,
                                                @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId(); 
        Horario horarioActulizado = horarioService.editarHorarioLocal(horario, id,duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(horarioActulizado);
    }

    @GetMapping
    public ResponseEntity<List<Horario>> obtenerHorariosLocal( @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId(); 
        List<Horario> horarios = horarioService.obtenerHorarios(duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(horarios);
    
    }

    @GetMapping("/{id}")
    public ResponseEntity<Horario> obtenerHorarioLocal(@PathVariable Long id , @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId(); 
        Horario horario = horarioService.obtenerHorario(id,duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(horario);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorarioLocal(@PathVariable Long id , @AuthenticationPrincipal UserPrincipal user){
         Long duenoId = user.getId();
         horarioService.eliminarHorarioLocal(id, duenoId);
          return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
 