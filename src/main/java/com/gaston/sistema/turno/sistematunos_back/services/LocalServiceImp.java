package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.EmpleadoResumenDto;
import com.gaston.sistema.turno.sistematunos_back.dto.HorarioDto;
import com.gaston.sistema.turno.sistematunos_back.dto.ImagenDto;
import com.gaston.sistema.turno.sistematunos_back.dto.LocalDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.LocalDetalleDto;
import com.gaston.sistema.turno.sistematunos_back.dto.LocalInfoBasicaDto;
import com.gaston.sistema.turno.sistematunos_back.dto.LocalResumenDto;
import com.gaston.sistema.turno.sistematunos_back.dto.ServicioDto;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.repositories.LocalRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ReseniaRepository;

@Service
public class LocalServiceImp implements LocalService {

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private DuenoServiceImp duenoService;

    @Autowired
    private ReseniaRepository reseniaRepository;

    @Override
    @Transactional
    public Map<String, Object> crearLocal(Local local, Long duenoId) {
        try {
            Dueno dueno = duenoService.findById(duenoId)
                    .orElseThrow(() -> new IllegalArgumentException("Dueno no encontrado"));

            dueno.setLocal(local);
            local.setDueno(dueno);

            Local nuevoLocal = localRepository.save(local);

            LocalDTO localDto = new LocalDTO(nuevoLocal);

            Map<String, Object> resp = new HashMap<>();
            resp.put("message ", "local creado correctamente");
            resp.put("local ", localDto);

            return resp;

        } catch (Exception e) {
            throw new RuntimeException("Error al crear el local: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Local editarLocal(Local local, Long duenoId) {
        Local localDb = obtenerPorDuenoEntidad(duenoId);
        localDb.actualizarDatosLocal(local);
        return localRepository.save(localDb);
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDetalleDto obtenerLocalPorId(Long id) {
        Local local = localRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Local no encontrado con ese id " + id));

        LocalDetalleDto dto = new LocalDetalleDto();
        dto.setId(local.getId());
        dto.setNombre(local.getNombre());
        dto.setDescripcion(local.getDescripcion());
        dto.setDireccion(local.getDireccion());
        dto.setProvincia(local.getProvincia());
        dto.setTelefono(local.getTelefono());
        dto.setTipoLocal(local.getTipoLocal());
        dto.setLatitud(local.getLatitud());
        dto.setLongitud(local.getLongitud());

        // Dueño → objeto plano
        if (local.getDueno() != null) {
            dto.setDueno(new LocalDetalleDto.DuenoInfo(
                    local.getDueno().getNombre(),
                    local.getDueno().getApellido()));
        }

        // Imágenes → ImagenDto (base64 en constructor)
        dto.setImagenes(local.getImagenes().stream()
                .map(img -> new ImagenDto(img.getId(), img.getDatosImagen(), img.getTipoArchivo(),
                        img.getNombreArchivo()))
                .collect(Collectors.toList()));

        // Horarios → HorarioDto
        dto.setHorarios(local.getHorarios().stream().map(h -> {
            HorarioDto hDto = new HorarioDto();
            hDto.setId(h.getId());
            hDto.setDiaSemana(h.getDiaSemana());
            hDto.setHorarioApertura(h.getHorarioApertura() != null ? h.getHorarioApertura().toString() : null);
            hDto.setHorarioCierre(h.getHorarioCierre() != null ? h.getHorarioCierre().toString() : null);
            hDto.setActivo(h.isActivo());
            return hDto;
        }).collect(Collectors.toList()));

        // Servicios → ServicioDto
        dto.setServicios(local.getServicios().stream().map(s -> {
            ServicioDto sDto = new ServicioDto();
            sDto.setId(s.getId());
            sDto.setNombre(s.getNombre());
            sDto.setDescripcion(s.getDescripcion());
            sDto.setTiempo(s.getTiempo());
            sDto.setPrecio(s.getPrecio());
            return sDto;
        }).collect(Collectors.toList()));

        // Empleados → EmpleadoResumenDto
        dto.setEmpleados(local.getEmpleados().stream().map(emp -> {
            EmpleadoResumenDto eDto = new EmpleadoResumenDto();
            eDto.setId(emp.getId());
            eDto.setNombre(emp.getNombre());
            eDto.setApellido(emp.getApellido());
            eDto.setEmail(emp.getEmail());
            eDto.setEspecialidad(emp.getEspecialidad());
            eDto.setDueno(emp.isDueno());
            if (emp.getImagenEmpleado() != null) {
                eDto.setImagenEmpleado(new ImagenDto(
                        emp.getImagenEmpleado().getId(),
                        emp.getImagenEmpleado().getDatosImagen(),
                        emp.getImagenEmpleado().getTipoArchivo(),
                        emp.getImagenEmpleado().getNombreArchivo()));
            }
            return eDto;
        }).collect(Collectors.toList()));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Local obtenerLocalEntidadPorId(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Local no encontrado con ese id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public LocalInfoBasicaDto obtenerPorDueno(Long duenoId) {
        Local local = localRepository.findByDuenoId(duenoId)
                .orElseThrow(() -> new IllegalArgumentException("local no econtrado con este Id de dueño: " + duenoId));

        LocalInfoBasicaDto dto = new LocalInfoBasicaDto();
        dto.setId(local.getId());
        dto.setNombre(local.getNombre());
        dto.setDireccion(local.getDireccion());
        dto.setTelefono(local.getTelefono());
        dto.setProvincia(local.getProvincia());
        dto.setTipoLocal(local.getTipoLocal());
        dto.setDescripcion(local.getDescripcion());
        dto.setLatitud(local.getLatitud());
        dto.setLongitud(local.getLongitud());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Local obtenerPorDuenoEntidad(Long duenoId) {
        return localRepository.findByDuenoId(duenoId)
                .orElseThrow(() -> new IllegalArgumentException("local no econtrado con este Id de dueño: " + duenoId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LocalResumenDto> obtenerLocalesDisponibles(Pageable pageable) {
        try {
            return localRepository.findAll(pageable).map(this::toResumen);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LocalResumenDto> obtnerLocalPorTipoOProvicinciaONombre(
            String tipoLocal, String provincia, String nombre, Pageable pageable) {

        try {
            Specification<Local> spec = Specification.allOf();

            if (tipoLocal != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("tipoLocal"), tipoLocal));
            }

            if (provincia != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("provincia"), provincia));
            }

            if (nombre != null) {
                spec = spec.and(
                        (root, query, cb) -> cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }

            Page<Local> paginaLocales = localRepository.findAll(spec, pageable);

            return paginaLocales.map(this::toResumen);

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener locales paginados: " + e.getMessage());
        }
    }

    /**
     * Convierte un Local a LocalResumenDto (ligero, sin colecciones pesadas).
     */
    private LocalResumenDto toResumen(Local local) {
        LocalResumenDto dto = new LocalResumenDto();
        dto.setId(local.getId());
        dto.setNombre(local.getNombre());
        dto.setDireccion(local.getDireccion());
        dto.setProvincia(local.getProvincia());
        dto.setTipoLocal(local.getTipoLocal());
        dto.setTelefono(local.getTelefono());

        // Nombre del dueño (lazy, pero 1 query simple)
        try {
            if (local.getDueno() != null) {
                dto.setNombreDueno(local.getDueno().getNombre());
            }
        } catch (Exception e) {
            dto.setNombreDueno("N/A");
        }

        // Promedio de calificación
        Double promedio = reseniaRepository.obtenerPromedioCalificacion(local.getId());
        dto.setPromedioCalificacion(promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0);

        // Solo la primera imagen (evita cargar todas las BYTEA)
        try {
            if (!local.getImagenes().isEmpty()) {
                ImagenLocal img = local.getImagenes().get(0);
                if (img.getDatosImagen() != null) {
                    dto.setImagenPrincipal(Base64.getEncoder().encodeToString(img.getDatosImagen()));
                    dto.setTipoContenidoImagen(img.getTipoArchivo());
                }
            }
        } catch (Exception e) {
            // Sin imagen, no pasa nada
        }

        return dto;
    }

}
