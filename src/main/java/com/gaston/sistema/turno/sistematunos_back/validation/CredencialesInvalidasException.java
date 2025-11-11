package com.gaston.sistema.turno.sistematunos_back.validation;

public class CredencialesInvalidasException extends RuntimeException{
    public CredencialesInvalidasException(String message){
        super(message);
    }
}
