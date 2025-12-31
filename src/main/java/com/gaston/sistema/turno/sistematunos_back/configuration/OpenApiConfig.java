package com.gaston.sistema.turno.sistematunos_back.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("API TuTurno - Sistema de Gestión")
                        .version("1.0.0")
                        .description("Documentación del Backend. \n\n" +
                                     "**Nota de Seguridad:** Esta API utiliza **Cookies HttpOnly**. " +
                                     "Para probar los endpoints protegidos en esta interfaz:\n" +
                                     "1. Ejecuta el endpoint `/autenticacion/login` con credenciales válidas.\n" +
                                     "2. El navegador guardará la cookie automáticamente.\n" +
                                     "3. Luego podrás ejecutar cualquier endpoint protegido (candado cerrado).")
                        .contact(new Contact()
                        .name("Gastón Olartes")))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth")) 
                .components(new Components()
                        .addSecuritySchemes("cookieAuth", 
                                new SecurityScheme()
                                        .name("accessToken")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .description("JWT Access Token almacenado en cookie HttpOnly")
                        ));
    }
}
