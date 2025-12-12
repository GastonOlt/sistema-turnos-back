package com.gaston.sistema.turno.sistematunos_back.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.TurnoEmpleadoDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Turno;
import com.gaston.sistema.turno.sistematunos_back.repositories.TurnoRepository;

@Service
public class TurnoEmpleadoServiceImp implements TurnoEmpleadoService{

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired 
    private EmailService emailService;
    
    @Override
    @Transactional(readOnly = true)
    public List<TurnoEmpleadoDTO> listadoTurnoPendientes(Long empleadoId) {
        return turnoRepository.findByEmpleadoIdAndEstado(empleadoId, EstadoTurno.PENDIENTE).stream()
               .map(this::convertirATurnoEmpleadoDTO)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoEmpleadoDTO> listadoTurnoConfirmados(Long empleadoId) {
        return turnoRepository.findByEmpleadoIdAndEstado(empleadoId, EstadoTurno.CONFIRMADO).stream()
               .map(this::convertirATurnoEmpleadoDTO)
               .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TurnoEmpleadoDTO> historialTurnos(Long empleadoId) {
        return turnoRepository.findByEmpleadoIdAndEstado(empleadoId, EstadoTurno.FINALIZADO).stream()
               .map(this::convertirATurnoEmpleadoDTO)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelarTurno(Long empleadoId, Long turnoId) {
        Empleado emp = empleadoService.obtenerEmpleadoEntity(empleadoId);
        Turno turno = emp.getTurnos().stream()
        .filter(tur -> tur.getId().equals(turnoId))
        .findFirst()
        .orElseThrow(()->new IllegalArgumentException("no se encontro el turno"));

        turno.setEstado(EstadoTurno.CANCELADO);
        turnoRepository.save(turno);
    }

    @Override
    @Transactional
    public void confirmarTurno(Long empleadoId, Long turnoId) {
        Empleado emp = empleadoService.obtenerEmpleadoEntity(empleadoId);
        Turno turno = emp.getTurnos().stream()
        .filter(tur -> tur.getId().equals(turnoId))
        .findFirst()
        .orElseThrow(()->new IllegalArgumentException("no se encontro el turno"));

        turno.setEstado(EstadoTurno.CONFIRMADO);
        turnoRepository.save(turno);
       try {
            if (turno.getCliente() != null) {
                emailService.enviarConfirmacionTurno(
                    turno.getCliente().getEmail(),
                    turno.getCliente().getNombre(),
                    turno.getFechaHoraInicio(),
                    turno.getServicio().getNombre(),
                    turno.getLocal().getNombre(),
                    turno.getLocal().getDireccion()
                );
            }
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioLocal> obtenerServiciosPorEmpleado(Long empleadoId) {
        Empleado emp = empleadoService.obtenerEmpleadoEntity(empleadoId);
        return emp.getLocal().getServicios();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularGanancias(Long empleadoId, LocalDate desde, LocalDate hasta) {
        LocalDateTime fechaInicio = desde.atStartOfDay();
        LocalDateTime fechaFin = hasta.atTime(23, 59);

        List<Turno> turnos = turnoRepository.findByEmpleadoIdAndEstadoAndFechaHoraInicioBetween(empleadoId,EstadoTurno.FINALIZADO,fechaInicio,fechaFin);

        int sumaTotal = 0;

        for(Turno turno : turnos){
            sumaTotal += turno.getServicio().getPrecio();
        }

        return BigDecimal.valueOf(sumaTotal);

    }

    public TurnoEmpleadoDTO convertirATurnoEmpleadoDTO(Turno turno){
      TurnoEmpleadoDTO dto = new TurnoEmpleadoDTO();
        dto.setId(turno.getId());
        dto.setFechaHoraInicio(turno.getFechaHoraInicio());
        dto.setFechaHoraFin(turno.getFechaHoraFin());
        dto.setNombreCliente(turno.getCliente().getNombre()+" "+turno.getCliente().getApellido( ));
        dto.setEstado(turno.getEstado().name()); 
        dto.setServicio(turno.getServicio().getNombre());
        dto.setPrecio(turno.getServicio().getPrecio());
        return dto;
    }

   

}
