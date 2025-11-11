package com.gaston.sistema.turno.sistematunos_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dueno")
public class Dueno extends Usuario{
    
    @OneToOne()
    @JoinColumn(name = "local_id")
    @JsonIgnore
    private Local local;

    public Dueno() {
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }
    


}
