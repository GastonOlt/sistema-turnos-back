package com.gaston.sistema.turno.sistematunos_back.validation;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message){
        super(message);
    }
}
