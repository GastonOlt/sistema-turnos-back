package com.gaston.sistema.turno.sistematunos_back.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentEmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ShopOfferingDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus;
import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.repositories.AppointmentRepository;

@Service
public class EmployeeAppointmentServiceImpl implements EmployeeAppointmentService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeAppointmentServiceImpl.class);
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
        return appointmentRepository.findByEmployeeIdAndStatusWithRelations(employeeId, AppointmentStatus.PENDING).stream()
               .map(this::convertToDTO)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentEmployeeDTO> listConfirmedAppointments(Long employeeId) {
        return appointmentRepository.findByEmployeeIdAndStatusWithRelations(employeeId, AppointmentStatus.CONFIRMED).stream()
               .map(this::convertToDTO)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentEmployeeDTO> appointmentHistory(Long employeeId) {
        return appointmentRepository.findByEmployeeIdAndStatusWithRelations(employeeId, AppointmentStatus.COMPLETED).stream()
               .map(this::convertToDTO)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentEmployeeDTO> appointmentHistoryPaged(Long employeeId, Pageable pageable) {
        return appointmentRepository.findByEmployeeIdAndStatusWithRelationsPaged(
                employeeId, AppointmentStatus.COMPLETED, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long employeeId, Long appointmentId) {
        // Load with all relations to send cancellation email without extra queries
        Appointment appointment = appointmentRepository.findByIdWithRelations(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        if (!appointment.getEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException("This appointment does not belong to this employee");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        // Notify the client that the employee cancelled
        try {
            emailService.sendCancellationNotification(
                    appointment.getClient().getEmail(),
                    appointment.getClient().getName(),
                    appointment.getStartDateTime(),
                    appointment.getService().getName(),
                    appointment.getShop().getName(),
                    "el empleado " + appointment.getEmployee().getName());
        } catch (Exception e) {
            log.error("Error sending cancellation notification for appointment id={}", appointmentId, e);
        }
    }

    @Override
    @Transactional
    public void confirmAppointment(Long employeeId, Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        if (!appointment.getEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException("This appointment does not belong to this employee");
        }

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
            log.error("Error sending confirmation email for appointment id={}", appointmentId, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopOfferingDTO> getServicesByEmployee(Long employeeId) {
        Employee employee = employeeService.getEmployeeEntity(employeeId);
        return employee.getShop().getServices().stream()
                .map(s -> {
                    ShopOfferingDTO dto = new ShopOfferingDTO();
                    dto.setId(s.getId());
                    dto.setName(s.getName());
                    dto.setDescription(s.getDescription());
                    dto.setDuration(s.getDuration());
                    dto.setPrice(s.getPrice());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateEarnings(Long employeeId, LocalDate from, LocalDate to) {
        LocalDateTime startDate = from.atStartOfDay();
        LocalDateTime endDate = to.atTime(23, 59);

        List<Appointment> appointments = appointmentRepository.findByEmployeeIdAndStatusAndDateRangeWithService(
                employeeId, AppointmentStatus.COMPLETED, startDate, endDate);

        int totalSum = 0;
        for (Appointment appointment : appointments) {
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
