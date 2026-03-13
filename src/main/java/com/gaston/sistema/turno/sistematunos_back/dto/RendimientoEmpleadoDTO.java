package com.gaston.sistema.turno.sistematunos_back.dto;

import java.util.List;

public class RendimientoEmpleadoDTO {
    private Long empleadoId;
    private String nombre;
    private int cantidadTurnos;
    private double gananciaGenerada;
    private List<TurnoDuenoDTO> turnos;

    // Getters y Setters
    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidadTurnos() {
        return cantidadTurnos;
    }

    public void setCantidadTurnos(int cantidadTurnos) {
        this.cantidadTurnos = cantidadTurnos;
    }

    public double getGananciaGenerada() {
        return gananciaGenerada;
    }

    public void setGananciaGenerada(double gananciaGenerada) {
        this.gananciaGenerada = gananciaGenerada;
    }

    public List<TurnoDuenoDTO> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<TurnoDuenoDTO> turnos) {
        this.turnos = turnos;
    }
}
