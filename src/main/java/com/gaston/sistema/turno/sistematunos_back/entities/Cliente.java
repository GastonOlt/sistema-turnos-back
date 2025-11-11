package com.gaston.sistema.turno.sistematunos_back.entities;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente extends Usuario {
    
    @OneToMany(mappedBy = "cliente",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Turno> turnos = new ArrayList<>();

    public List<Turno> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<Turno> turno) {
        this.turnos = turno;
    }

    

}
