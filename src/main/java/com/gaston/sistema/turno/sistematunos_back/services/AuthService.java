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
import com.gaston.sistema.turno.sistematunos_back.dto.UsuarioDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Cliente;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.entities.RefreshToken;
import com.gaston.sistema.turno.sistematunos_back.entities.Usuario;
import com.gaston.sistema.turno.sistematunos_back.security.JwtTokenProvider;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.validation.CredencialesInvalidasException;
import com.gaston.sistema.turno.sistematunos_back.validation.EmailExistenteException;

import jakarta.validation.Valid;

@Service
public class AuthService {

    private final ClienteService clienteService;
    private final DuenoService duenoService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthService(ClienteService clienteService, DuenoService duenoService, PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.clienteService = clienteService;
        this.duenoService = duenoService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }


    @Transactional
    public UsuarioDTO registrarCliente(Cliente cliente){
        if(clienteService.findByEmail(cliente.getEmail()).isPresent()){
            throw new EmailExistenteException("Email ya registrado");     
        }
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        cliente.setRol("CLIENTE");
        Cliente nuevoCLiente = clienteService.crearCliente(cliente);
        
        return clienteDTO(nuevoCLiente);
    }
    
    
    @Transactional
    public UsuarioDTO registrarDueno(Dueno dueno){
        if(duenoService.findByEmail(dueno.getEmail()).isPresent()){
            throw new EmailExistenteException("Email ya registrado");     
        }
        dueno.setPassword(passwordEncoder.encode(dueno.getPassword()));
        dueno.setRol("DUENO");
        Dueno nuevDueno = duenoService.crearDueno(dueno);  
        return duenoDTO(nuevDueno);
    }
    
    @Transactional
    public Map<String, ResponseCookie> Login(@Valid LoginRequest req){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(),req.getPassword()));

            UserPrincipal usuario = (UserPrincipal) authentication.getPrincipal();
            String jwt = jwtTokenProvider.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario.getId());

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
             throw new CredencialesInvalidasException("error en las credenciales");
        }
    }

    @Transactional
   public Map<String, ResponseCookie> refrescarSesionConCookies(String refreshTokenValue) {
    return refreshTokenService.findByToken(refreshTokenValue)
            .map(refreshTokenService::verifyExpiration)
            .map(tokenEntidad -> {
                Usuario usuario = tokenEntidad.getUsuario();
                String nuevoJwt = jwtTokenProvider.generateTokenDesdeUsuario(usuario);

                ResponseCookie jwtCookie = ResponseCookie.from("accessToken", nuevoJwt)
                        .httpOnly(true)
                        .secure(false) 
                        .path("/")
                        .maxAge(900) 
                        .sameSite("Lax")
                        .build();
                        
                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenEntidad.getToken())
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

    public UsuarioDTO clienteDTO(Cliente cliente){
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setEmail(cliente.getEmail());
        dto.setRol(cliente.getRol());

        return dto;
    }
    
    public UsuarioDTO duenoDTO(Dueno dueno){
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(dueno.getId());
        dto.setNombre(dueno.getNombre());
        dto.setApellido(dueno.getApellido());
        dto.setEmail(dueno.getEmail());
        dto.setRol(dueno.getRol());

        return dto;
    }
}
