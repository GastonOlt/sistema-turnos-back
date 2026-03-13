package com.gaston.sistema.turno.sistematunos_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dueno")
public class Dueno extends Usuario {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id")
    @JsonIgnore
    private Local local;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_perfil_id")
    @JsonIgnore
    private Empleado empleadoPerfil;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "imagen_id")
    private ImagenLocal imagenDueno;

    public Dueno() {
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Empleado getEmpleadoPerfil() {
        return empleadoPerfil;
    }

    public void setEmpleadoPerfil(Empleado empleadoPerfil) {
        this.empleadoPerfil = empleadoPerfil;
    }

    public ImagenLocal getImagenDueno() {
        return imagenDueno;
    }

    public void setImagenDueno(ImagenLocal imagenDueno) {
        this.imagenDueno = imagenDueno;
    }
}
