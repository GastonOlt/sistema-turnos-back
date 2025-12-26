package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImp implements EmailService {

    private final JavaMailSender mailSender;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EmailServiceImp(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarConfirmacionTurno(String destinatario, String nombreCliente, LocalDateTime fechaHora, String servicio, String nombreLocal , String ubiLocal) {
        if(destinatario == null || destinatario.toLowerCase().contains("anonimo")) return;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Turno Confirmado - TuTurno");
        message.setText("Hola "+nombreCliente+ ",\n\n" +
                        "Tu Turno para el servicio de: "+ servicio + " ha sido CONFIRMADO "+ ",\n\n" +
                        "En el Local: "+ nombreLocal +  ",\n\n" +
                        "Ubicacion: "+ ubiLocal + ",\n\n" +
                        "te esperamos el:" + fechaHora.format(formatter));

        mailSender.send(message);
    }

    @Override
    public void enviarRecordatorioTurno(String destinatario, String nombreCliente, LocalDateTime fechaHora, String servicio, String nombreLocal , String ubiLocal) {
        if(destinatario == null || destinatario.toLowerCase().contains("anonimo")) return;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Recordatorio de Turno para Hoy - TuTurno");
        message.setText("Hola "+nombreCliente+ ",\n\n" +
                        "Te recordamos que hoy tienes un turno para: "+ servicio + ",\n\n" +
                        "En el Local: "+ nombreLocal +  ",\n\n" +
                        "Ubicacion: "+ ubiLocal + ",\n\n" +
                        "Horario" + fechaHora.format(formatter)+ "\n\n" +
                        "¡Te esperamos!");

        mailSender.send(message);
    }

}
