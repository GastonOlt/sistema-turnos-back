package com.gaston.sistema.turno.sistematunos_back.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @Email(message = "email requerido")
    @NotBlank(message = "ingrese un valor ")
    @Schema(description = "Correo del usuario registrado", example = "cliente@test.com") 
    private String email;

    @NotBlank(message = "ingrese un valor")
    @Size(min = 6)
    @Schema(description = "Contraseña del usuario", example = "Password123!")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
