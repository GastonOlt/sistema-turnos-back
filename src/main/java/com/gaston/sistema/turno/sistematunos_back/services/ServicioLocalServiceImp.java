package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;
import com.gaston.sistema.turno.sistematunos_back.repositories.ServicioLocalRepository;

@Service
public class ServicioLocalServiceImp implements ServicioLocalService{

    @Autowired
    private ServicioLocalRepository servicioLocalRepository;

    @Autowired
    private LocalService localService;

    @Override
    public ServicioLocal crearServicio(ServicioLocal servicio, Long duenoId) {
            Local localDb =localService.obtenerPorDueno(duenoId);
            servicio.setLocal(localDb);
            localDb.getServicios().add(servicio);
            ServicioLocal nuevoServicio = servicioLocalRepository.save(servicio);
            return nuevoServicio;
    }

    @Override
    public ServicioLocal editarServicio(ServicioLocal servicio, Long servicioId,Long duenoId) {  
            ServicioLocal servicioDb = servicioLocalRepository.findById(servicioId).orElseThrow(()->
                                         new IllegalArgumentException("error al encontar el servicio con id: "+servicioId));

            if(!servicioDb.getLocal().getDueno().getId().equals(duenoId)){
                 throw new AccessDeniedException("No tienes permisos para editar este servicio");
            }
            servicioDb.setDescripcion(servicio.getDescripcion());
            servicioDb.setNombre(servicio.getNombre());
            servicioDb.setPrecio(servicio.getPrecio());
            servicioDb.setTiempo(servicio.getTiempo());
            return servicioLocalRepository.save(servicioDb);
    }

    @Override
    public ServicioLocal obtenerServicio(Long servicioId,Long duenoId) {
        ServicioLocal servicioDb = servicioLocalRepository.findById(servicioId).orElseThrow(()->
                                new IllegalArgumentException("error al encontar el servicio con id: "+servicioId));

        if(!servicioDb.getLocal().getDueno().getId().equals(duenoId)){
            throw new AccessDeniedException("no tienes permisos para ver este servicio");
        }
        return servicioDb;
    }
    
    @Override
    public List<ServicioLocal> obtenerServicios(Long duenoId) {
         Local localDb = localService.obtenerPorDueno(duenoId);
         List<ServicioLocal> serviciosLocal = localDb.getServicios();
         return serviciosLocal;
    }

    @Override
    public void eliminarServicio(Long servicioId, Long duenoId) {
        ServicioLocal servicioDb = servicioLocalRepository.findById(servicioId).orElseThrow(()->
                                    new IllegalArgumentException("no se encontro el servicio con ese id: "+servicioId));
        if(!servicioDb.getLocal().getDueno().getId().equals(duenoId)){
            throw new AccessDeniedException("no tienes permitido eliminar este servcio");
        }
        servicioLocalRepository.delete(servicioDb);
    }

    @Override
    public ServicioLocal obtenerServicioEntity(Long servicioId) {
           ServicioLocal servicioDb = servicioLocalRepository.findById(servicioId).orElseThrow(()->
                                new IllegalArgumentException("error al encontar el servicio con id: "+servicioId));

        return servicioDb;
    }

}