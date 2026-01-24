package com.gaston.sistema.turno.sistematunos_back.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.repositories.LocalRepository;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalRequestDTO;

@Service
public class LocalServiceImp implements LocalService {

    private final LocalRepository localRepository;
    private final DuenoServiceImp duenoService;

    public LocalServiceImp(LocalRepository localRepository, DuenoServiceImp duenoService) {
        this.localRepository = localRepository;
        this.duenoService = duenoService;
    }

    @Override
    @Transactional
    public LocalDTO crearLocal(LocalRequestDTO localDto, Long duenoId) {
        Dueno dueno = duenoService.findById(duenoId)
                .orElseThrow(() -> new IllegalArgumentException("Dueno no encontrado"));

        Local local = new Local();
        local.setNombre(localDto.getNombre());
        local.setDescripcion(localDto.getDescripcion());
        local.setProvincia(localDto.getProvincia());
        local.setTelefono(localDto.getTelefono());
        local.setLatitud(localDto.getLatitud());
        local.setLongitud(localDto.getLongitud());

        dueno.setLocal(local);
        local.setDueno(dueno);

        Local nuevoLocal = localRepository.save(local);

        return new LocalDTO(nuevoLocal);
    }

    @Override
    @Transactional
    public LocalDTO editarLocal(LocalRequestDTO localDto, Long duenoId) {
        Local localDb = obtenerPorDueno(duenoId);

        // Manual updates from DTO
        localDb.setNombre(localDto.getNombre());
        localDb.setDescripcion(localDto.getDescripcion());
        localDb.setProvincia(localDto.getProvincia());
        localDb.setTelefono(localDto.getTelefono());
        localDb.setLatitud(localDto.getLatitud());
        localDb.setLongitud(localDto.getLongitud());

        Local localActualizado = localRepository.save(localDb);
        return new LocalDTO(localActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Local obtenerLocalPorId(Long id, Long duenoId) {
        Local local = localRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Local no encontrado con ese id " + id));

        if (!local.getDueno().getId().equals(duenoId)) {
            throw new IllegalArgumentException("Acceso denegado: El local no pertenece al dueño autenticado");
        }
        return local;
    }

    @Override
    @Transactional(readOnly = true)
    public Local obtenerLocalEntity(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Local no encontrado con ese id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDTO obtenerLocalPublicoPorId(Long id) {
        Local local = localRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Local no encontrado con ese id " + id));
        return new LocalDTO(local);
    }

    @Override
    @Transactional(readOnly = true)
    public Local obtenerPorDueno(Long duenoId) {
        return localRepository.findByDuenoId(duenoId)
                .orElseThrow(() -> new IllegalArgumentException("local no econtrado con este Id de dueño: " + duenoId));
    }

    @Override
    public Page<LocalDTO> obtenerLocales(
            String tipoLocal, String provincia, String nombre, Pageable pageable) {

        if (tipoLocal == null && provincia == null && nombre == null) {
            return localRepository.findAll(pageable)
                    .map(this::convertirADTO);
        }

        Specification<Local> spec = Specification.allOf();

        if (tipoLocal != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipoLocal"), tipoLocal));
        }

        if (provincia != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("provincia"), provincia));
        }

        if (nombre != null) {
            spec = spec
                    .and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
        }

        return localRepository.findAll(spec, pageable)
                .map(this::convertirADTO);
    }

    private LocalDTO convertirADTO(Local local) {
        return new LocalDTO(local);
    }
}
