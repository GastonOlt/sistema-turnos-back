package com.gaston.sistema.turno.sistematunos_back.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AppointmentTaskScheduler {

    private final AppointmentService appointmentService;

    public AppointmentTaskScheduler(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Scheduled(fixedRate = 600000)
    public void taskUpdateCompletedAppointments() {
        appointmentService.updateCompletedAppointments();
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void taskSendReminders() {
        appointmentService.sendAppointmentReminder();
    }
}
