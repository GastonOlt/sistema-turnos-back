package com.gaston.sistema.turno.sistematunos_back.services;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.gaston.sistema.turno.sistematunos_back.dto.AuthTokenDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.LoginRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.RegistroClienteDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.RegistroDuenoDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.UsuarioDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Cliente;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.entities.RefreshToken;
import com.gaston.sistema.turno.sistematunos_back.entities.Usuario;
import com.gaston.sistema.turno.sistematunos_back.security.JwtTokenProvider;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.validation.CredencialesInvalidasException;
import com.gaston.sistema.turno.sistematunos_back.validation.EmailExistenteException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager,
            RefreshTokenService refreshTokenService) {
        this.clienteService = clienteService;
        this.duenoService = duenoService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public UsuarioDTO registrarCliente(RegistroClienteDTO clienteDto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteDto.getNombre());
        cliente.setApellido(clienteDto.getApellido());
        cliente.setEmail(clienteDto.getEmail());
        cliente.setPassword(clienteDto.getPassword());

        if (clienteService.findByEmail(cliente.getEmail()).isPresent()) {
            throw new EmailExistenteException("Email ya registrado");
        }
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        cliente.setRol("CLIENTE");
        Cliente nuevoCLiente = clienteService.crearCliente(cliente);

        return clienteDTO(nuevoCLiente);
    }

    @Transactional
    public UsuarioDTO registrarDueno(RegistroDuenoDTO duenoDto) {
        Dueno dueno = new Dueno();
        dueno.setNombre(duenoDto.getNombre());
        dueno.setApellido(duenoDto.getApellido());
        dueno.setEmail(duenoDto.getEmail());
        dueno.setPassword(duenoDto.getPassword());

        if (duenoService.findByEmail(dueno.getEmail()).isPresent()) {
            throw new EmailExistenteException("Email ya registrado");
        }
        dueno.setPassword(passwordEncoder.encode(dueno.getPassword()));
        dueno.setRol("DUENO");
        Dueno nuevDueno = duenoService.crearDueno(dueno);
        return duenoDTO(nuevDueno);
    }

    @Transactional
    public AuthTokenDTO Login(@Valid LoginRequest req) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

            UserPrincipal usuario = (UserPrincipal) authentication.getPrincipal();
            String jwt = jwtTokenProvider.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario.getId());

            return new AuthTokenDTO(jwt, refreshToken.getToken(), refreshTokenService.getDurationInSeconds());

        } catch (AuthenticationException e) {
            throw new CredencialesInvalidasException("error en las credenciales");
        }
    }

    @Transactional
    public AuthTokenDTO refrescarSesionConCookies(String refreshTokenValue) {
        return refreshTokenService.findByToken(refreshTokenValue)
                .map(refreshTokenService::verifyExpiration)
                .map(tokenEntidad -> {
                    Usuario usuario = tokenEntidad.getUsuario();
                    String nuevoJwt = jwtTokenProvider.generateTokenDesdeUsuario(usuario);

                    return new AuthTokenDTO(nuevoJwt, tokenEntidad.getToken(),
                            refreshTokenService.getDurationInSeconds());
                })
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token inválido o inexistente"));
    }

    public UsuarioDTO clienteDTO(Cliente cliente) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setEmail(cliente.getEmail());
        dto.setRol(cliente.getRol());

        return dto;
    }

    public UsuarioDTO duenoDTO(Dueno dueno) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(dueno.getId());
        dto.setNombre(dueno.getNombre());
        dto.setApellido(dueno.getApellido());
        dto.setEmail(dueno.getEmail());
        dto.setRol(dueno.getRol());

        return dto;
    }
}
