package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.LoginRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.UsuarioDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Cliente;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.services.AuthService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/autenticacion")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/cliente")
    public ResponseEntity<UsuarioDTO> crearCliente(@Valid @RequestBody Cliente cliente) {
         UsuarioDTO clienteRegistrado = authService.registrarCliente(cliente);
         return ResponseEntity.status(HttpStatus.CREATED).body(clienteRegistrado);
    }

    @PostMapping("/dueno")
    public ResponseEntity<UsuarioDTO> crearDueno(@Valid @RequestBody Dueno dueno) {
      UsuarioDTO duenoRegistrado = authService.registrarDueno(dueno);
       return ResponseEntity.status(HttpStatus.CREATED).body(duenoRegistrado);
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginCliente(@RequestBody LoginRequest req){
        Map<String,Object> resp = authService.Login(req);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }


}


