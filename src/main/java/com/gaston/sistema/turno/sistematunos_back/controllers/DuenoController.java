package com.gaston.sistema.turno.sistematunos_back.controllers;

import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.repositories.EmpleadoRepository;
import com.gaston.sistema.turno.sistematunos_back.security.JwtTokenProvider;
import com.gaston.sistema.turno.sistematunos_back.services.DuenoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dueno")
public class DuenoController {

    @Autowired
    private DuenoService duenoService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/mi-local")
    public ResponseEntity<?> obtenerMiLocal(HttpServletRequest request) {
        String token = obtenerTokenDeRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }

        Long duenoId = jwtTokenProvider.getIdFromToken(token);
        Dueno dueno = duenoService.findById(duenoId).orElse(null);

        if (dueno == null || dueno.getLocal() == null) {
            return ResponseEntity.ok(Map.of("nombreLocal", "Mi Local"));
        }

        Local local = dueno.getLocal();
        return ResponseEntity.ok(Map.of("nombreLocal", local.getNombre()));
    }

    @GetMapping("/estado-atencion")
    public ResponseEntity<?> obtenerEstadoAtencion(HttpServletRequest request) {
        String token = obtenerTokenDeRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }

        Long duenoId = jwtTokenProvider.getIdFromToken(token);
        Dueno dueno = duenoService.findById(duenoId).orElse(null);

        if (dueno == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Dueño no encontrado"));
        }

        boolean activo = dueno.getEmpleadoPerfil() != null && dueno.getEmpleadoPerfil().isActivoParaTurnos();
        return ResponseEntity.ok(Map.of("isAtendiendo", activo));
    }

    @PostMapping("/configurar-atencion")
    public ResponseEntity<?> configurarAtencion(@RequestBody Map<String, Boolean> body,
            HttpServletRequest request) {
        String token = obtenerTokenDeRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }

        Long duenoId = jwtTokenProvider.getIdFromToken(token);
        Dueno dueno = duenoService.findById(duenoId)
                .orElseThrow(() -> new IllegalArgumentException("Dueño no encontrado"));

        Local local = dueno.getLocal();
        if (local == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "El dueño no tiene un local asignado"));
        }

        boolean activar = body.getOrDefault("isAtendiendo", false);

        Empleado perfil = dueno.getEmpleadoPerfil();

        if (perfil == null) {
            // Primera vez: crear el perfil empleado persistente
            perfil = new Empleado();
            perfil.setNombre(dueno.getNombre());
            perfil.setApellido(dueno.getApellido());
            perfil.setEmail("dueno-" + duenoId + "@tuturno.internal");
            perfil.setPassword(passwordEncoder.encode("duenoNoLogin"));
            perfil.setRol("EMPLEADO");
            perfil.setEspecialidad("Dueño");
            perfil.setDueno(true);
            perfil.setActivoParaTurnos(activar);
            perfil.setLocal(local);

            Empleado saved = empleadoRepository.save(perfil);
            dueno.setEmpleadoPerfil(saved);
            duenoService.crearDueno(dueno);
        } else {
            // Ya existe: solo mutar el flag
            perfil.setActivoParaTurnos(activar);
            empleadoRepository.save(perfil);
        }

        String mensaje = activar ? "Cuenta de atención activada" : "Cuenta de atención desactivada";
        return ResponseEntity.ok(Map.of("mensaje", mensaje, "isAtendiendo", activar));
    }

    private String obtenerTokenDeRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
