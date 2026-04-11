package com.gaston.sistema.turno.sistematunos_back.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReviewRequestDTO {

    @NotNull(message = "El ID del turno es obligatorio")
    private Long appointmentId;

    @NotNull(message = "la calificacion es obligatoria")
    @Min(0)
    @Max(5)
    private double rating;

    private String comment;

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
