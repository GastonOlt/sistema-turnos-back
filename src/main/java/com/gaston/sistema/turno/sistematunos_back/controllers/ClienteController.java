package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ClienteDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ClienteService;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/perfil")
    public ResponseEntity<ClienteDTO> obtenerPerfilCliente(@AuthenticationPrincipal UserPrincipal user) {
        Long clienteId = user.getId();
        ClienteDTO clienteDb = clienteService.obtenerClienteDTOPorId(clienteId);
        return ResponseEntity.status(HttpStatus.OK).body(clienteDb);
    }

    @PutMapping("/perfil")
    public ResponseEntity<ClienteDTO> editarPerfilCliente(@AuthenticationPrincipal UserPrincipal user, @RequestBody ClienteDTO cliente) {
        Long clienteId = user.getId();
        ClienteDTO clienteActualizado = clienteService.actualizarCliente(clienteId, cliente);
        return ResponseEntity.status(HttpStatus.OK).body(clienteActualizado);
    }
    
    @DeleteMapping("/perfil")
    public ResponseEntity<Void> eliminarPerfilCliente(@AuthenticationPrincipal UserPrincipal user) {
        Long clienteId = user.getId();
        clienteService.eliminarCliente(clienteId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
