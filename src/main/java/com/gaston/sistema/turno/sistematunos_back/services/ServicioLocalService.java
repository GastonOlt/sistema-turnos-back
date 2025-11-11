package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;

public interface ServicioLocalService {
    ServicioLocal crearServicio(ServicioLocal servicio , Long duenoId);
    ServicioLocal editarServicio(ServicioLocal servicio , Long servicioId,Long duenoId);
    ServicioLocal obtenerServicio(Long servicioId,Long duenoId);
    ServicioLocal obtenerServicioEntity(Long servicioId);
    List<ServicioLocal> obtenerServicios(Long duenoId);
    void eliminarServicio(Long servicioId,Long duenoId);
}
