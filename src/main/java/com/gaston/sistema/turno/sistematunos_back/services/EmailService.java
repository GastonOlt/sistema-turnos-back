package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDateTime;

public interface EmailService {
    
    void enviarConfirmacionTurno(String destinatario, String nombreCliente, LocalDateTime fechaHora, String servicio, String nombreLocal , String ubiLocal);
    void enviarRecordatorioTurno(String destinatario, String nombreCliente, LocalDateTime fechaHora, String servicio, String nombreLocal , String ubiLocal);
}
