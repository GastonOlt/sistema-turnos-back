package com.gaston.sistema.turno.sistematunos_back.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class AppointmentRequestDTO {

    @Schema(description = "ID of the employee who will perform the service", example = "1")
    private Long employeeId;
    @Schema(description = "ID of the service to book", example = "5")
    private Long serviceId;
    @Schema(description = "ID of the shop", example = "2")
    private Long shopId;
    @Schema(description = "Appointment start date and time in ISO 8601 format", example = "2024-07-15T14:30:00")
    private LocalDateTime startDateTime;

    public AppointmentRequestDTO() {
    }

    public AppointmentRequestDTO(Long employeeId, Long serviceId, Long shopId, LocalDateTime startDateTime) {
        this.employeeId = employeeId;
        this.serviceId = serviceId;
        this.shopId = shopId;
        this.startDateTime = startDateTime;
    }

    public Long getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    public Long getServiceId() {
        return serviceId;
    }
    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
    public Long getShopId() {
        return shopId;
    }
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
}
