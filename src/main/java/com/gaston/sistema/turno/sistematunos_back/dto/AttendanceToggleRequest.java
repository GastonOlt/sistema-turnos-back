package com.gaston.sistema.turno.sistematunos_back.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for the owner attendance toggle request.
 * Extracted from OwnerAttendanceController to comply with SRP
 * (a Controller should not define its own DTOs as inner classes).
 */
public class AttendanceToggleRequest {

    @NotNull(message = "The 'available' field must be provided")
    private Boolean available;

    public Boolean isAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
