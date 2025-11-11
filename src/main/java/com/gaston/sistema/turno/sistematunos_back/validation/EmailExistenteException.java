package com.gaston.sistema.turno.sistematunos_back.validation;


public class EmailExistenteException extends RuntimeException{

    public EmailExistenteException(String message){
        super(message);
    }
}
