package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.LoginRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.RegisterClientRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.RegisterOwnerRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.UserDTO;
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
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for registration, login and logout. Handles HttpOnly Cookies.")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new Client", description = "Creates a user with the CLIENT role. The role is always set server-side.")
    @ApiResponse(responseCode = "201", description = "Client created successfully")
    @SecurityRequirements()
    @PostMapping("/client")
    public ResponseEntity<UserDTO> registerClient(@Valid @RequestBody RegisterClientRequest request) {
        UserDTO registeredClient = authService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredClient);
    }

    @Operation(summary = "Register a new Owner", description = "Creates a user with the OWNER role. The role is always set server-side.")
    @ApiResponse(responseCode = "201", description = "Owner created successfully")
    @SecurityRequirements()
    @PostMapping("/owner")
    public ResponseEntity<UserDTO> registerOwner(@Valid @RequestBody RegisterOwnerRequest request) {
        UserDTO registeredOwner = authService.registerOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredOwner);
    }

    @Operation(summary = "Login", description = "Verifies credentials and returns tokens in HttpOnly Cookies (not visible in response body).")
    @ApiResponse(responseCode = "200", description = "Login successful. The 'accessToken' and 'refreshToken' cookies have been set.")
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    @SecurityRequirements()
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Map<String, ResponseCookie> cookies = authService.login(req);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookies.get("jwt").toString())
                .header(HttpHeaders.SET_COOKIE, cookies.get("refreshToken").toString())
                .body(Map.of("message", "Login successful"));
    }

    @Operation(summary = "Refresh Token", description = "Uses the 'refreshToken' cookie to generate a new 'accessToken' without logging in again.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken") String refreshTokenValue) {
        Map<String, ResponseCookie> newCookies = authService.refreshSessionWithCookies(refreshTokenValue);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newCookies.get("jwt").toString())
                .header(HttpHeaders.SET_COOKIE, newCookies.get("refresh").toString())
                .body(Map.of("message", "Token refreshed successfully"));
    }

    @Operation(summary = "Logout", description = "Invalidates the refresh token in the database and clears both auth cookies in the browser.")
    @ApiResponse(responseCode = "200", description = "Logout successful. Cookies have been cleared.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken", required = false) String refreshTokenValue) {
        Map<String, ResponseCookie> expiredCookies = authService.logout(refreshTokenValue);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookies.get("jwt").toString())
                .header(HttpHeaders.SET_COOKIE, expiredCookies.get("refreshToken").toString())
                .body(Map.of("message", "Logout successful"));
    }
}
