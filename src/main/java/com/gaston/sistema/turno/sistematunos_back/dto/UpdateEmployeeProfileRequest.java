package com.gaston.sistema.turno.sistematunos_back.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for an Employee editing their own profile data.
 * Excludes role, shop, and image fields — those are managed by the Owner.
 */
public class UpdateEmployeeProfileRequest {

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

    private String specialty;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}
