package com.gaston.sistema.turno.sistematunos_back.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReseniaRequestDTO {
    
    @NotNull(message = "El ID del turno es obligatorio")
    private Long turnoId;

    @NotNull(message = "la calificacion es obligatoria")
    @Min(0)
    @Max(5)
    private double calificacion;

    private String comentario;

    public Long getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(Long turnoId) {
        this.turnoId = turnoId;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

}
