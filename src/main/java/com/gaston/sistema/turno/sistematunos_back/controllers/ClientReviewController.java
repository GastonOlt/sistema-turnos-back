package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ReviewRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ReviewResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ReviewService;

import jakarta.validation.Valid;

/**
 * Handles client-side review operations (write).
 * Public read endpoints (GET) live in PublicShopController under /public/shops.
 */
@RestController
@RequestMapping("/client/reviews")
@PreAuthorize("hasRole('CLIENTE')")
public class ClientReviewController {

    private final ReviewService reviewService;

    public ClientReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> publishReview(@AuthenticationPrincipal UserPrincipal user,
                                                           @Valid @RequestBody ReviewRequestDTO request) {
        Long clientId = user.getId();
        ReviewResponseDTO review = reviewService.publishReview(clientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }
}
