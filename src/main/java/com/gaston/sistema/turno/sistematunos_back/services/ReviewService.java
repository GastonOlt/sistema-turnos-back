package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.dto.ReviewRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ReviewResponseDTO;

public interface ReviewService {
    ReviewResponseDTO publishReview(Long clientId, ReviewRequestDTO request);
    List<ReviewResponseDTO> getReviewsByShop(Long shopId);
    Double getShopAverageRating(Long shopId);
}
