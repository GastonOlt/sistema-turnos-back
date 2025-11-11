package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.EmpleadoDto;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.EmpleadoService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("/empleado")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearEmpleado(@Valid @RequestPart("empleado") Empleado empleado, 
                        @AuthenticationPrincipal UserPrincipal user ,
                        @RequestPart("imagen")  MultipartFile archivo) {
        Long duenoId = user.getId();
        EmpleadoDto empleadoNuevo = empleadoService.crearEmpleado(empleado, duenoId,archivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoNuevo);
    }
    
    @DeleteMapping("/eliminar/{empleadoId}")
    public ResponseEntity<?> eliminarEmpleado(@PathVariable Long empleadoId, @AuthenticationPrincipal UserPrincipal user){
        Long duenoId = user.getId();
        empleadoService.eliminarEmpleado(empleadoId,duenoId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    
    @PutMapping("/editar/{empleadoId}")
    public ResponseEntity<?> editarEmppleado(@RequestPart("empleado") Empleado empleado,
                        @RequestPart("imagen") MultipartFile archivo,
                        @PathVariable Long empleadoId,
                        @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        EmpleadoDto empleadoEditado = empleadoService.editarEmpleado(empleado, archivo,empleadoId,duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(empleadoEditado);
    }
    
    @GetMapping("/todos")
    public ResponseEntity<?> obtenerEmpleados(@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        List<EmpleadoDto> empleados = empleadoService.obtenerEmpleados(duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(empleados);
    }

    @GetMapping("/{empleadoId}")
    public ResponseEntity<?> obtenerEmpleado(@PathVariable Long empleadoId, @AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        EmpleadoDto empleados = empleadoService.obtenerEmpleado(empleadoId,duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(empleados);
    }
   
}
