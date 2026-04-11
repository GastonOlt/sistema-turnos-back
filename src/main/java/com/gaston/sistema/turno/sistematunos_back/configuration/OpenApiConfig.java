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
                    .title("TuTurno API - Management System")
                        .version("1.0.0")
                        .description("Backend Documentation. \n\n" +
                                     "**Security Note:** This API uses **HttpOnly Cookies**. " +
                                     "To test protected endpoints in this interface:\n" +
                                     "1. Execute the `/auth/login` endpoint with valid credentials.\n" +
                                     "2. The browser will automatically save the cookie.\n" +
                                     "3. Then you will be able to execute any protected endpoint (closed padlock).")
                        .contact(new Contact()
                        .name("Gastón Olartes")))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth")) 
                .components(new Components()
                        .addSecuritySchemes("cookieAuth", 
                                new SecurityScheme()
                                        .name("accessToken")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .description("JWT Access Token stored in HttpOnly cookie")
                        ));
    }
}
