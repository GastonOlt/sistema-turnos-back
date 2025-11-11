package com.gaston.sistema.turno.sistematunos_back.dto;

import java.time.LocalDateTime;

public class TurnoRequestDTO {

    private Long empleadoId;
    private Long servicioId;
    private Long localId;
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
