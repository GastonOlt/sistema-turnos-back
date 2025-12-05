package com.gaston.sistema.turno.sistematunos_back.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.dto.TurnoEmpleadoDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;

public interface TurnoEmpleadoService {
    List<TurnoEmpleadoDTO> listadoTurnoConfirmados(Long empleadoId);
    List<TurnoEmpleadoDTO> listadoTurnoPendientes(Long empleadoId);
    List<TurnoEmpleadoDTO> historialTurnos(Long empleadoId);
    void cancelarTurno(Long empleadoId, Long turnoId);
    void confirmarTurno(Long empleadoId, Long turnoId);
    BigDecimal calcularGanancias(Long empleadoId, LocalDate desde, LocalDate hasta);
    List<ServicioLocal> obtenerServiciosPorEmpleado(Long empleadoId);
    
}
