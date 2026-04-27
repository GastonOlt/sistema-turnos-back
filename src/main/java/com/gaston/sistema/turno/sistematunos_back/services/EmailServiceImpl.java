package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendAppointmentConfirmation(String recipient, String clientName, LocalDateTime dateTime, String service, String shopName, String shopAddress) {
        if(recipient == null || recipient.toLowerCase().contains("anonimo")) return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject("Turno Confirmado - TuTurno");
            message.setText("Hola "+clientName+ ",\n\n" +
                            "Tu Turno para el servicio de: "+ service + " ha sido CONFIRMADO "+ ",\n\n" +
                            "En el Local: "+ shopName +  ",\n\n" +
                            "Ubicacion: "+ shopAddress + ",\n\n" +
                            "te esperamos el:" + dateTime.format(formatter));

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERROR enviando email a: " + recipient + " - " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendAppointmentReminder(String recipient, String clientName, LocalDateTime dateTime, String service, String shopName, String shopAddress) {
        if(recipient == null || recipient.toLowerCase().contains("anonimo")) return;

       try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject("Recordatorio de Turno para Hoy - TuTurno");
            message.setText("Hola "+clientName+ ",\n\n" +
                            "Te recordamos que hoy tienes un turno para: "+ service + ",\n\n" +
                            "En el Local: "+ shopName +  ",\n\n" +
                            "Ubicacion: "+ shopAddress + ",\n\n" +
                            "Horario" + dateTime.format(formatter)+ "\n\n" +
                            "¡Te esperamos!");

            mailSender.send(message);
        } catch (Exception e) {
             System.err.println("ERROR enviando recordatorio: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String recipient, String userName, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject("Restablecer contraseña - TuTurno");
            message.setText("Hola " + userName + ",\n\n" +
                    "Recibimos una solicitud para restablecer la contraseña de tu cuenta.\n\n" +
                    "Usa el siguiente token en la app para completar el proceso:\n\n" +
                    resetToken + "\n\n" +
                    "Este enlace expira en 1 hora. Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                    "Equipo TuTurno");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERROR enviando email de reset: " + e.getMessage());
        }
    }
}
