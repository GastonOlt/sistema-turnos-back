package com.gaston.sistema.turno.sistematunos_back.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for viewing and editing an Owner's own profile data.
 */
public class OwnerProfileDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "Name can only contain letters")
    private String name;

    @NotBlank(message = "Last name is required")
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "Last name can only contain letters")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    private boolean availableToAttend;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAvailableToAttend() { return availableToAttend; }
    public void setAvailableToAttend(boolean availableToAttend) { this.availableToAttend = availableToAttend; }
}
