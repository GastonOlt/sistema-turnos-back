package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ChangePasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.OwnerProfileDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.OwnerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/owner/profile")
@Tag(name = "Owner - Profile", description = "Owner profile management: view, edit and change password.")
public class OwnerProfileController {

    private final OwnerService ownerService;

    public OwnerProfileController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Operation(summary = "Get own profile", description = "Returns the authenticated owner's profile data.")
    @GetMapping
    public ResponseEntity<OwnerProfileDTO> getProfile(@AuthenticationPrincipal UserPrincipal user) {
        OwnerProfileDTO profile = ownerService.getProfile(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    @Operation(summary = "Update own profile", description = "Allows the owner to update their name, last name and email.")
    @PutMapping
    public ResponseEntity<OwnerProfileDTO> updateProfile(@AuthenticationPrincipal UserPrincipal user,
                                                         @Valid @RequestBody OwnerProfileDTO profileDTO) {
        OwnerProfileDTO updated = ownerService.updateProfile(user.getId(), profileDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @Operation(summary = "Change password", description = "Requires the current password before setting the new one.")
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserPrincipal user,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        ownerService.changePassword(user.getId(), request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
