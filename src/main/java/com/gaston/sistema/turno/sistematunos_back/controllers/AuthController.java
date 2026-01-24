package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.AuthTokenDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.LoginRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.RegistroClienteDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.RegistroDuenoDTO;
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
    public ResponseEntity<UsuarioDTO> crearCliente(@Valid @RequestBody RegistroClienteDTO clienteDto) {
        UsuarioDTO clienteRegistrado = authService.registrarCliente(clienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteRegistrado);
    }

    @Operation(summary = "Registrar nuevo Dueño", description = "Crea un usuario con rol DUEÑO.")
    @ApiResponse(responseCode = "201", description = "Dueño creado exitosamente")
    @SecurityRequirements()
    @PostMapping("/dueno")
    public ResponseEntity<UsuarioDTO> crearDueno(@Valid @RequestBody RegistroDuenoDTO duenoDto) {
        UsuarioDTO duenoRegistrado = authService.registrarDueno(duenoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(duenoRegistrado);
    }

    @Operation(summary = "Iniciar Sesión", description = "Verifica credenciales y devuelve tokens en Cookies HttpOnly (no visibles en response body).")
    @ApiResponse(responseCode = "200", description = "Login exitoso. Las cookies 'accessToken' y 'refreshToken' han sido seteadas.")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content)
    @SecurityRequirements()
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginCliente(@RequestBody LoginRequest req) {
        AuthTokenDTO tokens = authService.Login(req);

        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", tokens.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(900)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/autenticacion/refresh")
                .maxAge(tokens.getRefreshTokenDuration())
                .sameSite("Lax")
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body((Map.of("mensaje", "Login exitoso")));
    }

    @Operation(summary = "Refrescar Token", description = "Usa la cookie 'refreshToken' para generar un nuevo 'accessToken' sin loguearse de nuevo.")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refrescarToken(
            @CookieValue(name = "refreshToken") String refreshTokenValue) {
        AuthTokenDTO tokens = authService.refrescarSesionConCookies(refreshTokenValue);

        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", tokens.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(900)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/autenticacion/refresh")
                .maxAge(tokens.getRefreshTokenDuration())
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("mensaje", "Token renovado exitosamente"));
    }
}
