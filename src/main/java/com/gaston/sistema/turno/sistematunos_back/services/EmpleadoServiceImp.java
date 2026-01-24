package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Base64;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.EmpleadoDto;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.entities.Horario;
import com.gaston.sistema.turno.sistematunos_back.repositories.EmpleadoRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ImagenLocalRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.HorarioRepository;
import com.gaston.sistema.turno.sistematunos_back.validation.EmailExistenteException;

import com.gaston.sistema.turno.sistematunos_back.dto.EmpleadoRequestDTO;

@Service
public class EmpleadoServiceImp implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final LocalService localService;
    private final ImagenLocalRepository imagenLocalRepository;
    private final HorarioRepository horarioRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoServiceImp(EmpleadoRepository empleadoRepository, LocalService localService,
            ImagenLocalRepository imagenLocalRepository, HorarioRepository horarioRepository,
            PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.localService = localService;
        this.imagenLocalRepository = imagenLocalRepository;
        this.horarioRepository = horarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public EmpleadoDto crearEmpleado(EmpleadoRequestDTO empleadoDto, Long duenoId, MultipartFile archivo) {
        if (empleadoRepository.findByEmail(empleadoDto.getEmail()).isPresent()) {
            throw new EmailExistenteException("Email ya registrado");
        }

        Local localDb = localService.obtenerPorDueno(duenoId);
        if (localDb.getEmpleados().size() >= 5) {
            throw new IllegalArgumentException("No puedes tener mas de 5 empleados");
        }

        Empleado empleado = new Empleado();
        empleado.setNombre(empleadoDto.getNombre());
        empleado.setApellido(empleadoDto.getApellido());
        empleado.setEmail(empleadoDto.getEmail());
        empleado.setEspecialidad(empleadoDto.getEspecialidad());
        // Password por defecto o aleatoria? Por ahora asumimos una default o la del DTO
        // si existiera (no existe)
        // Usaremos email como password temporal para este ejemplo si el usuario no la
        // provee
        empleado.setPassword(passwordEncoder.encode(empleadoDto.getEmail()));

        try {
            if (archivo != null && !archivo.isEmpty()) {
                ImagenLocal imgEmpleado = new ImagenLocal();
                imgEmpleado.setNombreArchivo(archivo.getOriginalFilename());
                imgEmpleado.setTipoArchivo(archivo.getContentType());
                imgEmpleado.setDatosImagen(archivo.getBytes());
                empleado.setImagenEmpleado(imgEmpleado);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la imagen del empleado: " + e);
        }

        localDb.getEmpleados().add(empleado);

        empleado.setLocal(localDb);
        empleado.setRol("EMPLEADO");

        Empleado nuevoEmpleado = empleadoRepository.save(empleado);

        // Copiar horarios del local al nuevo empleado
        List<Horario> horariosLocal = localDb.getHorarios();
        if (horariosLocal != null && !horariosLocal.isEmpty()) {
            for (Horario horarioLocal : horariosLocal) {
                // Solo copiar los horarios generales del local (sin empleado asignado)
                if (horarioLocal.getEmpleado() == null) {
                    Horario nuevoHorario = new Horario();
                    nuevoHorario.setActivo(horarioLocal.isActivo());
                    nuevoHorario.setDiaSemana(horarioLocal.getDiaSemana());
                    nuevoHorario.setHorarioApertura(horarioLocal.getHorarioApertura());
                    nuevoHorario.setHorarioCierre(horarioLocal.getHorarioCierre());
                    nuevoHorario.setLocal(null); // O pcional: vincular al local tambien si se requiere, pero Horario
                                                 // tiene OneToMany mappedBy local or empleado usually.
                    // En Horario.java: @ManyToOne Local local. Si el horario es del empleado, ¿debe
                    // tener reference al local?
                    // Generalmente el horario de empleado es personal. Pero veamos
                    // HorarioServiceImp. crearHorarioEmpleado no setea local, solo empleado.
                    // Pero si setea local puede ser util. Dejemoslo null por consistencia con
                    // crearHorarioEmpleado que no setea local explícitamente en el snippet previo
                    // (aunque checkear)
                    // Revisando HorarioServiceImp.crearHorarioEmpleado:
                    // HorarioServiceImp.java:98 -> horario.setEmpleado(empleadoDb); return save.
                    // No setea local.

                    nuevoHorario.setEmpleado(nuevoEmpleado);
                    horarioRepository.save(nuevoHorario);
                }
            }
        }

        return empleadoDto(nuevoEmpleado);
    }

    @Override
    @Transactional
    public EmpleadoDto editarEmpleado(EmpleadoRequestDTO empleadoDto, MultipartFile archivo, Long empleadoId,
            Long duenoId) {
        Empleado empleadoDb = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el empleado con ese id " + empleadoId));

        if (!empleadoDb.getLocal().getDueno().getId().equals(duenoId)) {
            throw new AccessDeniedException("No tienes permisos para editar este empleado");
        }

        empleadoDb.setApellido(empleadoDto.getApellido());
        empleadoDb.setEspecialidad(empleadoDto.getEspecialidad());
        empleadoDb.setNombre(empleadoDto.getNombre());
        empleadoDb.setEmail(empleadoDto.getEmail());
        // Password update logic separate

        try {
            if (archivo != null && !archivo.isEmpty()) {
                ImagenLocal img = empleadoDb.getImagenEmpleado();
                if (img != null) {
                    imagenLocalRepository.delete(img);
                }
                ImagenLocal imgEmpleado = new ImagenLocal();
                imgEmpleado.setNombreArchivo(archivo.getOriginalFilename());
                imgEmpleado.setTipoArchivo(archivo.getContentType());
                imgEmpleado.setDatosImagen(archivo.getBytes());

                empleadoDb.setImagenEmpleado(imgEmpleado);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la imagen del empleado: " + e);
        }

        Empleado empleadoEditado = empleadoRepository.save(empleadoDb);
        return empleadoDto(empleadoEditado);
    }

    @Override
    public void eliminarEmpleado(Long empleadoId, Long duenoId) {
        Empleado empleadoDb = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("no se encontro el empleado con ese id" + empleadoId));

        if (!empleadoDb.getLocal().getDueno().getId().equals(duenoId)) {
            throw new AccessDeniedException("No tienes permisos para eliminar este empleado");
        }
        empleadoRepository.deleteById(empleadoId);
    }

    @Override
    public List<EmpleadoDto> obtenerEmpleados(Long duenoId) {
        Local localDb = localService.obtenerPorDueno(duenoId);
        return empleadoRepository.findByLocalId(localDb.getId()).stream().map(emple -> empleadoDto(emple))
                .toList();
    }

    @Override
    public EmpleadoDto obtenerEmpleado(Long empleadoId, Long duenoId) {
        Empleado empleadoDb = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("no se encontro el empleado con ese id" + empleadoId));

        if (!empleadoDb.getLocal().getDueno().getId().equals(duenoId)) {
            throw new AccessDeniedException("No tienes permisos para ver este empleado");
        }
        return empleadoDto(empleadoDb);
    }

    @Override
    public Empleado obtenerEmpleadoEntity(Long empleadoId) {
        Empleado empl = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("error al encontrar al empleado"));
        return empl;
    }

    public EmpleadoDto empleadoDto(Empleado empleado) {
        EmpleadoDto respDto = new EmpleadoDto();
        respDto.setId(empleado.getId());
        respDto.setApellido(empleado.getApellido());
        respDto.setNombre(empleado.getNombre());
        respDto.setEmail(empleado.getEmail());
        respDto.setRol(empleado.getRol());
        respDto.setEspecialidad(empleado.getEspecialidad());

        if (empleado.getImagenEmpleado() != null) {
            respDto.setDatosImagen(Base64.getEncoder().encodeToString(empleado.getImagenEmpleado().getDatosImagen()));
            respDto.setTipoContenido(empleado.getImagenEmpleado().getTipoArchivo());
        }
        return respDto;
    }

}
