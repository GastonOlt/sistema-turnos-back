package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.CambioPasswordDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.PerfilDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Usuario;
import com.gaston.sistema.turno.sistematunos_back.repositories.EmpleadoRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.UsuarioRepository;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @GetMapping
    public ResponseEntity<?> obtenerPerfil(@AuthenticationPrincipal UserPrincipal user) {
        Usuario usuario = usuarioRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return ResponseEntity.ok(toDTO(usuario));
    }

    @PutMapping
    public ResponseEntity<?> actualizarPerfil(@AuthenticationPrincipal UserPrincipal user,
            @RequestBody PerfilDTO datos) {
        Usuario usuario = usuarioRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (datos.getNombre() != null)
            usuario.setNombre(datos.getNombre());
        if (datos.getApellido() != null)
            usuario.setApellido(datos.getApellido());
        if (datos.getEmail() != null)
            usuario.setEmail(datos.getEmail());

        usuarioRepository.save(usuario);

        // Sincronizar nombre/apellido al perfil fantasma si es Dueño
        if (usuario instanceof Dueno dueno && dueno.getEmpleadoPerfil() != null) {
            Empleado perfil = dueno.getEmpleadoPerfil();
            perfil.setNombre(usuario.getNombre());
            perfil.setApellido(usuario.getApellido());
            empleadoRepository.save(perfil);
        }

        return ResponseEntity.ok(toDTO(usuario));
    }

    @PutMapping("/password")
    public ResponseEntity<?> cambiarPassword(@AuthenticationPrincipal UserPrincipal user,
            @RequestBody CambioPasswordDTO dto) {
        Usuario usuario = usuarioRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "La contraseña actual es incorrecta"));
        }

        usuario.setPassword(passwordEncoder.encode(dto.getPasswordNuevo()));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> subirAvatar(@AuthenticationPrincipal UserPrincipal user,
            @RequestPart("imagen") MultipartFile archivo) {
        try {
            Usuario usuario = usuarioRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            byte[] bytes = archivo.getBytes();
            String nombreArchivo = archivo.getOriginalFilename();
            String tipoArchivo = archivo.getContentType();

            if (usuario instanceof Dueno dueno) {
                // Guardar/actualizar ImagenLocal del Dueño
                ImagenLocal img = dueno.getImagenDueno();
                if (img == null) {
                    img = new ImagenLocal();
                    dueno.setImagenDueno(img);
                }
                img.setDatosImagen(bytes);
                img.setNombreArchivo(nombreArchivo);
                img.setTipoArchivo(tipoArchivo);
                usuarioRepository.save(dueno);

                // Sincronizar al perfil fantasma
                if (dueno.getEmpleadoPerfil() != null) {
                    Empleado perfil = dueno.getEmpleadoPerfil();
                    ImagenLocal imgEmp = perfil.getImagenEmpleado();
                    if (imgEmp == null) {
                        imgEmp = new ImagenLocal();
                        perfil.setImagenEmpleado(imgEmp);
                    }
                    imgEmp.setDatosImagen(bytes);
                    imgEmp.setNombreArchivo(nombreArchivo);
                    imgEmp.setTipoArchivo(tipoArchivo);
                    empleadoRepository.save(perfil);
                }
            } else if (usuario instanceof Empleado empleado) {
                // Guardar/actualizar ImagenLocal del Empleado
                ImagenLocal img = empleado.getImagenEmpleado();
                if (img == null) {
                    img = new ImagenLocal();
                    empleado.setImagenEmpleado(img);
                }
                img.setDatosImagen(bytes);
                img.setNombreArchivo(nombreArchivo);
                img.setTipoArchivo(tipoArchivo);
                empleadoRepository.save(empleado);
            }

            // Devolver la imagen como base64
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return ResponseEntity.ok(Map.of(
                    "datosImagen", base64,
                    "tipoContenido", tipoArchivo,
                    "mensaje", "Avatar actualizado"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al subir la imagen: " + e.getMessage()));
        }
    }

    private PerfilDTO toDTO(Usuario u) {
        PerfilDTO dto = new PerfilDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol());

        // Extraer imagen según tipo de usuario
        ImagenLocal img = null;
        if (u instanceof Dueno dueno) {
            img = dueno.getImagenDueno();
        } else if (u instanceof Empleado empleado) {
            img = empleado.getImagenEmpleado();
        }

        if (img != null && img.getDatosImagen() != null) {
            dto.setDatosImagen(Base64.getEncoder().encodeToString(img.getDatosImagen()));
            dto.setTipoContenido(img.getTipoArchivo());
        }

        return dto;
    }
}
