package com.gaston.sistema.turno.sistematunos_back.services;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.LocalDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.repositories.LocalRepository;

@Service
public class LocalServiceImp implements LocalService{

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private DuenoServiceImp duenoService;

    @Override
    @Transactional
    public  Map<String,Object> crearLocal(Local local,Long duenoId) {
        try{
            Dueno dueno = duenoService.findById(duenoId).orElseThrow( ()-> 
                                        new IllegalArgumentException("Dueno no encontrado"));

            dueno.setLocal(local);
            local.setDueno(dueno);

            Local nuevoLocal = localRepository.save(local);

            LocalDTO localDto = new LocalDTO(nuevoLocal);
            
            Map<String,Object> resp = new HashMap<>();
            resp.put("message ", "local creado correctamente");
            resp.put("local ", localDto );

            return resp;

        } catch (Exception e) {
            throw new RuntimeException("Error al crear el local: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Local editarLocal(Local local,Long duenoId) {
        Local localDb = obtenerPorDueno(duenoId);
        localDb.actualizarDatosLocal(local);
        return localRepository.save(localDb);
    }

    @Transactional(readOnly = true)
    public Local obtenerLocalPorId(Long id) {
        return localRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Local no encontrado con ese id "+id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Local obtenerPorDueno(Long duenoId) {
      return localRepository.findByDuenoId(duenoId).orElseThrow(() -> 
                                new IllegalArgumentException("local no econtrado con este Id de dueño: "+duenoId));
    }

    @Override
    public Page<LocalDTO> obtenerLocalesDisponibles(Pageable pageable) {
        try {
            Page<LocalDTO> paginaLocales = localRepository.findAll(pageable).map(local -> new LocalDTO(local));
            return paginaLocales;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Page<LocalDTO> obtnerLocalPorTipoOProvicinciaONombre(
        String tipoLocal, String provincia, String nombre, Pageable pageable) {

    try {
        Specification<Local> spec = Specification.allOf();
        
        if (tipoLocal != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("tipoLocal"), tipoLocal));
        }
        
        if (provincia != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("provincia"), provincia));
        }
        
        if (nombre != null) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
        }
        
        Page<Local> paginaLocales = localRepository.findAll(spec, pageable);
        
        return paginaLocales.map(LocalDTO::new);
                
    } catch (Exception e) {
        throw new RuntimeException("Error al obtener locales paginados: " + e.getMessage());
    }
    }

}
