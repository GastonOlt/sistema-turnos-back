package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDateTime;

public interface EmailService {
    void sendAppointmentConfirmation(String recipient, String clientName, LocalDateTime dateTime, String service, String shopName, String shopAddress);
    void sendAppointmentReminder(String recipient, String clientName, LocalDateTime dateTime, String service, String shopName, String shopAddress);
    void sendPasswordResetEmail(String recipient, String userName, String resetToken);
    /** Notifies a party (client or employee) that an appointment was cancelled. */
    void sendCancellationNotification(String recipient, String recipientName, LocalDateTime dateTime, String service, String shopName, String cancelledBy);
}
