package com.gaston.sistema.turno.sistematunos_back.services;



import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.Horario;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.repositories.HorarioRepository;

@Service
public class HorarioServiceImp implements HorarioService {

    private final HorarioRepository horarioRepository;
    private final LocalService localService;
    private final EmpleadoService empleadoService;

    public HorarioServiceImp(HorarioRepository horarioRepository, LocalService localService,
            EmpleadoService empleadoService) {
        this.horarioRepository = horarioRepository;
        this.localService = localService;
        this.empleadoService = empleadoService;
    }


    @Override
    @Transactional
    public Horario crearHorarioLocal(Horario horario, Long duenoId) {
            Local localDb = localService.obtenerPorDueno(duenoId);

            localDb.getHorarios().add(horario);
            horario.setLocal(localDb);

             return horarioRepository.save(horario);
        }
        
   
    @Override
    @Transactional
    public Horario editarHorarioLocal(Horario horario, Long horarioId, Long duenoId) {
            Horario horarioDb = horarioRepository.findById(horarioId).orElseThrow(()-> 
                                                new IllegalArgumentException("error al enocntrar  el horario"));
            if (!horarioDb.getLocal().getDueno().getId().equals(duenoId)) {
                 throw new AccessDeniedException("No tienes permisos para editar este horario");
            }
            horarioDb.setActivo(horario.isActivo());
            horarioDb.setDiaSemana(horario.getDiaSemana());
            horarioDb.setHorarioApertura(horario.getHorarioApertura());
            horarioDb.setHorarioCierre(horario.getHorarioCierre());
            
            return horarioRepository.save(horarioDb);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horario> obtenerHorarios(Long duenoId) {
        Local localDb = localService.obtenerPorDueno(duenoId);
        List<Horario> horarios = localDb.getHorarios(); 
        return horarios;
    }

    @Override
    @Transactional(readOnly = true)
    public Horario obtenerHorario(Long horarioId, Long duenoId) {
             Horario horarioDb = horarioRepository.findById(horarioId).orElseThrow(()-> 
                                                            new IllegalArgumentException("Horario no encontrado"));
             if(!horarioDb.getLocal().getDueno().getId().equals(duenoId)){
                throw new AccessDeniedException("No tienes permisos ver este horario");
             }
             return horarioDb;
    }
    
    @Override
    @Transactional
    public void eliminarHorarioLocal(Long horarioId, Long duenoId) {
            Horario horarioDb = horarioRepository.findById(horarioId).orElseThrow(()-> 
                                                            new IllegalArgumentException("Horario no encontrado"));

            if(!horarioDb.getLocal().getDueno().getId().equals(duenoId)){
                throw new AccessDeniedException("No tienes permisos eliminar este horario");
             }
            horarioRepository.delete(horarioDb);
    }

    //////// EMPLEADO   ////////
    /// 
    @Override
    @Transactional 
    public Horario crearHorarioEmpleado(Horario horario, Long empleadoId) {
            Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(empleadoId);
            empleadoDb.getHorarios().add(horario);
            horario.setEmpleado(empleadoDb);

            return horarioRepository.save(horario);
    }

    @Override
    @Transactional
    public Horario editarHorarioEmpleado(Horario horario, Long horarioId, Long empleadoId) {
            Horario horarioDb = horarioRepository.findById(horarioId).orElseThrow(()->
                                                             new IllegalArgumentException("error al obtnere el horario"));
            if(!horarioDb.getEmpleado().getId().equals(empleadoId)){
                throw new AccessDeniedException("no tienes permisos para editar este horario");
            }
            horarioDb.setActivo(horario.isActivo());
            horarioDb.setDiaSemana(horario.getDiaSemana());
            horarioDb.setHorarioApertura(horario.getHorarioApertura());
            horarioDb.setHorarioCierre(horario.getHorarioCierre());
            
            return horarioRepository.save(horarioDb);
    }


    @Override
    @Transactional(readOnly = true)
    public Horario obtenerHorarioEmpleado(Long horarioId, Long empleadoId) {
         Horario horarioDb = horarioRepository.findById(horarioId).orElseThrow(()->
                                                             new IllegalArgumentException("error al obtnere el horario"));
            if(!horarioDb.getEmpleado().getId().equals(empleadoId)){
                throw new AccessDeniedException("no tienes permisos para editar este horario");
             }
           return horarioDb;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Horario> obtenerHorariosEmpleado(Long empleadoId) {
            Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(empleadoId);
            List<Horario> horarios = empleadoDb.getHorarios();
            return horarios;
    }

    @Override
    @Transactional
    public void eliminarHorarioEmpleado(Long horarioId, Long empleadoId) {
           Horario horarioDb = horarioRepository.findById(horarioId).orElseThrow(()->
                                                             new IllegalArgumentException("error al obtnere el horario"));
            if(!horarioDb.getEmpleado().getId().equals(empleadoId)){
                throw new AccessDeniedException("no tienes permisos para editar este horario");
             }
            horarioRepository.delete(horarioDb);
    }
}
