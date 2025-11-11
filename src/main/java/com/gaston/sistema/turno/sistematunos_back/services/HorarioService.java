package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.entities.Horario;

public interface HorarioService {
    Horario crearHorarioLocal(Horario horario,Long duenoId);
    Horario crearHorarioEmpleado(Horario horario,Long empleadoId);

    Horario editarHorarioLocal(Horario horario,Long horarioId,Long duenoId);
    Horario editarHorarioEmpleado(Horario horario,Long horarioId,Long empleadoId);

    Horario obtenerHorario(Long horarioId,Long duenoId);
    Horario obtenerHorarioEmpleado(Long horarioId,Long empleadoId);

    List<Horario> obtenerHorarios(Long duenoId);
    List<Horario> obtenerHorariosEmpleado(Long empleadoId);

    void eliminarHorarioLocal(Long horarioId,Long duenoId);
    void eliminarHorarioEmpleado(Long horarioId,Long empleadoId);
}
