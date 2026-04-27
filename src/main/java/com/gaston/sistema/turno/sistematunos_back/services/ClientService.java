package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;
import java.util.Optional;

import com.gaston.sistema.turno.sistematunos_back.dto.ChangePasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.ClientDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.AppointmentClientDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Client;

public interface ClientService {
    Client createClient(Client client);
    Optional<Client> findByEmail(String email);
    Client getById(Long id);
    ClientDTO getClientDTOById(Long id);
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    void deleteClient(Long id);
    void changePassword(Long clientId, ChangePasswordRequest request);

    List<AppointmentClientDTO> getActiveAppointments(Long clientId);
    List<AppointmentClientDTO> getAppointmentHistory(Long clientId);
    void cancelAppointment(Long clientId, Long appointmentId);
}
