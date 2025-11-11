package com.gaston.sistema.turno.sistematunos_back.dto;

import java.time.LocalDateTime;

public class SlotDisponibleDTO {
    private LocalDateTime inicio;
    private LocalDateTime fin;

    public SlotDisponibleDTO() {
    }

    public SlotDisponibleDTO(LocalDateTime inicio, LocalDateTime fin) {
        this.inicio = inicio;
        this.fin = fin;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    } 
}
