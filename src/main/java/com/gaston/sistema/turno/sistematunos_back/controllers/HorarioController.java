package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/horario")
public class HorarioController {
    
    @Autowired
    private HorarioService horarioService;
    
    @PostMapping("/local")
    public ResponseEntity<?> crearHorarioLocal(@Valid @RequestBody Horario horario,@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId(); 
        Horario nuevoHorario = horarioService.crearHorarioLocal(horario, duenoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }

    @PutMapping("/local/{id}")
    public ResponseEntity<?> editarHorarioLocal(@PathVariable Long id, @RequestBody Horario horario,
                                                @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId(); 
        Horario horarioActulizado = horarioService.editarHorarioLocal(horario, id,duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(horarioActulizado);

    }
    @GetMapping("/local/obtener/todos")
    public ResponseEntity<?> obtenerHorariosLocal( @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId(); 
        List<Horario> horarios = horarioService.obtenerHorarios(duenoId);
        return ResponseEntity.ok(horarios);
    
    }
    @GetMapping("/local/obtener/{id}")
    public ResponseEntity<?> obtenerHorarioLocal(@PathVariable Long id , @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId(); 
        Horario horario = horarioService.obtenerHorario(id,duenoId);
        return ResponseEntity.ok(horario);
    }
    
    @DeleteMapping("/local/{id}")
    public ResponseEntity<?> eliminarHorarioLocal(@PathVariable Long id , @AuthenticationPrincipal UserPrincipal user){
         Long duenoId = user.getId();
         horarioService.eliminarHorarioLocal(id, duenoId);
          return ResponseEntity.ok("eliminado correctamente");
    }

    ////// EMPLEADO ////
    @PostMapping("/empleado")
    public ResponseEntity<?> crearHorarioEmpleado(@Valid @RequestBody Horario horario,@AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId(); 
        Horario nuevoHorario = horarioService.crearHorarioEmpleado(horario, empleadoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }

    @PutMapping("/empleado/{id}")
    public ResponseEntity<?> editarHorarioEmpleado(@PathVariable Long id, @RequestBody Horario horario,
                                                @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId(); 
        Horario horarioActulizado = horarioService.editarHorarioEmpleado(horario, id,empleadoId);
        return ResponseEntity.status(HttpStatus.OK).body(horarioActulizado);

    }
    @GetMapping("/empleado/obtener/todos")
    public ResponseEntity<?> obtenerHorariosEmpleado( @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId(); 
        List<Horario> horarios = horarioService.obtenerHorariosEmpleado(empleadoId);
        return ResponseEntity.ok(horarios);
    
    }
    @GetMapping("/empleado/obtener/{id}")
    public ResponseEntity<?> obtenerHorarioEmpleado(@PathVariable Long id , @AuthenticationPrincipal UserPrincipal user) {
        Long empleadoId = user.getId(); 
        Horario horario = horarioService.obtenerHorarioEmpleado(id,empleadoId);
        return ResponseEntity.ok(horario);
    }
    
    @DeleteMapping("/empleado/{id}")
    public ResponseEntity<?> eliminarHorarioEmpleado(@PathVariable Long id , @AuthenticationPrincipal UserPrincipal user){
         Long empleadoId = user.getId();
         horarioService.eliminarHorarioEmpleado(id, empleadoId);
          return ResponseEntity.ok("eliminado correctamente");
    }


}
 