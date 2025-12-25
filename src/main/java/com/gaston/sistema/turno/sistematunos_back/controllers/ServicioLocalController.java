package com.gaston.sistema.turno.sistematunos_back.controllers;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ServicioLocalService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("/dueno/servicios")
public class ServicioLocalController {

    private final ServicioLocalService servicioLocalService;

    public ServicioLocalController(ServicioLocalService servicioLocalService) {
        this.servicioLocalService = servicioLocalService;
    }

    @PostMapping
    public ResponseEntity<ServicioLocal> crearServicio(@Valid @RequestBody ServicioLocal servicio,@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        ServicioLocal nuevoServicio = servicioLocalService.crearServicio(servicio, duenoId);        
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioLocal> editarServicio(@PathVariable Long id, @RequestBody ServicioLocal servicioLocal,
                                            @AuthenticationPrincipal UserPrincipal user)  {
        Long duenoId = user.getId();
        ServicioLocal servicioEditado = servicioLocalService.editarServicio(servicioLocal, id, duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(servicioEditado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerServicio(@PathVariable Long id,@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        ServicioLocal servicioDb = servicioLocalService.obtenerServicio(id,duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(servicioDb);
    }
    
    @GetMapping
    public ResponseEntity<List<ServicioLocal>> obtenerServicios(@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        List<ServicioLocal> servicioDb = servicioLocalService.obtenerServicios(duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(servicioDb);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Long id,@AuthenticationPrincipal UserPrincipal user){
        Long duenoId = user.getId();
        servicioLocalService.eliminarServicio(id, duenoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
}
