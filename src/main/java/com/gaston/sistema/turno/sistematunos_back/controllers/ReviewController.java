package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ReviewRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ReviewResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ReviewService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/client/reviews")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ReviewResponseDTO> publishReview(@AuthenticationPrincipal UserPrincipal user,
                                          @Valid @RequestBody ReviewRequestDTO request) {
        Long clientId = user.getId();
        ReviewResponseDTO review = reviewService.publishReview(clientId, request);
        return ResponseEntity.status(HttpStatus.OK).body(review);
    }

    @GetMapping("/public/shops/{shopId}/reviews")
    @SecurityRequirements()
    public ResponseEntity<List<ReviewResponseDTO>> getShopReviews(@PathVariable Long shopId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByShop(shopId);
        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }

    @GetMapping("/public/shops/{shopId}/reviews/average")
    @SecurityRequirements()
    public ResponseEntity<Double> getShopAverageRating(@PathVariable Long shopId) {
        Double average = reviewService.getShopAverageRating(shopId);
        return ResponseEntity.status(HttpStatus.OK).body(average);
    }
}
