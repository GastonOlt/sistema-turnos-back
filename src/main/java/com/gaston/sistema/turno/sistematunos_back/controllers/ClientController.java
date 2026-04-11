package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ClientDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ClientService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ClientDTO> getClientProfile(@AuthenticationPrincipal UserPrincipal user) {
        Long clientId = user.getId();
        ClientDTO clientDb = clientService.getClientDTOById(clientId);
        return ResponseEntity.status(HttpStatus.OK).body(clientDb);
    }

    @PutMapping("/profile")
    public ResponseEntity<ClientDTO> editClientProfile(@AuthenticationPrincipal UserPrincipal user, @RequestBody ClientDTO client) {
        Long clientId = user.getId();
        ClientDTO updatedClient = clientService.updateClient(clientId, client);
        return ResponseEntity.status(HttpStatus.OK).body(updatedClient);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteClientProfile(@AuthenticationPrincipal UserPrincipal user) {
        Long clientId = user.getId();
        clientService.deleteClient(clientId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
