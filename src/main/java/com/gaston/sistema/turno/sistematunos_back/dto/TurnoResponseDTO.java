package com.gaston.sistema.turno.sistematunos_back.dto;

import java.time.LocalDateTime;

public class TurnoResponseDTO {

    private Long id;
    private String empleadoNombre;
    private String servicioNombre;
    private String localNombre;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String estado;
    private boolean adelantado;

    public TurnoResponseDTO() {
    }

    public TurnoResponseDTO(Long id, String empleadoNombre, String servicioNombre, String localNombre,
            LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, String estado, boolean adelantado) {
        this.id = id;
        this.empleadoNombre = empleadoNombre;
        this.servicioNombre = servicioNombre;
        this.localNombre = localNombre;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
        this.adelantado = adelantado;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmpleadoNombre() {
        return empleadoNombre;
    }
    public void setEmpleadoNombre(String empleadoNombre) {
        this.empleadoNombre = empleadoNombre;
    }
    public String getServicioNombre() {
        return servicioNombre;
    }
    public void setServicioNombre(String servicioNombre) {
        this.servicioNombre = servicioNombre;
    }
    public String getLocalNombre() {
        return localNombre;
    }
    public void setLocalNombre(String localNombre) {
        this.localNombre = localNombre;
    }
    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }
    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public boolean isAdelantado() {
        return adelantado;
    }
    public void setAdelantado(boolean adelantado) {
        this.adelantado = adelantado;
    } 
}
