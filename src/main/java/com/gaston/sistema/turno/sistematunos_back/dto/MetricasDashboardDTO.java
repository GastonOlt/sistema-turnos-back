package com.gaston.sistema.turno.sistematunos_back.dto;

import java.util.List;

public class MetricasDashboardDTO {
    private double gananciaTotal;
    private int cantidadTurnosTotal;
    private int empleadosActivos;

    private List<RendimientoEmpleadoDTO> rendimientoEmpleados;
    private List<TurnoDuenoDTO> ultimosTurnos;

    // Getters y Setters
    public double getGananciaTotal() {
        return gananciaTotal;
    }

    public void setGananciaTotal(double gananciaTotal) {
        this.gananciaTotal = gananciaTotal;
    }

    public int getCantidadTurnosTotal() {
        return cantidadTurnosTotal;
    }

    public void setCantidadTurnosTotal(int cantidadTurnosTotal) {
        this.cantidadTurnosTotal = cantidadTurnosTotal;
    }

    public int getEmpleadosActivos() {
        return empleadosActivos;
    }

    public void setEmpleadosActivos(int empleadosActivos) {
        this.empleadosActivos = empleadosActivos;
    }

    public List<RendimientoEmpleadoDTO> getRendimientoEmpleados() {
        return rendimientoEmpleados;
    }

    public void setRendimientoEmpleados(List<RendimientoEmpleadoDTO> rendimientoEmpleados) {
        this.rendimientoEmpleados = rendimientoEmpleados;
    }

    public List<TurnoDuenoDTO> getUltimosTurnos() {
        return ultimosTurnos;
    }

    public void setUltimosTurnos(List<TurnoDuenoDTO> ultimosTurnos) {
        this.ultimosTurnos = ultimosTurnos;
    }
}
