package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;

public interface LocalService {
        Map<String,Object> crearLocal(Local local,Long duenoId);
        Local editarLocal(Local local,Long duenoId);
        Local obtenerLocalPorId(Long id);
        Local obtenerPorDueno(Long duenoId);    

        Page<LocalDTO> obtenerLocalesDisponibles(Pageable pageable);
        Page<LocalDTO> obtnerLocalPorTipoOProvicinciaONombre(String tipo, String Provincia,String nombre,Pageable pageable);
}
