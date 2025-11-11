package com.gaston.sistema.turno.sistematunos_back.services;



import java.util.List;
import java.util.Optional;

import com.gaston.sistema.turno.sistematunos_back.dto.ClienteDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoClienteDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Cliente;


public interface ClienteService {
    Cliente crearCliente(Cliente cliente);
    Optional<Cliente> findByEmail(String email);
    Cliente obtenerPorId(Long id);
    ClienteDTO obtenerClienteDTOPorId(Long id);
    ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO);
    void eliminarCliente(Long id);
    List<TurnoClienteDTO> obtenerTurnosActivos(Long clienteId);
    List<TurnoClienteDTO> obtenerHistorialTurnos(Long clienteId);
    void cancelarTurno(Long clienteId, Long turnoId);
}
