package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailServiceImpl.class);
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
            log.error("Error sending confirmation email to: {}", recipient, e);
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
             log.error("Error sending reminder email to: {}", recipient, e);
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
            log.error("Error sending password reset email to: {}", recipient, e);
        }
    }

    @Override
    @Async
    public void sendCancellationNotification(String recipient, String recipientName, LocalDateTime dateTime,
                                             String service, String shopName, String cancelledBy) {
        if (recipient == null || recipient.toLowerCase().contains("anonimo")) return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject("Turno Cancelado - TuTurno");
            message.setText("Hola " + recipientName + ",\n\n" +
                    "Te informamos que el turno para el servicio de: " + service + " ha sido CANCELADO.\n\n" +
                    "Local: " + shopName + "\n" +
                    "Fecha y hora: " + dateTime.format(formatter) + "\n" +
                    "Cancelado por: " + cancelledBy + "\n\n" +
                    "Podés reservar un nuevo turno cuando lo desees.\n\n" +
                    "Equipo TuTurno");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error sending cancellation email to: {}", recipient, e);
        }
    }
}
