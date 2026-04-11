package com.gaston.sistema.turno.sistematunos_back.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;

public class ShopDTO {

    private Long id;
    private String name;
    private String description;
    private String province;
    private String phone;
    private Double latitude;
    private Double longitude;
    private String ownerName;
    private Double averageRating;

    private List<ShopImageDTO> images = new ArrayList<>();
    private List<ShopOfferingDTO> services = new ArrayList<>();
    private List<ScheduleDTO> schedules = new ArrayList<>();
    private List<EmployeeDTO> employees = new ArrayList<>();

    public ShopDTO(Shop shop) {
        this.id = shop.getId();
        this.name = shop.getName();
        this.description = shop.getDescription();
        this.province = shop.getProvince();
        this.phone = shop.getPhone();
        this.latitude = shop.getLatitude();
        this.longitude = shop.getLongitude();
        this.ownerName = shop.getOwner().getName();
        this.averageRating = shop.getAverageRating();

        if (shop.getImages() != null) {
            this.images = shop.getImages().stream()
                .map(img -> new ShopImageDTO(img.getId(), img.getFileName()))
                .collect(Collectors.toList());
        }

        if (shop.getServices() != null) {
            this.services = shop.getServices().stream()
                .map(this::mapShopOffering)
                .collect(Collectors.toList());
        }

        if (shop.getSchedules() != null) {
            this.schedules = shop.getSchedules().stream()
                .map(this::mapSchedule)
                .collect(Collectors.toList());
        }

        if (shop.getEmployees() != null) {
            this.employees = shop.getEmployees().stream()
                .map(this::mapEmployee)
                .collect(Collectors.toList());
        }
    }

    private ShopOfferingDTO mapShopOffering(ShopOffering s) {
        ShopOfferingDTO dto = new ShopOfferingDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setPrice(s.getPrice());
        dto.setDuration(s.getDuration());
        return dto;
    }

    private ScheduleDTO mapSchedule(Schedule h) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(h.getId());
        dto.setDayOfWeek(h.getDayOfWeek());
        dto.setOpeningTime(h.getOpeningTime());
        dto.setClosingTime(h.getClosingTime());
        dto.setActive(h.isActive());
        return dto;
    }

    private EmployeeDTO mapEmployee(Employee e) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setLastName(e.getLastName());
        dto.setEmail(e.getEmail());
        dto.setSpecialty(e.getSpecialty());
        dto.setRole(e.getRole());
        return dto;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<ShopImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ShopImageDTO> images) {
        this.images = images;
    }

    public List<ShopOfferingDTO> getServices() {
        return services;
    }

    public void setServices(List<ShopOfferingDTO> services) {
        this.services = services;
    }

    public List<ScheduleDTO> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleDTO> schedules) {
        this.schedules = schedules;
    }

    public List<EmployeeDTO> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeDTO> employees) {
        this.employees = employees;
    }
}
