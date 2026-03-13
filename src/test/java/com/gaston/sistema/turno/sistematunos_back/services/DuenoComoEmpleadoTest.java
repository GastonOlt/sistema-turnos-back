package com.gaston.sistema.turno.sistematunos_back.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.EmpleadoDto;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.repositories.EmpleadoRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ImagenLocalRepository;

@ExtendWith(MockitoExtension.class)
class DuenoComoEmpleadoTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private LocalService localService;

    @Mock
    private ImagenLocalRepository imagenLocalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DuenoService duenoService;

    @InjectMocks
    private EmpleadoServiceImp empleadoService;

    private Local local;
    private Dueno dueno;

    @BeforeEach
    void setUp() {
        local = new Local();
        local.setId(1L);
        local.setNombre("Barbería Test");
        local.setEmpleados(new ArrayList<>());

        dueno = new Dueno();
        dueno.setId(100L);
        dueno.setNombre("Gaston");
        dueno.setApellido("Dueño Test");
        dueno.setEmail("gaston@test.com");
        dueno.setPassword("123456");
        dueno.setRol("DUENO");
        dueno.setLocal(local);
    }

    // ============================================================
    // TEST 1: Unicidad — activar/desactivar no incrementa entidades
    // ============================================================
    @Test
    @DisplayName("Ciclo activar/desactivar dueño muta estado sin crear nuevas entidades")
    void testCicloActivarDesactivarNoIncrementaEntidades() {
        // Simular la creación del perfil (primera activación)
        Empleado perfilEmpleado = new Empleado();
        perfilEmpleado.setId(200L);
        perfilEmpleado.setNombre(dueno.getNombre());
        perfilEmpleado.setApellido(dueno.getApellido());
        perfilEmpleado.setEmail("dueno-100@tuturno.internal");
        perfilEmpleado.setPassword("encoded");
        perfilEmpleado.setRol("EMPLEADO");
        perfilEmpleado.setEspecialidad("Dueño");
        perfilEmpleado.setDueno(true);
        perfilEmpleado.setActivoParaTurnos(true);
        perfilEmpleado.setLocal(local);

        // PRIMERA ACTIVACIÓN: crea el perfil
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(perfilEmpleado);
        Empleado saved = empleadoRepository.save(perfilEmpleado);
        dueno.setEmpleadoPerfil(saved);

        assertNotNull(dueno.getEmpleadoPerfil());
        assertTrue(dueno.getEmpleadoPerfil().isActivoParaTurnos());
        assertEquals(200L, dueno.getEmpleadoPerfil().getId());
        verify(empleadoRepository, times(1)).save(any(Empleado.class));

        // DESACTIVACIÓN: solo muta el flag, NO elimina
        Empleado perfil = dueno.getEmpleadoPerfil();
        perfil.setActivoParaTurnos(false);
        when(empleadoRepository.save(perfil)).thenReturn(perfil);
        empleadoRepository.save(perfil);

        assertFalse(dueno.getEmpleadoPerfil().isActivoParaTurnos());
        assertNotNull(dueno.getEmpleadoPerfil()); // Perfil SIGUE existiendo
        assertEquals(200L, dueno.getEmpleadoPerfil().getId()); // Mismo ID

        // REACTIVACIÓN: solo muta el flag, NO crea nuevo registro
        perfil.setActivoParaTurnos(true);
        when(empleadoRepository.save(perfil)).thenReturn(perfil);
        empleadoRepository.save(perfil);

        assertTrue(dueno.getEmpleadoPerfil().isActivoParaTurnos());
        assertEquals(200L, dueno.getEmpleadoPerfil().getId()); // Mismo ID preservado

        // Verificar que save se llamó 3 veces en total (1 creación + 2 mutaciones)
        // Nunca se llamó a delete
        verify(empleadoRepository, times(3)).save(any(Empleado.class));
        verify(empleadoRepository, never()).delete(any(Empleado.class));
        verify(empleadoRepository, never()).deleteById(any(Long.class));
    }

    // =============================================================
    // TEST 2: Límite eliminado — registrar empleado #6 sin excepción
    // =============================================================
    @Test
    @DisplayName("Crear empleado N°6 no lanza excepción tras eliminar límite")
    void testCrearSextoEmpleadoSinExcepcion() throws Exception {
        // Preparar local con 5 empleados existentes
        for (int i = 1; i <= 5; i++) {
            Empleado emp = new Empleado();
            emp.setId((long) i);
            emp.setNombre("Empleado" + i);
            emp.setApellido("Apell" + i);
            emp.setEmail("emp" + i + "@test.com");
            emp.setPassword("123456");
            emp.setRol("EMPLEADO");
            emp.setLocal(local);
            local.getEmpleados().add(emp);
        }
        assertEquals(5, local.getEmpleados().size());

        // Preparar el 6to empleado
        Empleado sextoEmpleado = new Empleado();
        sextoEmpleado.setNombre("Sexto");
        sextoEmpleado.setApellido("Empleado");
        sextoEmpleado.setEmail("sexto@test.com");
        sextoEmpleado.setPassword("123456");
        sextoEmpleado.setEspecialidad("Corte");

        // Mock del archivo de imagen
        MultipartFile archivo = mock(MultipartFile.class);
        when(archivo.getOriginalFilename()).thenReturn("foto.jpg");
        when(archivo.getContentType()).thenReturn("image/jpeg");
        when(archivo.getBytes()).thenReturn(new byte[] { 1, 2, 3 });

        when(empleadoRepository.findByEmail("sexto@test.com")).thenReturn(Optional.empty());
        when(localService.obtenerPorDuenoEntidad(100L)).thenReturn(local);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        Empleado empleadoGuardado = new Empleado();
        empleadoGuardado.setId(6L);
        empleadoGuardado.setNombre("Sexto");
        empleadoGuardado.setApellido("Empleado");
        empleadoGuardado.setEmail("sexto@test.com");
        empleadoGuardado.setRol("EMPLEADO");
        empleadoGuardado.setEspecialidad("Corte");
        empleadoGuardado.setLocal(local);
        ImagenLocal img = new ImagenLocal();
        img.setNombreArchivo("foto.jpg");
        img.setTipoArchivo("image/jpeg");
        img.setDatosImagen(new byte[] { 1, 2, 3 });
        empleadoGuardado.setImagenEmpleado(img);

        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoGuardado);

        // EJECUTAR: crear el 6to empleado — NO debe lanzar excepción
        EmpleadoDto resultado = assertDoesNotThrow(() -> empleadoService.crearEmpleado(sextoEmpleado, 100L, archivo));

        // VERIFICAR
        assertNotNull(resultado);
        assertEquals("Sexto", resultado.getNombre());
        assertEquals("Empleado", resultado.getApellido());
        assertEquals(6, local.getEmpleados().size()); // Ahora tiene 6
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }
}
