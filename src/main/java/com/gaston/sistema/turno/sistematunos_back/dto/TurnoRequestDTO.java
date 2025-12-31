package com.gaston.sistema.turno.sistematunos_back.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class TurnoRequestDTO {

    @Schema(description = "ID del empleado que realizará el servicio", example = "1")
    private Long empleadoId;
    @Schema(description = "ID del servicio a reservar", example = "5")
    private Long servicioId;
    @Schema(description = "ID del local", example = "2")
    private Long localId;
    @Schema(description = "Fecha y hora de inicio del turno en formato ISO 8601", example = "2024-07-15T14:30:00")
    private LocalDateTime fechaHoraInicio;

    public TurnoRequestDTO() {
    }

    public TurnoRequestDTO(Long empleadoId, Long servicioId, Long localId, LocalDateTime fechaHoraInicio) {
        this.empleadoId = empleadoId;
        this.servicioId = servicioId;
        this.localId = localId;
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public Long getEmpleadoId() {
        return empleadoId;
    }
    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }
    public Long getServicioId() {
        return servicioId;
    }
    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }
    public Long getLocalId() {
        return localId;
    }
    public void setLocalId(Long localId) {
        this.localId = localId;
    }
    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }
    
}
