package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.dto.ReseniaRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ReseniaResponseDTO;

public interface ReseniaService {
    
    ReseniaResponseDTO publicarResenia(Long clienteId, ReseniaRequestDTO request);
    List<ReseniaResponseDTO> obtenerReseniasPorLocal(Long localId);
    Double obtenerPromedioLocal(Long localId);
}
