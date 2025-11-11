package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.repositories.DuenoRepository;

@Service
public class DuenoServiceImp implements DuenoService {

    @Autowired
    private DuenoRepository duenoRepository;

    @Override
    @Transactional
    public Dueno crearDueno(Dueno dueno) {
        return duenoRepository.save(dueno);
    }
    

    @Override
    public Optional<Dueno> findByEmail(String email) {
      return duenoRepository.findByEmail(email);
    }


    @Override
    public Optional<Dueno> findById(Long id) {
      return duenoRepository.findById(id);
    }

}
