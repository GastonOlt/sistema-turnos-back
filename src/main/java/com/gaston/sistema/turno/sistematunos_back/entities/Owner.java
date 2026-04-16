package com.gaston.sistema.turno.sistematunos_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;

@Entity
@Table(name = "owner")
public class Owner extends User {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    @JsonIgnore
    private Shop shop;

    /**
     * Ghost profile: an Employee entity created the first time the owner activates attendance.
     * Never deleted — only deactivated via Employee.active flag.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_profile_id")
    @JsonIgnore
    private Employee employeeProfile;

    /** Whether the owner is currently available to attend appointments. */
    private boolean availableToAttend = false;

    public Owner() {
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Employee getEmployeeProfile() {
        return employeeProfile;
    }

    public void setEmployeeProfile(Employee employeeProfile) {
        this.employeeProfile = employeeProfile;
    }

    public boolean isAvailableToAttend() {
        return availableToAttend;
    }

    public void setAvailableToAttend(boolean availableToAttend) {
        this.availableToAttend = availableToAttend;
    }
}
