package com.gaston.sistema.turno.sistematunos_back.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.LocalDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;

public interface LocalService {
        LocalDTO crearLocal(LocalRequestDTO local, Long duenoId);

        LocalDTO editarLocal(LocalRequestDTO local, Long duenoId);

        Local obtenerLocalPorId(Long id, Long duenoId);

        Local obtenerLocalEntity(Long id);

        LocalDTO obtenerLocalPublicoPorId(Long id);

        Local obtenerPorDueno(Long duenoId);

        Page<LocalDTO> obtenerLocales(String tipoLocal, String Provincia, String nombre, Pageable pageable);
}
