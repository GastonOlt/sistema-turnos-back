package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalDetalleDto;
import com.gaston.sistema.turno.sistematunos_back.dto.LocalInfoBasicaDto;
import com.gaston.sistema.turno.sistematunos_back.dto.LocalResumenDto;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;

public interface LocalService {
        Map<String, Object> crearLocal(Local local, Long duenoId);

        Local editarLocal(Local local, Long duenoId);

        LocalDetalleDto obtenerLocalPorId(Long id);

        Local obtenerLocalEntidadPorId(Long id);

        LocalInfoBasicaDto obtenerPorDueno(Long duenoId);

        Local obtenerPorDuenoEntidad(Long duenoId);

        Page<LocalResumenDto> obtenerLocalesDisponibles(Pageable pageable);

        Page<LocalResumenDto> obtnerLocalPorTipoOProvicinciaONombre(String tipo, String Provincia, String nombre,
                        Pageable pageable);
}
