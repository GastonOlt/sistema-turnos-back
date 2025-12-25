package com.gaston.sistema.turno.sistematunos_back.services;


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
import com.gaston.sistema.turno.sistematunos_back.repositories.ReseniaRepository;

@Service
public class LocalServiceImp implements LocalService{

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private DuenoServiceImp duenoService;

    @Autowired
    private ReseniaRepository reseniaRepository;

    @Override
    @Transactional
    public  LocalDTO crearLocal(Local local,Long duenoId) {
            Dueno dueno = duenoService.findById(duenoId).orElseThrow( ()-> 
                                        new IllegalArgumentException("Dueno no encontrado"));

            dueno.setLocal(local);
            local.setDueno(dueno);

            Local nuevoLocal = localRepository.save(local);

            return new LocalDTO(nuevoLocal);
    }

    @Override
    @Transactional
    public Local editarLocal(Local local,Long duenoId) {
        Local localDb = obtenerPorDueno(duenoId);
        localDb.actualizarDatosLocal(local);
        return localRepository.save(localDb);
    }

    @Override
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
    public Page<LocalDTO> obtenerLocales(
        String tipoLocal, String provincia, String nombre, Pageable pageable) {

        if (tipoLocal == null && provincia == null && nombre == null) {
        return localRepository.findAll(pageable)
                .map(this::convertirADTO); 
        }

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
        
       return localRepository.findAll(spec, pageable)
                .map(this::convertirADTO);
    }

    private LocalDTO convertirADTO(Local local) {
        LocalDTO dto = new LocalDTO(local);
        Double promedio = reseniaRepository.obtenerPromedioCalificacion(local.getId());
        dto.setPromedioCalificacion(promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0);
        return dto;
    }
}
