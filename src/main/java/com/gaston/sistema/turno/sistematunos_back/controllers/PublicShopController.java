package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.AvailableSlotDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ReviewResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ShopDTO;
import com.gaston.sistema.turno.sistematunos_back.services.AppointmentService;
import com.gaston.sistema.turno.sistematunos_back.services.ReviewService;
import com.gaston.sistema.turno.sistematunos_back.services.ShopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/public/shops")
public class PublicShopController {

    private final ShopService shopService;
    private final AppointmentService appointmentService;
    private final ReviewService reviewService;

    public PublicShopController(ShopService shopService, AppointmentService appointmentService,
                                ReviewService reviewService) {
        this.shopService = shopService;
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
    }

    @GetMapping("{id}")
    @SecurityRequirements()
    public ResponseEntity<ShopDTO> getShopById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(shopService.getShopDTOById(id));
    }

    @GetMapping
    @SecurityRequirements()
    public ResponseEntity<Page<ShopDTO>> getShopsByTypeOrProvinceOrName(
                                                      @RequestParam(required = false) String tipoLocal,
                                                      @RequestParam(required = false) String provincia,
                                                      @RequestParam(required = false) String nombre,
                                                      @PageableDefault(page = 0, size = 10) Pageable pageable) {

       Page<ShopDTO> shops = shopService.getShops(tipoLocal, provincia, nombre, pageable);
       return ResponseEntity.status(HttpStatus.OK).body(shops);
    }

    /**
     * Public availability check: no authentication required.
     * Allows users to check available slots before creating an account.
     * Fixes audit gap [I9]: availability was incorrectly under /client/** (requires CLIENTE role).
     */
    @Operation(summary = "Check availability (public)",
               description = "Returns available time slots for a specific employee, service and date. No authentication required.")
    @SecurityRequirements()
    @GetMapping("/availability")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlots(
            @RequestParam Long shopId,
            @RequestParam Long employeeId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<AvailableSlotDTO> slots = appointmentService.getAvailableSlots(shopId, employeeId, serviceId, date);
        return ResponseEntity.status(HttpStatus.OK).body(slots);
    }

    @Operation(summary = "Get shop reviews (public)",
               description = "Returns all reviews for a specific shop. No authentication required.")
    @SecurityRequirements()
    @GetMapping("/{shopId}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getShopReviews(@PathVariable Long shopId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReviewsByShop(shopId));
    }

    @Operation(summary = "Get shop average rating (public)",
               description = "Returns the average rating score for a specific shop. No authentication required.")
    @SecurityRequirements()
    @GetMapping("/{shopId}/reviews/average")
    public ResponseEntity<Double> getShopAverageRating(@PathVariable Long shopId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getShopAverageRating(shopId));
    }
}
