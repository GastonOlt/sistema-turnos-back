package com.gaston.sistema.turno.sistematunos_back.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating or editing a Schedule (shop or employee).
 * Deliberately excludes 'shop', 'employee' and 'id' to prevent
 * clients from manipulating JPA associations directly.
 */
public class ScheduleRequestDTO {

    @NotBlank(message = "Day of week is required (e.g. MONDAY)")
    private String dayOfWeek;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;

    private boolean active = true;

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }

    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
