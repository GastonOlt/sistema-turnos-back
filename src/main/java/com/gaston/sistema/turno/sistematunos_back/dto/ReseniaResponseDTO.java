package com.gaston.sistema.turno.sistematunos_back.dto;

import java.time.LocalDateTime;

public class ReseniaResponseDTO {

    private Long id;
    private Double calificacion;
    private String comentario;
    private String nombreCliente;
    private LocalDateTime fecha;
    private String nombreServicio;

    public ReseniaResponseDTO() {
    }

    public ReseniaResponseDTO(Long id, Double calificacion, String comentario, String nombreCliente,
            LocalDateTime fecha, String nombreServicio) {
        this.id = id;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.nombreCliente = nombreCliente;
        this.fecha = fecha;
        this.nombreServicio = nombreServicio;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Double getCalificacion() {
        return calificacion;
    }
    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }
    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    public String getNombreCliente() {
        return nombreCliente;
    }
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    public String getNombreServicio() {
        return nombreServicio;
    }
    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }
    
}
