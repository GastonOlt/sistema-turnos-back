package com.gaston.sistema.turno.sistematunos_back.dto;

import java.time.LocalDateTime;

public class AvailableSlotDTO {
    private LocalDateTime start;
    private LocalDateTime end;

    public AvailableSlotDTO() {
    }

    public AvailableSlotDTO(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
