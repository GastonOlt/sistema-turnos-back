package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.ChangePasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.OwnerProfileDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Owner;
import com.gaston.sistema.turno.sistematunos_back.repositories.OwnerRepository;
import com.gaston.sistema.turno.sistematunos_back.validation.EmailAlreadyExistsException;

@Service
public class OwnerServiceImpl implements OwnerService {

    private static final Logger log = LoggerFactory.getLogger(OwnerServiceImpl.class);

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;

    public OwnerServiceImpl(OwnerRepository ownerRepository, PasswordEncoder passwordEncoder) {
        this.ownerRepository = ownerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Owner createOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Owner> findByEmail(String email) {
        return ownerRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Owner> findById(Long id) {
        return ownerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerProfileDTO getProfile(Long ownerId) {
        Owner owner = getOwnerOrThrow(ownerId);
        return toProfileDTO(owner);
    }

    @Override
    @Transactional
    public OwnerProfileDTO updateProfile(Long ownerId, OwnerProfileDTO profileDTO) {
        Owner owner = getOwnerOrThrow(ownerId);

        // If email is changing, verify it is not already taken by another user
        if (!owner.getEmail().equalsIgnoreCase(profileDTO.getEmail())) {
            ownerRepository.findByEmail(profileDTO.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(ownerId)) {
                    throw new EmailAlreadyExistsException("Email is already in use by another account");
                }
            });
        }

        owner.setName(profileDTO.getName());
        owner.setLastName(profileDTO.getLastName());
        owner.setEmail(profileDTO.getEmail());

        Owner updated = ownerRepository.save(owner);
        log.info("Owner id={} updated their profile", ownerId);
        return toProfileDTO(updated);
    }

    @Override
    @Transactional
    public void changePassword(Long ownerId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }
        Owner owner = getOwnerOrThrow(ownerId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), owner.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        owner.setPassword(passwordEncoder.encode(request.getNewPassword()));
        ownerRepository.save(owner);
        log.info("Owner id={} changed their password", ownerId);
    }

    // ===================== PRIVATE =====================

    private Owner getOwnerOrThrow(Long ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));
    }

    private OwnerProfileDTO toProfileDTO(Owner owner) {
        OwnerProfileDTO dto = new OwnerProfileDTO();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setLastName(owner.getLastName());
        dto.setEmail(owner.getEmail());
        dto.setAvailableToAttend(owner.isAvailableToAttend());
        return dto;
    }
}
