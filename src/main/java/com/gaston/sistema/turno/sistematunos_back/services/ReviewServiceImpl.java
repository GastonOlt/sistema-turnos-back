package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.ReviewRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ReviewResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus;
import com.gaston.sistema.turno.sistematunos_back.entities.Review;
import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ReviewRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.AppointmentRepository;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final ShopRepository shopRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, AppointmentRepository appointmentRepository, ShopRepository shopRepository) {
        this.reviewRepository = reviewRepository;
        this.appointmentRepository = appointmentRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    @Transactional
    public ReviewResponseDTO publishReview(Long clientId, ReviewRequestDTO request) {
        Appointment currentAppointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("El turno no existe"));

        if (!currentAppointment.getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException("Este turno no corresponde al cliente autenticado");
        }

        if (currentAppointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Solo se pueden reseñar turnos FINALIZADOS");
        }

        Long shopId = currentAppointment.getShop().getId();

        Optional<Review> existingReview = reviewRepository.findByShopIdAndClientId(shopId, clientId);

        Review review;
        if (existingReview.isPresent()) {
            review = existingReview.get();
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            review.setAppointment(currentAppointment);
        } else {
            review = new Review();
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            review.setShop(currentAppointment.getShop());
            review.setClient(currentAppointment.getClient());
            review.setAppointment(currentAppointment);
        }

        Review savedReview = reviewRepository.save(review);
        updateShopAverageRating(savedReview.getShop().getId());

        return convertToDTO(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByShop(Long shopId) {
        return reviewRepository.findByShopIdWithRelations(shopId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getShopAverageRating(Long shopId) {
        Double average = reviewRepository.getAverageRating(shopId);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
    }

    private void updateShopAverageRating(Long shopId) {
        Double newAverage = reviewRepository.getAverageRating(shopId);
        if (newAverage == null) newAverage = 0.0;

        newAverage = Math.round(newAverage * 10.0) / 10.0;

        shopRepository.updateAverageRating(shopId, newAverage);
    }

    private ReviewResponseDTO convertToDTO(Review r) {
        String clientName = (r.getClient() != null)
            ? r.getClient().getName() + " " + r.getClient().getLastName()
            : "Anónimo";

        return new ReviewResponseDTO(
            r.getId(),
            r.getRating(),
            r.getComment(),
            clientName,
            r.getLastModifiedDate(),
            r.getAppointment().getService().getName()
        );
    }
}
