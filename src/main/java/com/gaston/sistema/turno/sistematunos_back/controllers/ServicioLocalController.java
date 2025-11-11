package com.gaston.sistema.turno.sistematunos_back.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/dueno/servicio")
public class ServicioLocalController {

    @Autowired
    private ServicioLocalService servicioLocalService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearServicio(@Valid @RequestBody ServicioLocal servicio,@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        ServicioLocal nuevoServicio = servicioLocalService.crearServicio(servicio, duenoId);        
        return ResponseEntity.status(HttpStatus.OK).body(nuevoServicio);
    }

    @PutMapping("/editar/{servicioId}")
    public ResponseEntity<?> editarServicio(@PathVariable Long servicioId, @RequestBody ServicioLocal servicioLocal,
                                            @AuthenticationPrincipal UserPrincipal user)  {
        Long duenoId = user.getId();
        ServicioLocal servicioEditado = servicioLocalService.editarServicio(servicioLocal, servicioId, duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(servicioEditado);
    }

    @GetMapping("/{servicioId}")
    public ResponseEntity<?> obtenerServicio(@PathVariable Long servicioId,@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        ServicioLocal servicioDb = servicioLocalService.obtenerServicio(servicioId,duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(servicioDb);
    }
    
    @GetMapping("/todos")
    public ResponseEntity<?> obtenerServicios(@AuthenticationPrincipal UserPrincipal user) {
        Long duenoId = user.getId();
        List<ServicioLocal> servicioDb = servicioLocalService.obtenerServicios(duenoId);
        return ResponseEntity.status(HttpStatus.OK).body(servicioDb);
    }
    
    @DeleteMapping("/eliminar/{servicioId}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Long servicioId,@AuthenticationPrincipal UserPrincipal user){
        Long duenoId = user.getId();
        servicioLocalService.eliminarServicio(servicioId, duenoId);
        return ResponseEntity.status(HttpStatus.OK).body("eliminado correctamente");
    }
    
}
