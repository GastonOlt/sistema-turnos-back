package com.gaston.sistema.turno.sistematunos_back.dto;

import java.time.LocalDateTime;

public class AppointmentResponseDTO {

    private Long id;
    private String barberName;
    private String serviceName;
    private String shopName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;
    private boolean early;

    public AppointmentResponseDTO() {
    }

    public AppointmentResponseDTO(Long id, String barberName, String serviceName, String shopName,
            LocalDateTime startDateTime, LocalDateTime endDateTime, String status, boolean early) {
        this.id = id;
        this.barberName = barberName;
        this.serviceName = serviceName;
        this.shopName = shopName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.early = early;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBarberName() {
        return barberName;
    }
    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getShopName() {
        return shopName;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public boolean isEarly() {
        return early;
    }
    public void setEarly(boolean early) {
        this.early = early;
    }
}
