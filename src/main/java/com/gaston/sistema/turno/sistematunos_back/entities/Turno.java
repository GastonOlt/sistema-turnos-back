package com.gaston.sistema.turno.sistematunos_back.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "turno")
public class Turno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonIgnore
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "empleado_id")
    @JsonIgnore
    private Empleado empleado;
    
    @ManyToOne
    @JoinColumn(name = "servicio_id")
    @JsonIgnore
    private ServicioLocal servicio;

    @ManyToOne
    @JoinColumn(name = "local_id")
    @JsonIgnore
    private Local local;
    
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private LocalDateTime fechaHoraInicioReal;
    private LocalDateTime fechaHoraFinReal;
    
    @Enumerated(EnumType.STRING)
    private EstadoTurno estado;
    
    private boolean adelantado = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public ServicioLocal getServicio() {
        return servicio;
    }

    public void setServicio(ServicioLocal servicio) {
        this.servicio = servicio;
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

    public LocalDateTime getFechaHoraInicioReal() {
        return fechaHoraInicioReal;
    }

    public void setFechaHoraInicioReal(LocalDateTime fechaHoraInicioReal) {
        this.fechaHoraInicioReal = fechaHoraInicioReal;
    }

    public LocalDateTime getFechaHoraFinReal() {
        return fechaHoraFinReal;
    }

    public void setFechaHoraFinReal(LocalDateTime fechaHoraFinReal) {
        this.fechaHoraFinReal = fechaHoraFinReal;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    public boolean isAdelantado() {
        return adelantado;
    }

    public void setAdelantado(boolean adelantado) {
        this.adelantado = adelantado;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }
    
    
}
 