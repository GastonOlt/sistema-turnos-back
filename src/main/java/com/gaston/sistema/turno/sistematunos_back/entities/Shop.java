package com.gaston.sistema.turno.sistematunos_back.entities;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "shop")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "ingrese un valor ")
    private String name;

    @NotBlank(message = "ingrese un valor ")
    private String description;

    @NotBlank(message = "ingrese un valor ")
    private String address;

    @NotBlank(message = "ingrese un valor ")
    private String province;

    @NotBlank(message = "ingrese un valor ")
    private String phone;

    @NotBlank(message = "ingrese un valor ")
    private String shopType;

    private Double latitude;
    private Double longitude;

    private Double averageRating;

    @OneToOne(mappedBy = "shop")
    private Owner owner;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<ShopImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @BatchSize(size = 20)
    private List<Schedule> schedules = new ArrayList<>();

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<Employee> employees = new ArrayList<>();

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<ShopOffering> services = new ArrayList<>();

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<Appointment> appointments = new ArrayList<>();

    public void updateShopData(Shop shop) {
        if (shop.name != null) this.name = shop.getName();
        if (shop.description != null) this.description = shop.getDescription();
        if (shop.address != null) this.address = shop.getAddress();
        if (shop.province != null) this.province = shop.getProvince();
        if (shop.phone != null) this.phone = shop.getPhone();
        if (shop.shopType != null) this.shopType = shop.getShopType();
        if (shop.latitude != null) this.latitude = shop.getLatitude();
        if (shop.longitude != null) this.longitude = shop.getLongitude();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public List<ShopImage> getImages() {
        return images;
    }

    public void setImages(List<ShopImage> images) {
        this.images = images;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getShopType() {
        return shopType;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<ShopOffering> getServices() {
        return services;
    }

    public void setServices(List<ShopOffering> services) {
        this.services = services;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Double getAverageRating() {
        return averageRating != null ? averageRating : 0.0;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}
