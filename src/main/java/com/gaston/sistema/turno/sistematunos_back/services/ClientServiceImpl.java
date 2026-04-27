package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.ChangePasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.ClientDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentClientDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Client;
import com.gaston.sistema.turno.sistematunos_back.entities.Appointment;
import com.gaston.sistema.turno.sistematunos_back.entities.AppointmentStatus;
import com.gaston.sistema.turno.sistematunos_back.entities.Review;
import com.gaston.sistema.turno.sistematunos_back.repositories.ClientRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ReviewRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.AppointmentRepository;

@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ClientRepository clientRepository;
    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ClientServiceImpl(ClientRepository clientRepository, ReviewRepository reviewRepository,
            AppointmentRepository appointmentRepository, PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.clientRepository = clientRepository;
        this.reviewRepository = reviewRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Client getById(Long id) {
          Client clientDb = clientRepository.findById(id).orElseThrow(()->
                                                    new IllegalArgumentException("no se encontro el cliente con este Id: "+id));
          return clientDb;
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientDTOById(Long id) {
        Client client = getById(id);
        return convertToClientDTO(client);
    }

    @Override
    @Transactional
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client client = getById(id);
        if (!client.getEmail().equals(clientDTO.getEmail())) {
            Optional<Client> existingClient = findByEmail(clientDTO.getEmail());
            if (existingClient.isPresent()) {
                throw new IllegalArgumentException("El email ya está en uso");
            }
        }

        client.setName(clientDTO.getName());
        client.setLastName(clientDTO.getLastName());
        client.setEmail(clientDTO.getEmail());

        Client updatedClient = clientRepository.save(client);
        return convertToClientDTO(updatedClient);
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
       if (!clientRepository.existsById(id)) {
             throw new IllegalArgumentException("Cliente no encontrado");
        }
        clientRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void changePassword(Long clientId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }
        Client client = getById(clientId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), client.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        client.setPassword(passwordEncoder.encode(request.getNewPassword()));
        clientRepository.save(client);
    }

    ////////// APPOINTMENTS //////////

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentClientDTO> getActiveAppointments(Long clientId) {
       return appointmentRepository.findActiveClientAppointmentsWithRelations(clientId, LocalDateTime.now())
                .stream()
                .map(a -> convertToAppointmentClientDTO(a, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentClientDTO> getAppointmentHistory(Long clientId) {
        List<Appointment> appointments = appointmentRepository.findClientHistoryWithRelations(
                clientId, LocalDateTime.now());

        // Load all reviews for this batch in a single query — eliminates N+1
        List<Long> appointmentIds = appointments.stream().map(Appointment::getId).toList();
        Map<Long, Review> reviewsByAppointment = reviewRepository.findByAppointmentIdIn(appointmentIds)
                .stream()
                .collect(Collectors.toMap(r -> r.getAppointment().getId(), r -> r));

        return appointments.stream()
                .map(a -> convertToAppointmentClientDTO(a, reviewsByAppointment.get(a.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentClientDTO> getAppointmentHistoryPaged(Long clientId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findClientHistoryWithRelationsPaged(
                clientId, LocalDateTime.now(), pageable);

        // Batch-load reviews for this page to eliminate N+1
        List<Long> appointmentIds = page.getContent().stream().map(Appointment::getId).toList();
        Map<Long, Review> reviewsByAppointment = reviewRepository.findByAppointmentIdIn(appointmentIds)
                .stream()
                .collect(Collectors.toMap(r -> r.getAppointment().getId(), r -> r));

        return page.map(a -> convertToAppointmentClientDTO(a, reviewsByAppointment.get(a.getId())));
    }

    @Override
    @Transactional
    public void cancelAppointment(Long clientId, Long appointmentId) {
        // Load appointment with all relations in a single query (avoids N+1 when sending emails)
        Appointment appointment = appointmentRepository.findByIdWithRelations(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));

        if (!appointment.getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException("Este turno no pertenece al cliente");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
                appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Este turno no puede cancelarse en su estado actual");
        }

        // Cancellation window: must be at least 2 hours before the appointment
        if (appointment.getStartDateTime().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException(
                    "No se puede cancelar un turno con menos de 2 horas de anticipación");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        // Notify the employee that the client cancelled
        try {
            emailService.sendCancellationNotification(
                    appointment.getEmployee().getEmail(),
                    appointment.getEmployee().getName(),
                    appointment.getStartDateTime(),
                    appointment.getService().getName(),
                    appointment.getShop().getName(),
                    "el cliente " + appointment.getClient().getName());
        } catch (Exception e) {
            log.error("Error sending cancellation notification for appointment id={}", appointmentId, e);
        }
    }

    private ClientDTO convertToClientDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setLastName(client.getLastName());
        dto.setEmail(client.getEmail());
        return dto;
    }

    private AppointmentClientDTO convertToAppointmentClientDTO(Appointment appointment, Review review) {
        AppointmentClientDTO dto = new AppointmentClientDTO();
        dto.setId(appointment.getId());
        dto.setStartDateTime(appointment.getStartDateTime());
        dto.setEndDateTime(appointment.getEndDateTime());
        dto.setStatus(appointment.getStatus().name());

        if (appointment.getShop() != null) {
            dto.setShopName(appointment.getShop().getName());
            dto.setShopAddress(appointment.getShop().getAddress());
        }

        if (appointment.getService() != null) {
            dto.setService(appointment.getService().getName());
            dto.setPrice(appointment.getService().getPrice());
        }

        if (review != null) {
            dto.setRating(review.getRating());
            dto.setComment(review.getComment());
        }

        return dto;
    }
}
