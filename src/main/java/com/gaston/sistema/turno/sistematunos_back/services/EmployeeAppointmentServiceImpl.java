package com.gaston.sistema.turno.sistematunos_back.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentEmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.repositories.AppointmentRepository;

@Service
public class EmployeeAppointmentServiceImpl implements EmployeeAppointmentService {

    private final EmployeeService employeeService;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    public EmployeeAppointmentServiceImpl(EmployeeService employeeService, AppointmentRepository appointmentRepository,
            EmailService emailService) {
        this.employeeService = employeeService;
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentEmployeeDTO> listPendingAppointments(Long employeeId) {
        return appointmentRepository.findByEmployeeIdAndStatus(employeeId, AppointmentStatus.PENDING).stream()
               .map(this::convertToDTO)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentEmployeeDTO> listConfirmedAppointments(Long employeeId) {
        return appointmentRepository.findByEmployeeIdAndStatus(employeeId, AppointmentStatus.CONFIRMED).stream()
               .map(this::convertToDTO)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentEmployeeDTO> appointmentHistory(Long employeeId) {
        return appointmentRepository.findByEmployeeIdAndStatus(employeeId, AppointmentStatus.COMPLETED).stream()
               .map(this::convertToDTO)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelAppointment(Long employeeId, Long appointmentId) {
        Employee employee = employeeService.getEmployeeEntity(employeeId);
        Appointment appointment = employee.getAppointments().stream()
        .filter(a -> a.getId().equals(appointmentId))
        .findFirst()
        .orElseThrow(()->new IllegalArgumentException("no se encontro el turno"));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void confirmAppointment(Long employeeId, Long appointmentId) {
        Employee employee = employeeService.getEmployeeEntity(employeeId);
        Appointment appointment = employee.getAppointments().stream()
        .filter(a -> a.getId().equals(appointmentId))
        .findFirst()
        .orElseThrow(()->new IllegalArgumentException("no se encontro el turno"));

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);
       try {
            if (appointment.getClient() != null) {
                emailService.sendAppointmentConfirmation(
                    appointment.getClient().getEmail(),
                    appointment.getClient().getName(),
                    appointment.getStartDateTime(),
                    appointment.getService().getName(),
                    appointment.getShop().getName(),
                    appointment.getShop().getAddress()
                );
            }
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopOffering> getServicesByEmployee(Long employeeId) {
        Employee employee = employeeService.getEmployeeEntity(employeeId);
        return employee.getShop().getServices();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateEarnings(Long employeeId, LocalDate from, LocalDate to) {
        LocalDateTime startDate = from.atStartOfDay();
        LocalDateTime endDate = to.atTime(23, 59);

        List<Appointment> appointments = appointmentRepository.findByEmployeeIdAndStatusAndStartDateTimeBetween(
                employeeId, AppointmentStatus.COMPLETED, startDate, endDate);

        int totalSum = 0;
        for(Appointment appointment : appointments){
            totalSum += appointment.getService().getPrice();
        }

        return BigDecimal.valueOf(totalSum);
    }

    private AppointmentEmployeeDTO convertToDTO(Appointment appointment){
        AppointmentEmployeeDTO dto = new AppointmentEmployeeDTO();
        dto.setId(appointment.getId());
        dto.setStartDateTime(appointment.getStartDateTime());
        dto.setEndDateTime(appointment.getEndDateTime());
        dto.setClientName(appointment.getClient().getName()+" "+appointment.getClient().getLastName());
        dto.setStatus(appointment.getStatus().name());
        dto.setService(appointment.getService().getName());
        dto.setPrice(appointment.getService().getPrice());
        return dto;
    }
}
