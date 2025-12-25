package com.gaston.sistema.turno.sistematunos_back.services;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;

public interface LocalService {
        LocalDTO crearLocal(Local local,Long duenoId);
        Local editarLocal(Local local,Long duenoId);
        Local obtenerLocalPorId(Long id);
        Local obtenerPorDueno(Long duenoId);    

        Page<LocalDTO> obtenerLocales(String tipoLocal, String Provincia,String nombre,Pageable pageable);
}
