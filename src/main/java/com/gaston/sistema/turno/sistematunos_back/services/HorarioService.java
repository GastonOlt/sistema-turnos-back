package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.dto.HorarioDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.HorarioRequestDTO;

public interface HorarioService {
    HorarioDTO crearHorarioLocal(HorarioRequestDTO horario, Long duenoId);

    HorarioDTO crearHorarioEmpleado(HorarioRequestDTO horario, Long empleadoId);

    HorarioDTO editarHorarioLocal(HorarioRequestDTO horario, Long horarioId, Long duenoId);

    HorarioDTO editarHorarioEmpleado(HorarioRequestDTO horario, Long horarioId, Long empleadoId);

    HorarioDTO obtenerHorario(Long horarioId, Long duenoId);

    HorarioDTO obtenerHorarioEmpleado(Long horarioId, Long empleadoId);

    List<HorarioDTO> obtenerHorarios(Long duenoId);

    List<HorarioDTO> obtenerHorariosEmpleado(Long empleadoId);

    void eliminarHorarioLocal(Long horarioId, Long duenoId);

    void eliminarHorarioEmpleado(Long horarioId, Long empleadoId);
}
