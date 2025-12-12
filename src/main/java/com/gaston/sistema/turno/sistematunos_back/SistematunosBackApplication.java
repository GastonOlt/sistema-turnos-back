package com.gaston.sistema.turno.sistematunos_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistematunosBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistematunosBackApplication.class, args);
	}

}
