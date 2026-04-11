package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.AvailableSlotDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Client;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopOffering;
import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.repositories.AppointmentRepository;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final EmployeeService employeeService;
    private final ShopOfferingService shopOfferingService;
    private final ShopService shopService;
    private final ClientService clientService;
    private final EmailService emailService;
    private final AvailabilityCalculatorService availabilityCalculatorService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, EmployeeService employeeService,
            ShopOfferingService shopOfferingService, ShopService shopService, ClientService clientService,
            EmailService emailService, AvailabilityCalculatorService availabilityCalculatorService) {
        this.appointmentRepository = appointmentRepository;
        this.employeeService = employeeService;
        this.shopOfferingService = shopOfferingService;
        this.shopService = shopService;
        this.clientService = clientService;
        this.emailService = emailService;
        this.availabilityCalculatorService = availabilityCalculatorService;
    }

    @Override
    @Transactional
    public AppointmentResponseDTO bookAppointment(Long clientId, AppointmentRequestDTO request) {
        List<AppointmentStatus> statuses = Arrays.asList(AppointmentStatus.CONFIRMED, AppointmentStatus.PENDING);

        if (appointmentRepository.existsByClientIdAndStatusIn(clientId, statuses)) {
            throw new RuntimeException("Ya tienes un turno . No puedes reservar más de uno.");
        }
        Employee employeeDb = employeeService.getEmployeeEntity(request.getEmployeeId());
        ShopOffering serviceDb = shopOfferingService.getServiceEntity(request.getServiceId());
        Shop shopDb = shopService.getShopById(request.getShopId());
        Client clientDb = clientService.getById(clientId);

        validateShopConsistency(employeeDb, serviceDb, shopDb);

        LocalDateTime startDateTime = request.getStartDateTime();

        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar turnos en el pasado");
        }

        LocalDateTime endDateTime = startDateTime.plusMinutes(serviceDb.getDuration());

        boolean occupied = appointmentRepository.existsByEmployeeAndOverlappingSchedule(employeeDb.getId(), startDateTime, endDateTime);
        if(occupied){
                throw new RuntimeException("El empleado no está disponible en ese horario");
        }

        Appointment appointment = createAppointmentEntity(clientDb, employeeDb, serviceDb, shopDb, startDateTime, endDateTime, AppointmentStatus.PENDING);
        Appointment newAppointment = appointmentRepository.save(appointment);

        return convertToDTO(newAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableSlotDTO> getAvailableSlots(Long shopId, Long employeeId, Long serviceId, LocalDate date) {
        Employee employeeDb = employeeService.getEmployeeEntity(employeeId);
        ShopOffering serviceDb = shopOfferingService.getServiceEntity(serviceId);
        Shop shopDb = shopService.getShopById(shopId);

        validateShopConsistency(employeeDb, serviceDb, shopDb);

        return availabilityCalculatorService.calculateSlots(employeeDb, serviceDb, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableSlotDTO> getAvailableSlots(Long employeeId, Long serviceId, LocalDate date) {
         Employee employeeDb = employeeService.getEmployeeEntity(employeeId);
         Long shopId = employeeDb.getShop().getId();
         return this.getAvailableSlots(shopId, employeeId, serviceId, date);
    }

    @Override
    @Transactional
    public AppointmentResponseDTO createEmployeeAppointment(Long employeeId, AppointmentRequestDTO request){
        Employee employeeDb = employeeService.getEmployeeEntity(employeeId);
        Shop shopDb = employeeDb.getShop();
        ShopOffering serviceDb = shopOfferingService.getServiceEntity(request.getServiceId());

        Client anonymousClient = clientService.findByEmail("anonimo@sistema.com")
                                            .orElseThrow(() -> new RuntimeException("Cliente anónimo no configurado"));

        LocalDateTime startDateTime = request.getStartDateTime();
        LocalDateTime endDateTime = startDateTime.plusMinutes(serviceDb.getDuration());

        boolean occupied = appointmentRepository.existsByEmployeeAndOverlappingSchedule(employeeId, startDateTime, endDateTime);
        if(occupied){
            throw new IllegalArgumentException("Horario no disponible para hacer una reserva");
        }

        Appointment appointment = createAppointmentEntity(anonymousClient, employeeDb, serviceDb, shopDb, startDateTime, endDateTime, AppointmentStatus.CONFIRMED);
        Appointment newAppointment = appointmentRepository.save(appointment);

        return convertToDTO(newAppointment);
    }

    @Override
    @Transactional
    public void updateCompletedAppointments(){
        LocalDateTime now = LocalDateTime.now();
        List<Appointment> expiredAppointments = appointmentRepository.findAllByStatusAndEndDateTimeBefore(AppointmentStatus.CONFIRMED, now);

        if(!expiredAppointments.isEmpty()){
            for(Appointment appointment : expiredAppointments){
                appointment.setStatus(AppointmentStatus.COMPLETED);
            }
            appointmentRepository.saveAll(expiredAppointments);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void sendAppointmentReminder(){
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23,59);

        List<Appointment> todayAppointments = appointmentRepository.findByStatusAndStartDateTimeBetween(AppointmentStatus.CONFIRMED, startOfDay, endOfDay);

            for (Appointment appointment : todayAppointments) {
                try {
                    if (appointment.getClient() != null) {
                           emailService.sendAppointmentReminder(appointment.getClient().getEmail(),
                                        appointment.getClient().getName(),
                                        appointment.getStartDateTime(),
                                        appointment.getService().getName(),
                                        appointment.getShop().getName(),
                                        appointment.getShop().getAddress());
                    }
                } catch (Exception e) {
                    System.err.println("Error enviando recordatorio al turno " + appointment.getId());
                }
            }
    }

    private void validateShopConsistency(Employee e, ShopOffering s, Shop shop) {
        if (!e.getShop().getId().equals(shop.getId())) {
            throw new RuntimeException("El empleado no pertenece a este local");
        }
        if (!s.getShop().getId().equals(shop.getId())) {
            throw new RuntimeException("El servicio no pertenece a este local");
        }
    }

    private Appointment createAppointmentEntity(Client c, Employee e, ShopOffering s, Shop shop, LocalDateTime start, LocalDateTime end, AppointmentStatus status) {
        Appointment appointment = new Appointment();
        appointment.setClient(c);
        appointment.setEmployee(e);
        appointment.setService(s);
        appointment.setShop(shop);
        appointment.setStartDateTime(start);
        appointment.setEndDateTime(end);
        appointment.setStatus(status);
        appointment.setEarly(false);
        return appointment;
    }

    public AppointmentResponseDTO convertToDTO(Appointment appointment){
        return new AppointmentResponseDTO(
            appointment.getId(),
            appointment.getEmployee().getName() +" "+appointment.getEmployee().getLastName(),
            appointment.getService().getName(),
            appointment.getShop().getName(),
            appointment.getStartDateTime(),
            appointment.getEndDateTime(),
            appointment.getStatus().name(),
            appointment.isEarly()
            );
    }
}
