package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.SlotDisponibleDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.Horario;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Turno;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.repositories.TurnoRepository;

@Service
public class CalculadoraDisponibilidadService {

    private final TurnoRepository turnoRepository;

    public CalculadoraDisponibilidadService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    @Transactional(readOnly = true)
    public List<SlotDisponibleDTO> calcularSlotsDueno(Dueno dueno, ServicioLocal servicio, LocalDate fecha) {
        Locale espaniol = Locale.of("es", "ES");
        String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, espaniol);
        String diaSemanaCapitalized = Character.toUpperCase(diaSemana.charAt(0)) + diaSemana.substring(1).toLowerCase();

        // Para el dueno, usamos los horarios del local donde NO hay empleado asignado
        // (horarios generales)
        // O asumimos que si el horario esta en la lista del local y cumple condicion,
        // es valido.
        // El local tiene una lista de horarios.
        List<Horario> rangos = dueno.getLocal().getHorarios().stream()
                .filter(h -> h.getEmpleado() == null) // Solo horarios del local (sin empleado especifico)
                .filter(h -> h.getDiaSemana().equalsIgnoreCase(diaSemanaCapitalized) && h.isActivo())
                .sorted(Comparator.comparing(Horario::getHorarioApertura))
                .collect(Collectors.toList());

        if (rangos.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);
        List<Turno> turnosDelDia = turnoRepository.findTurnosActivosPorFechaDueno(dueno.getId(), inicioDia, finDia);

        return generarSlots(rangos, turnosDelDia, servicio, fecha);
    }

    @Transactional(readOnly = true)
    public List<SlotDisponibleDTO> calcularSlots(Empleado empleado, ServicioLocal servicio, LocalDate fecha) {
        Locale espaniol = Locale.of("es", "ES");
        String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, espaniol);
        String diaSemanaCapitalized = Character.toUpperCase(diaSemana.charAt(0)) + diaSemana.substring(1).toLowerCase();

        List<Horario> rangos = empleado.getHorarios().stream()
                .filter(h -> h.getDiaSemana().equalsIgnoreCase(diaSemanaCapitalized) && h.isActivo())
                .sorted(Comparator.comparing(Horario::getHorarioApertura))
                .collect(Collectors.toList());

        if (rangos.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);
        List<Turno> turnosDelDia = turnoRepository.findTurnosActivosPorFecha(empleado.getId(), inicioDia, finDia);

        return generarSlots(rangos, turnosDelDia, servicio, fecha);
    }

    private List<SlotDisponibleDTO> generarSlots(List<Horario> rangos, List<Turno> turnosDelDia, ServicioLocal servicio,
            LocalDate fecha) {
        int duracionMin = servicio.getTiempo();
        int intervaloMin = 15;

        List<SlotDisponibleDTO> slotDisponibles = new ArrayList<>();

        for (Horario rango : rangos) {
            LocalTime apertura = rango.getHorarioApertura();
            LocalTime cierre = rango.getHorarioCierre();

            while (!apertura.plusMinutes(duracionMin).isAfter(cierre)) {
                LocalDateTime slotInicial = LocalDateTime.of(fecha, apertura);
                LocalDateTime slotFin = slotInicial.plusMinutes(duracionMin);

                if (slotInicial.isBefore(LocalDateTime.now())) {
                    apertura = apertura.plusMinutes(intervaloMin);
                    continue;
                }

                boolean ocupado = verificarSolapamiento(turnosDelDia, slotInicial, slotFin);

                if (!ocupado) {
                    slotDisponibles.add(new SlotDisponibleDTO(slotInicial, slotFin));
                }

                apertura = apertura.plusMinutes(intervaloMin);
            }
        }
        return slotDisponibles;
    }

    private boolean verificarSolapamiento(List<Turno> turnos, LocalDateTime slotInicio, LocalDateTime slotFin) {
        for (Turno turno : turnos) {
            // Lógica de colisión: (StartA < EndB) && (EndA > StartB)
            if (turno.getFechaHoraInicio().isBefore(slotFin) && turno.getFechaHoraFin().isAfter(slotInicio)) {
                return true;
            }
        }
        return false;
    }
}
