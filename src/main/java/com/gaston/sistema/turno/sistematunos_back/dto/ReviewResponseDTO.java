package com.gaston.sistema.turno.sistematunos_back.dto;

import java.time.LocalDateTime;

public class ReviewResponseDTO {

    private Long id;
    private Double rating;
    private String comment;
    private String clientName;
    private LocalDateTime date;
    private String serviceName;

    public ReviewResponseDTO() {
    }

    public ReviewResponseDTO(Long id, Double rating, String comment, String clientName,
            LocalDateTime date, String serviceName) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.clientName = clientName;
        this.date = date;
        this.serviceName = serviceName;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Double getRating() {
        return rating;
    }
    public void setRating(Double rating) {
        this.rating = rating;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
