package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.ClienteDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoClienteDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Cliente;
import com.gaston.sistema.turno.sistematunos_back.entities.Turno;
import com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno;
import com.gaston.sistema.turno.sistematunos_back.entities.Resenia;
import com.gaston.sistema.turno.sistematunos_back.repositories.ClienteRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ReseniaRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.TurnoRepository;


@Service
public class ClienteServiceImp implements ClienteService{

    private final ClienteRepository clienteRepository;
    private final ReseniaRepository reseniaRepository;
    private final TurnoRepository turnoRepository;

    public ClienteServiceImp(ClienteRepository clienteRepository, ReseniaRepository reseniaRepository, TurnoRepository turnoRepository) {
        this.clienteRepository = clienteRepository;
        this.reseniaRepository = reseniaRepository;
        this.turnoRepository = turnoRepository;
    }

    @Override
    @Transactional
    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
          Cliente clienteDb = clienteRepository.findById(id).orElseThrow(()->
                                                    new IllegalArgumentException("no se encontro el cliente con este Id: "+id));
          return clienteDb;
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClienteDTO obtenerClienteDTOPorId(Long id) {
        Cliente cliente = obtenerPorId(id);
        return convertirAClienteDTO(cliente);
    }

  
    @Override
    @Transactional
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = obtenerPorId(id);
        if (!cliente.getEmail().equals(clienteDTO.getEmail())) {
            Optional<Cliente> clienteExistente = findByEmail(clienteDTO.getEmail());
            if (clienteExistente.isPresent()) {
                throw new IllegalArgumentException("El email ya está en uso");
            }
        }
        
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellido(clienteDTO.getApellido());
        cliente.setEmail(clienteDTO.getEmail());
        
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertirAClienteDTO(clienteActualizado);
    }

    @Override
    @Transactional
    public void eliminarCliente(Long id) {
       if (!clienteRepository.existsById(id)) {
             throw new IllegalArgumentException("Cliente no encontrado");
        }
        clienteRepository.deleteById(id);
    }

    ////////// TURNOS //////////
    /// ///////////////////////
    
    @Override
    @Transactional(readOnly = true)
    public List<TurnoClienteDTO> obtenerTurnosActivos(Long clienteId) {
       return turnoRepository.buscarTurnosActivosCliente(clienteId, LocalDateTime.now())
                .stream()
                .map(this::convertirATurnoClienteDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoClienteDTO> obtenerHistorialTurnos(Long clienteId) {
      return turnoRepository.buscarHistorialCliente(clienteId, LocalDateTime.now())
                .stream()
                .map(this::convertirATurnoClienteDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelarTurno(Long clienteId, Long turnoId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));

        if (!turno.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("Este turno no pertenece al cliente");
        }
        
        // if (turno.getFechaHoraInicio().isBefore(LocalDateTime.now())) {
        //     throw new IllegalArgumentException("No se puede cancelar un turno pasado");
        // }
        
        turno.setEstado(EstadoTurno.CANCELADO);
        turnoRepository.save(turno);
    }

    private ClienteDTO convertirAClienteDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setEmail(cliente.getEmail());
        return dto;
    }

    private TurnoClienteDTO convertirATurnoClienteDTO(Turno turno) {
        TurnoClienteDTO dto = new TurnoClienteDTO();
        dto.setId(turno.getId());
        dto.setFechaHoraInicio(turno.getFechaHoraInicio());
        dto.setFechaHoraFin(turno.getFechaHoraFin());
        dto.setEstado(turno.getEstado().name()); 
        
        if (turno.getLocal() != null) {
            dto.setNombreLocal(turno.getLocal().getNombre());
            dto.setDireccionLocal(turno.getLocal().getDireccion());
        }
        
        if (turno.getServicio() != null) {
            dto.setServicio(turno.getServicio().getNombre());
            dto.setPrecio(turno.getServicio().getPrecio());
        }

        Optional<Resenia> resenia = reseniaRepository.findByTurnoId(turno.getId());
        if (resenia.isPresent()) {
            dto.setCalificacion(resenia.get().getCalificacion());
            dto.setComentario(resenia.get().getComentario());
        }
        
        return dto;
    }

}
