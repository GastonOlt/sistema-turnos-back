package com.gaston.sistema.turno.sistematunos_back.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating or editing a ShopOffering (service).
 * Deliberately excludes 'shop', 'id' and 'appointments' to prevent
 * clients from manipulating internal JPA associations.
 */
public class ShopOfferingRequestDTO {

    @NotBlank(message = "Service name is required")
    private String name;

    @NotBlank(message = "Service description is required")
    private String description;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer duration;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be zero or greater")
    private Integer price;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}
