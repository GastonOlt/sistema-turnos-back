package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Optional;

import com.gaston.sistema.turno.sistematunos_back.dto.ChangePasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.OwnerProfileDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Owner;

public interface OwnerService {
    Owner createOwner(Owner owner);
    Optional<Owner> findByEmail(String email);
    Optional<Owner> findById(Long id);
    OwnerProfileDTO getProfile(Long ownerId);
    OwnerProfileDTO updateProfile(Long ownerId, OwnerProfileDTO profileDTO);
    void changePassword(Long ownerId, ChangePasswordRequest request);
}
