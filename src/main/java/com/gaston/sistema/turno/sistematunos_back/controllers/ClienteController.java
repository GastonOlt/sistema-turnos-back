package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ClienteDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoClienteDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ClienteService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


///CRUD de entidad cliente y info de turnos 
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;
    
    @GetMapping("/obtener")
    public ResponseEntity<?> obtenerCliente(@AuthenticationPrincipal UserPrincipal user) {
        Long clienteId = user.getId();
        ClienteDTO clienteDb = clienteService.obtenerClienteDTOPorId(clienteId);
        return ResponseEntity.ok(clienteDb);
    }

    @PutMapping("/editar")
    public ResponseEntity<?> editarCliente(@AuthenticationPrincipal UserPrincipal user, @RequestBody ClienteDTO cliente) {
        Long clienteId = user.getId();
        ClienteDTO clienteActualizado = clienteService.actualizarCliente(clienteId, cliente);
        return ResponseEntity.ok(clienteActualizado);
    }
    
    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarCliente(@AuthenticationPrincipal UserPrincipal user) {
    Long clienteId = user.getId();
    clienteService.eliminarCliente(clienteId);
    return ResponseEntity.ok("eliminado correctamente");
    }


    @GetMapping("/turnos")
    public ResponseEntity<?> turnosActivosCliente(@AuthenticationPrincipal UserPrincipal user) {
        Long clienteId = user.getId();
        List<TurnoClienteDTO> turnos = clienteService.obtenerTurnosActivos(clienteId);
        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/historial/turnos")
    public ResponseEntity<?> historialTurnosCliente(@AuthenticationPrincipal UserPrincipal user) {
        Long clienteId = user.getId();
        List<TurnoClienteDTO> turnos = clienteService.obtenerHistorialTurnos(clienteId);
        return ResponseEntity.ok(turnos);
    }

    @PatchMapping("/cancelar/turno/{turnoId}")
        public ResponseEntity<?> cancelarTurnoCliente(@AuthenticationPrincipal UserPrincipal user,@PathVariable Long turnoId) {
        Long clienteId = user.getId();
        clienteService.cancelarTurno(clienteId,turnoId);
        return ResponseEntity.ok("turno cancelado");
    }
}
