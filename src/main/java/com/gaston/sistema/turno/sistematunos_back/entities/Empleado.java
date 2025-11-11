package com.gaston.sistema.turno.sistematunos_back.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleado")
public class Empleado extends Usuario {

    @ManyToOne
    @JoinColumn(name = "local_id")
    @JsonIgnore
    private Local local;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true )
    @JoinColumn(name = "imagen_id")
    private ImagenLocal imagenEmpleado;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horario> horarios = new ArrayList<>();
    
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Turno> turnos = new ArrayList<>();

    private String especialidad;

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public ImagenLocal getImagenEmpleado() {
        return imagenEmpleado;
    }

    public void setImagenEmpleado(ImagenLocal imagenEmpleado) {
        this.imagenEmpleado = imagenEmpleado;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<Turno> turno) {
        this.turnos = turno;
    }



    
}
