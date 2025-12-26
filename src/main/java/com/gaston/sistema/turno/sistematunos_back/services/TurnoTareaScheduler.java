package com.gaston.sistema.turno.sistematunos_back.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TurnoTareaScheduler {

    private final TurnoService turnoService;

    public TurnoTareaScheduler(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @Scheduled(fixedRate = 600000)
    public void tareaActualizarTurnosFinalizados() {
        turnoService.actualizarTurnosFinalizados();
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void tareaEnviarRecordatorios() {
        turnoService.enviarRecordatorioTurno();
    }
}
