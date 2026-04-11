package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.entities.Owner;
import com.gaston.sistema.turno.sistematunos_back.repositories.OwnerRepository;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
      this.ownerRepository = ownerRepository;
    }

    @Override
    @Transactional
    public Owner createOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Override
    public Optional<Owner> findByEmail(String email) {
      return ownerRepository.findByEmail(email);
    }

    @Override
    public Optional<Owner> findById(Long id) {
      return ownerRepository.findById(id);
    }
}
