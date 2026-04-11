package com.gaston.sistema.turno.sistematunos_back.validation;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String message){
        super(message);
    }
}
