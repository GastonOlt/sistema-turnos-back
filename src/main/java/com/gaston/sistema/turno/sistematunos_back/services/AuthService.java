package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Map;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.LoginRequest;
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

    @Transactional
    public UserDTO registerClient(Client client){
        if(clientService.findByEmail(client.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email ya registrado");
        }
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole("CLIENTE");
        Client newClient = clientService.createClient(client);

        return toClientDTO(newClient);
    }

    @Transactional
    public UserDTO registerOwner(Owner owner){
        if(ownerService.findByEmail(owner.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email ya registrado");
        }
        owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        owner.setRole("DUENO");
        Owner newOwner = ownerService.createOwner(owner);
        return toOwnerDTO(newOwner);
    }

    @Transactional
    public Map<String, ResponseCookie> login(@Valid LoginRequest req){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(),req.getPassword()));

            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
            String jwt = jwtTokenProvider.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            ResponseCookie jwtCookie = ResponseCookie.from("accessToken", jwt)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(900)
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken",refreshToken.getToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/autenticacion/refresh")
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

                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenEntity.getToken())
                        .httpOnly(true)
                        .secure(false)
                        .path("/autenticacion/refresh")
                        .maxAge(refreshTokenService.getDurationInSeconds())
                        .sameSite("Lax")
                        .build();

                return Map.of("jwt", jwtCookie, "refresh", refreshCookie);
            })
            .orElseThrow(() -> new IllegalArgumentException("Refresh Token inválido o inexistente"));
    }

    public UserDTO toClientDTO(Client client){
        UserDTO dto = new UserDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setLastName(client.getLastName());
        dto.setEmail(client.getEmail());
        dto.setRole(client.getRole());
        return dto;
    }

    public UserDTO toOwnerDTO(Owner owner){
        UserDTO dto = new UserDTO();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setLastName(owner.getLastName());
        dto.setEmail(owner.getEmail());
        dto.setRole(owner.getRole());
        return dto;
    }
}
