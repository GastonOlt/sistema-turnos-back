package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.Horario;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.repositories.HorarioRepository;

import com.gaston.sistema.turno.sistematunos_back.dto.HorarioDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.HorarioRequestDTO;

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
        public HorarioDTO crearHorarioLocal(HorarioRequestDTO horarioDto, Long duenoId) {
                Local localDb = localService.obtenerPorDueno(duenoId);

                Horario horario = new Horario();
                horario.setDiaSemana(horarioDto.getDiaSemana());
                horario.setHorarioApertura(horarioDto.getHorarioApertura());
                horario.setHorarioCierre(horarioDto.getHorarioCierre());
                horario.setActivo(horarioDto.isActivo());

                localDb.getHorarios().add(horario);
                horario.setLocal(localDb);

                Horario nuevoHorario = horarioRepository.save(horario);
                return new HorarioDTO(nuevoHorario);
        }

        @Override
        @Transactional
        public HorarioDTO editarHorarioLocal(HorarioRequestDTO horarioDto, Long horarioId, Long duenoId) {
                Horario horarioDb = horarioRepository.findById(horarioId)
                                .orElseThrow(() -> new IllegalArgumentException("error al enocntrar  el horario"));
                if (!horarioDb.getLocal().getDueno().getId().equals(duenoId)) {
                        throw new AccessDeniedException("No tienes permisos para editar este horario");
                }
                horarioDb.setActivo(horarioDto.isActivo());
                horarioDb.setDiaSemana(horarioDto.getDiaSemana());
                horarioDb.setHorarioApertura(horarioDto.getHorarioApertura());
                horarioDb.setHorarioCierre(horarioDto.getHorarioCierre());

                Horario horarioActualizado = horarioRepository.save(horarioDb);
                return new HorarioDTO(horarioActualizado);
        }

        @Override
        @Transactional(readOnly = true)
        public List<HorarioDTO> obtenerHorarios(Long duenoId) {
                Local localDb = localService.obtenerPorDueno(duenoId);
                List<Horario> horarios = localDb.getHorarios();
                return horarios.stream().map(HorarioDTO::new).toList();
        }

        @Override
        @Transactional(readOnly = true)
        public HorarioDTO obtenerHorario(Long horarioId, Long duenoId) {
                Horario horarioDb = horarioRepository.findById(horarioId)
                                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
                if (!horarioDb.getLocal().getDueno().getId().equals(duenoId)) {
                        throw new AccessDeniedException("No tienes permisos ver este horario");
                }
                return new HorarioDTO(horarioDb);
        }

        @Override
        @Transactional
        public void eliminarHorarioLocal(Long horarioId, Long duenoId) {
                Horario horarioDb = horarioRepository.findById(horarioId)
                                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));

                if (!horarioDb.getLocal().getDueno().getId().equals(duenoId)) {
                        throw new AccessDeniedException("No tienes permisos eliminar este horario");
                }
                horarioRepository.delete(horarioDb);
        }

        //////// EMPLEADO ////////
        ///
        @Override
        @Transactional
        public HorarioDTO crearHorarioEmpleado(HorarioRequestDTO horarioDto, Long empleadoId) {
                Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(empleadoId);

                Horario horario = new Horario();
                horario.setDiaSemana(horarioDto.getDiaSemana());
                horario.setHorarioApertura(horarioDto.getHorarioApertura());
                horario.setHorarioCierre(horarioDto.getHorarioCierre());
                horario.setActivo(horarioDto.isActivo());

                empleadoDb.getHorarios().add(horario);
                horario.setEmpleado(empleadoDb);

                Horario nuevoHorario = horarioRepository.save(horario);
                return new HorarioDTO(nuevoHorario);
        }

        @Override
        @Transactional
        public HorarioDTO editarHorarioEmpleado(HorarioRequestDTO horarioDto, Long horarioId, Long empleadoId) {
                Horario horarioDb = horarioRepository.findById(horarioId)
                                .orElseThrow(() -> new IllegalArgumentException("error al obtnere el horario"));
                if (!horarioDb.getEmpleado().getId().equals(empleadoId)) {
                        throw new AccessDeniedException("no tienes permisos para editar este horario");
                }
                horarioDb.setActivo(horarioDto.isActivo());
                horarioDb.setDiaSemana(horarioDto.getDiaSemana());
                horarioDb.setHorarioApertura(horarioDto.getHorarioApertura());
                horarioDb.setHorarioCierre(horarioDto.getHorarioCierre());

                Horario horarioActualizado = horarioRepository.save(horarioDb);
                return new HorarioDTO(horarioActualizado);
        }

        @Override
        @Transactional(readOnly = true)
        public HorarioDTO obtenerHorarioEmpleado(Long horarioId, Long empleadoId) {
                Horario horarioDb = horarioRepository.findById(horarioId)
                                .orElseThrow(() -> new IllegalArgumentException("error al obtnere el horario"));
                if (!horarioDb.getEmpleado().getId().equals(empleadoId)) {
                        throw new AccessDeniedException("no tienes permisos para editar este horario");
                }
                return new HorarioDTO(horarioDb);
        }

        @Override
        @Transactional(readOnly = true)
        public List<HorarioDTO> obtenerHorariosEmpleado(Long empleadoId) {
                Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(empleadoId);
                List<Horario> horarios = empleadoDb.getHorarios();
                return horarios.stream().map(HorarioDTO::new).toList();
        }

        @Override
        @Transactional
        public void eliminarHorarioEmpleado(Long horarioId, Long empleadoId) {
                Horario horarioDb = horarioRepository.findById(horarioId)
                                .orElseThrow(() -> new IllegalArgumentException("error al obtnere el horario"));
                if (!horarioDb.getEmpleado().getId().equals(empleadoId)) {
                        throw new AccessDeniedException("no tienes permisos para editar este horario");
                }
                horarioRepository.delete(horarioDb);
        }
}
