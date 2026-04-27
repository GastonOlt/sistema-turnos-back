package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.LoginRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.RegisterClientRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.RegisterOwnerRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.UserDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Client;
import com.gaston.sistema.turno.sistematunos_back.entities.Owner;
import com.gaston.sistema.turno.sistematunos_back.entities.RefreshToken;
import com.gaston.sistema.turno.sistematunos_back.entities.User;
import com.gaston.sistema.turno.sistematunos_back.security.JwtTokenProvider;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.validation.InvalidCredentialsException;
import com.gaston.sistema.turno.sistematunos_back.validation.EmailAlreadyExistsException;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Service
@Validated
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final ClientService clientService;
    private final OwnerService ownerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthService(ClientService clientService, OwnerService ownerService, PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.clientService = clientService;
        this.ownerService = ownerService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Registers a new Client.
     * Accepts a dedicated DTO (no 'role' field) to prevent privilege escalation.
     * The role is always forced to "CLIENTE" server-side.
     */
    @Transactional
    public UserDTO registerClient(@Valid RegisterClientRequest request) {
        if (clientService.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email ya registrado");
        }

        Client client = new Client();
        client.setName(request.getName());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        client.setRole("CLIENTE"); // role always forced — never from request body

        Client newClient = clientService.createClient(client);
        log.info("New client registered with email={}", newClient.getEmail());
        return toUserDTO(newClient);
    }

    /**
     * Registers a new Owner.
     * Accepts a dedicated DTO (no 'role' field) to prevent privilege escalation.
     * The role is always forced to "DUENO" server-side.
     */
    @Transactional
    public UserDTO registerOwner(@Valid RegisterOwnerRequest request) {
        if (ownerService.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email ya registrado");
        }

        Owner owner = new Owner();
        owner.setName(request.getName());
        owner.setLastName(request.getLastName());
        owner.setEmail(request.getEmail());
        owner.setPassword(passwordEncoder.encode(request.getPassword()));
        owner.setRole("DUENO"); // role always forced — never from request body

        Owner newOwner = ownerService.createOwner(owner);
        log.info("New owner registered with email={}", newOwner.getEmail());
        return toUserDTO(newOwner);
    }

    @Transactional
    public Map<String, ResponseCookie> login(@Valid LoginRequest req) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
            String jwt = jwtTokenProvider.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            log.info("User logged in: id={}", user.getId());

            ResponseCookie jwtCookie = ResponseCookie.from("accessToken", jwt)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(900)
                    .sameSite("Lax")
                    .build();

            // Fix: path must match the real endpoint /auth/refresh
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/auth/refresh")
                    .maxAge(refreshTokenService.getDurationInSeconds())
                    .sameSite("Lax")
                    .build();

            return Map.of("jwt", jwtCookie, "refreshToken", refreshCookie);

        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("error en las credenciales");
        }
    }

    @Transactional
    public Map<String, ResponseCookie> refreshSessionWithCookies(String refreshTokenValue) {
        return refreshTokenService.findByToken(refreshTokenValue)
                .map(refreshTokenService::verifyExpiration)
                .map(tokenEntity -> {
                    User user = tokenEntity.getUser();
                    String newJwt = jwtTokenProvider.generateTokenFromUser(user);

                    ResponseCookie jwtCookie = ResponseCookie.from("accessToken", newJwt)
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(900)
                            .sameSite("Lax")
                            .build();

                    // Fix: path must match the real endpoint /auth/refresh
                    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenEntity.getToken())
                            .httpOnly(true)
                            .secure(false)
                            .path("/auth/refresh")
                            .maxAge(refreshTokenService.getDurationInSeconds())
                            .sameSite("Lax")
                            .build();

                    return Map.of("jwt", jwtCookie, "refresh", refreshCookie);
                })
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token inválido o inexistente"));
    }

    /**
     * Logs out the user by deleting their RefreshToken from the database
     * and returning expired cookies to force the browser to clear them.
     */
    @Transactional
    public Map<String, ResponseCookie> logout(String refreshTokenValue) {
        // Invalidate the refresh token in DB (prevents future use even if someone captured the cookie)
        refreshTokenService.findByToken(refreshTokenValue)
                .ifPresent(token -> {
                    log.info("Logout: deleting refresh token for user id={}", token.getUser().getId());
                    refreshTokenService.deleteByToken(token);
                });

        // Return expired cookies so the browser deletes them immediately
        ResponseCookie expiredJwt = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie expiredRefresh = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/auth/refresh")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return Map.of("jwt", expiredJwt, "refreshToken", expiredRefresh);
    }

    private UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
