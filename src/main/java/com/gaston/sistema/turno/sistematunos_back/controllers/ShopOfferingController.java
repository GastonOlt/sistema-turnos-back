package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ShopOfferingDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ShopOfferingRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ShopOfferingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/owner/services")
@Tag(name = "Service Management (Owner)", description = "CRUD for services offered by the shop")
public class ShopOfferingController {

    private final ShopOfferingService shopOfferingService;

    public ShopOfferingController(ShopOfferingService shopOfferingService) {
        this.shopOfferingService = shopOfferingService;
    }

    @Operation(summary = "Create Service", description = "Adds a new service to the authenticated owner's shop.")
    @PostMapping
    public ResponseEntity<ShopOfferingDTO> createService(@Valid @RequestBody ShopOfferingRequestDTO request,
                                                         @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        ShopOfferingDTO newService = shopOfferingService.createService(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newService);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShopOfferingDTO> editService(@PathVariable Long id,
                                                       @Valid @RequestBody ShopOfferingRequestDTO request,
                                                       @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        ShopOfferingDTO editedService = shopOfferingService.editService(request, id, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(editedService);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopOfferingDTO> getService(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        ShopOfferingDTO serviceDTO = shopOfferingService.getService(id, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(serviceDTO);
    }

    @GetMapping
    public ResponseEntity<List<ShopOfferingDTO>> getServices(@AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        List<ShopOfferingDTO> services = shopOfferingService.getServices(ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(services);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id,
                                              @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        shopOfferingService.deleteService(id, ownerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
