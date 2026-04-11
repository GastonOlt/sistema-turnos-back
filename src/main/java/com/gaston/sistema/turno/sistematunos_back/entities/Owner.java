package com.gaston.sistema.turno.sistematunos_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "owner")
public class Owner extends User {

    @OneToOne
    @JoinColumn(name = "shop_id")
    @JsonIgnore
    private Shop shop;

    public Owner() {
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
