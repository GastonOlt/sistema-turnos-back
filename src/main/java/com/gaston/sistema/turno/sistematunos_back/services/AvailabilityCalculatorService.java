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

import com.gaston.sistema.turno.sistematunos_back.dto.AvailableSlotDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.repositories.AppointmentRepository;

@Service
public class AvailabilityCalculatorService {

    private final AppointmentRepository appointmentRepository;

    public AvailabilityCalculatorService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<AvailableSlotDTO> calculateSlots(Employee employee, ShopOffering service, LocalDate date) {
        Locale espaniol = Locale.of("es", "ES");
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, espaniol);
        String dayOfWeekCapitalized = Character.toUpperCase(dayOfWeek.charAt(0)) + dayOfWeek.substring(1).toLowerCase();

        List<Schedule> ranges = employee.getSchedules().stream()
                .filter(h -> h.getDayOfWeek().equalsIgnoreCase(dayOfWeekCapitalized) && h.isActive())
                .sorted(Comparator.comparing(Schedule::getOpeningTime))
                .collect(Collectors.toList());

        if (ranges.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Appointment> dayAppointments = appointmentRepository.findActiveAppointmentsByDate(employee.getId(), startOfDay, endOfDay);

        int durationMin = service.getDuration();
        int intervalMin = 15;

        List<AvailableSlotDTO> availableSlots = new ArrayList<>();

        for (Schedule range : ranges) {
            LocalTime opening = range.getOpeningTime();
            LocalTime closing = range.getClosingTime();

            while (!opening.plusMinutes(durationMin).isAfter(closing)) {
                LocalDateTime slotStart = LocalDateTime.of(date, opening);
                LocalDateTime slotEnd = slotStart.plusMinutes(durationMin);

                if (slotStart.isBefore(LocalDateTime.now())) {
                    opening = opening.plusMinutes(intervalMin);
                    continue;
                }

                boolean occupied = checkOverlap(dayAppointments, slotStart, slotEnd);

                if (!occupied) {
                    availableSlots.add(new AvailableSlotDTO(slotStart, slotEnd));
                }

                opening = opening.plusMinutes(intervalMin);
            }
        }
        return availableSlots;
    }

    private boolean checkOverlap(List<Appointment> appointments, LocalDateTime slotStart, LocalDateTime slotEnd) {
        for (Appointment appointment : appointments) {
            // Collision logic: (StartA < EndB) && (EndA > StartB)
            if (appointment.getStartDateTime().isBefore(slotEnd) && appointment.getEndDateTime().isAfter(slotStart)) {
                return true;
            }
        }
        return false;
    }
}
