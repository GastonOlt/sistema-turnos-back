package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;
import com.gaston.sistema.turno.sistematunos_back.repositories.ServicioLocalRepository;

import com.gaston.sistema.turno.sistematunos_back.dto.ServicioLocalDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ServicioLocalRequestDTO;

@Service
public class ServicioLocalServiceImp implements ServicioLocalService {

    private final ServicioLocalRepository servicioLocalRepository;
    private final LocalService localService;

    public ServicioLocalServiceImp(ServicioLocalRepository servicioLocalRepository, LocalService localService) {
        this.servicioLocalRepository = servicioLocalRepository;
        this.localService = localService;
    }

    @Override
    @Transactional
    public ServicioLocalDTO crearServicio(ServicioLocalRequestDTO servicioDto, Long duenoId) {
        Local localDb = localService.obtenerPorDueno(duenoId);

        ServicioLocal servicio = new ServicioLocal();
        servicio.setNombre(servicioDto.getNombre());
        servicio.setDescripcion(servicioDto.getDescripcion());
        servicio.setPrecio(servicioDto.getPrecio());
        servicio.setTiempo(servicioDto.getTiempo());

        servicio.setLocal(localDb);
        localDb.getServicios().add(servicio);
        ServicioLocal nuevoServicio = servicioLocalRepository.save(servicio);
        return new ServicioLocalDTO(nuevoServicio);
    }

    @Override
    @Transactional
    public ServicioLocalDTO editarServicio(ServicioLocalRequestDTO servicioDto, Long servicioId, Long duenoId) {
        ServicioLocal servicioDb = servicioLocalRepository.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("error al encontar el servicio con id: " + servicioId));

        if (!servicioDb.getLocal().getDueno().getId().equals(duenoId)) {
            throw new AccessDeniedException("No tienes permisos para editar este servicio");
        }
        servicioDb.setDescripcion(servicioDto.getDescripcion());
        servicioDb.setNombre(servicioDto.getNombre());
        servicioDb.setPrecio(servicioDto.getPrecio());
        servicioDb.setTiempo(servicioDto.getTiempo());

        ServicioLocal servicioActualizado = servicioLocalRepository.save(servicioDb);
        return new ServicioLocalDTO(servicioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioLocalDTO obtenerServicio(Long servicioId, Long duenoId) {
        ServicioLocal servicioDb = servicioLocalRepository.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("error al encontar el servicio con id: " + servicioId));

        if (!servicioDb.getLocal().getDueno().getId().equals(duenoId)) {
            throw new AccessDeniedException("no tienes permisos para ver este servicio");
        }
        return new ServicioLocalDTO(servicioDb);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioLocalDTO> obtenerServicios(Long duenoId) {
        Local localDb = localService.obtenerPorDueno(duenoId);
        List<ServicioLocal> serviciosLocal = localDb.getServicios();
        return serviciosLocal.stream().map(ServicioLocalDTO::new).toList();
    }

    @Override
    public void eliminarServicio(Long servicioId, Long duenoId) {
        ServicioLocal servicioDb = servicioLocalRepository.findById(servicioId).orElseThrow(
                () -> new IllegalArgumentException("no se encontro el servicio con ese id: " + servicioId));
        if (!servicioDb.getLocal().getDueno().getId().equals(duenoId)) {
            throw new AccessDeniedException("no tienes permitido eliminar este servcio");
        }
        servicioLocalRepository.delete(servicioDb);
    }

    @Override
    public ServicioLocal obtenerServicioEntity(Long servicioId) {
        ServicioLocal servicioDb = servicioLocalRepository.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("error al encontar el servicio con id: " + servicioId));

        return servicioDb;
    }

}