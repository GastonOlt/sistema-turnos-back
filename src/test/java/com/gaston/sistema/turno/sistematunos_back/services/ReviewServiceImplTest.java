package com.gaston.sistema.turno.sistematunos_back.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gaston.sistema.turno.sistematunos_back.dto.ReviewRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ReviewResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus;
import com.gaston.sistema.turno.sistematunos_back.entities.Client;
import com.gaston.sistema.turno.sistematunos_back.entities.Review;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.repositories.AppointmentRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ReviewRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ClientService clientService;
    @Mock
    private ShopService shopService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void publishReview_NewReview_Success() {
        // Arrange
        Long clientId = 5L;
        Long appointmentId = 100L;
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setAppointmentId(appointmentId);
        request.setRating(5.0);
        request.setComment("Excellent service");

        Client client = new Client();
        client.setId(clientId);
        client.setName("Test Client");

        Shop shop = new Shop();
        shop.setId(1L);

        ShopOffering service = new ShopOffering();
        service.setName("Test Service");
        
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setClient(client);
        appointment.setShop(shop);
        appointment.setService(service);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        // Required logic for publishReview
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(reviewRepository.findByShopIdAndClientId(1L, clientId)).thenReturn(Optional.empty());
        when(reviewRepository.getAverageRating(1L)).thenReturn(5.0); // For the internal updateShopAverageRating

        Review savedReview = new Review();
        savedReview.setId(50L);
        savedReview.setAppointment(appointment);
        savedReview.setShop(shop);
        savedReview.setClient(client);
        savedReview.setRating(5.0);
        savedReview.setComment("Excellent service");

        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return what was passed to save
        
        // Act
        ReviewResponseDTO response = reviewService.publishReview(clientId, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getRating()).isEqualTo(5.0);
        assertThat(response.getComment()).isEqualTo("Excellent service");

        verify(reviewRepository).save(any(Review.class));
        verify(shopRepository).updateAverageRating(eq(1L), anyDouble());
    }

    @Test
    void publishReview_FailsIfAppointmentNotCompleted() {
        // Arrange
        Long clientId = 5L;
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setAppointmentId(100L);

        Client client = new Client();
        client.setId(clientId);

        Appointment appointment = new Appointment();
        appointment.setId(100L);
        appointment.setClient(client);
        appointment.setStatus(AppointmentStatus.PENDING); // Not completed

        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(appointment));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.publishReview(clientId, request);
        });
    }
}
