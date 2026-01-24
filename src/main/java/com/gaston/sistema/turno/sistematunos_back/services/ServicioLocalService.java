package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.dto.ServicioLocalDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ServicioLocalRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;

public interface ServicioLocalService {
    ServicioLocalDTO crearServicio(ServicioLocalRequestDTO servicio, Long duenoId);

    ServicioLocalDTO editarServicio(ServicioLocalRequestDTO servicio, Long servicioId, Long duenoId);

    ServicioLocalDTO obtenerServicio(Long servicioId, Long duenoId);

    ServicioLocal obtenerServicioEntity(Long servicioId);

    List<ServicioLocalDTO> obtenerServicios(Long duenoId);

    void eliminarServicio(Long servicioId, Long duenoId);
}
