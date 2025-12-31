package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.LoginRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.UsuarioDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Cliente;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/autenticacion")
@Tag(name = "Autenticación", description = "Endpoints para registro y login. Maneja Cookies HttpOnly.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Registrar nuevo Cliente", description = "Crea un usuario con rol CLIENTE.")
    @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente")
    @SecurityRequirements()
    @PostMapping("/cliente")
    public ResponseEntity<UsuarioDTO> crearCliente(@Valid @RequestBody Cliente cliente) {
        UsuarioDTO clienteRegistrado = authService.registrarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteRegistrado);
    }

    @Operation(summary = "Registrar nuevo Dueño", description = "Crea un usuario con rol DUEÑO.")
    @ApiResponse(responseCode = "201", description = "Dueño creado exitosamente")
    @SecurityRequirements()
    @PostMapping("/dueno")
    public ResponseEntity<UsuarioDTO> crearDueno(@Valid @RequestBody Dueno dueno) {
      UsuarioDTO duenoRegistrado = authService.registrarDueno(dueno);
       return ResponseEntity.status(HttpStatus.CREATED).body(duenoRegistrado);
    }
    
    @Operation(summary = "Iniciar Sesión", description = "Verifica credenciales y devuelve tokens en Cookies HttpOnly (no visibles en response body).")
    @ApiResponse(responseCode = "200", description = "Login exitoso. Las cookies 'accessToken' y 'refreshToken' han sido seteadas.")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content)
    @SecurityRequirements()
    @PostMapping("/login")
    public ResponseEntity<?> loginCliente(@RequestBody LoginRequest req){
        Map<String, ResponseCookie> cookies = authService.Login(req);
        return ResponseEntity.status(HttpStatus.OK)
                   .header(HttpHeaders.SET_COOKIE, cookies.get("jwt").toString())
                   .header(HttpHeaders.SET_COOKIE, cookies.get("refreshToken").toString())
                   .body((Map.of("mensaje", "Login exitoso")));
    }

    @Operation(summary = "Refrescar Token", description = "Usa la cookie 'refreshToken' para generar un nuevo 'accessToken' sin loguearse de nuevo.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refrescarToken(@CookieValue(name = "refreshToken") String refreshTokenValue) {
        Map<String, ResponseCookie> nuevasCookies = authService.refrescarSesionConCookies(refreshTokenValue);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, nuevasCookies.get("jwt").toString())
                .header(HttpHeaders.SET_COOKIE, nuevasCookies.get("refresh").toString())
                .body(Map.of("mensaje", "Token renovado exitosamente"));
    }
}
