package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDate;
import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.dto.SlotDisponibleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoResponseDTO;

public interface TurnoService {
    
    List<SlotDisponibleDTO> obtenerSlotsDisponibles(Long localId, Long empleadoId, Long servicioId, LocalDate fecha);
    TurnoResponseDTO reservarTurno(Long clienteId , TurnoRequestDTO turnoRequest);
}
