package com.gaston.sistema.turno.sistematunos_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/dueno")
public class DuenoController {

    @GetMapping("")
    public String getMethodName(@RequestParam String nombre) {
        String mensaje = Map.of("bienvenido", nombre).toString();
        return mensaje ;
    }
    
}
