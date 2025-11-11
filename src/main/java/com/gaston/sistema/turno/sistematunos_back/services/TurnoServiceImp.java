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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.SlotDisponibleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Cliente;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno;
import com.gaston.sistema.turno.sistematunos_back.entities.Horario;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Turno;
import com.gaston.sistema.turno.sistematunos_back.repositories.TurnoRepository;

@Service
public class TurnoServiceImp implements TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ServicioLocalService servicioLocalService;

    @Autowired
    private LocalService localService;

    @Autowired
    private ClienteService clienteService;

    @Override
    @Transactional
    public TurnoResponseDTO reservarTurno(Long clienteId, TurnoRequestDTO turnoRequest) {
            if (turnoRepository.existsByClienteIdAndEstado(clienteId, EstadoTurno.CONFIRMADO)) {
                throw new RuntimeException("Ya tienes un turno confirmado. No puedes reservar más de uno.");
            }
            Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(turnoRequest.getEmpleadoId());
            ServicioLocal servicioDb = servicioLocalService.obtenerServicioEntity(turnoRequest.getServicioId());
            Local localDb = localService.obtenerLocalPorId(turnoRequest.getLocalId());
            Cliente clienteDb = clienteService.obtenerPorId(clienteId);

            if(!empleadoDb.getLocal().getId().equals(localDb.getId())){
                throw new RuntimeException("El empleado no pertenece a este local");
            }

            if (!servicioDb.getLocal().getId().equals(localDb.getId())) {
               throw new RuntimeException("El servicio no pertenece a este local");
            }
 
            LocalDateTime fechaHoraFin = turnoRequest.getFechaHoraInicio().plusMinutes(servicioDb.getTiempo());
            boolean ocupado = turnoRepository.existsByEmpleadoAndHorarioSolapado(empleadoDb.getId(), turnoRequest.getFechaHoraInicio(), fechaHoraFin);

            if(ocupado){
                 throw new RuntimeException("El empleado no está disponible en ese horario");
            }
            if(turnoRequest.getFechaHoraInicio().isBefore(LocalDateTime.now())){
                    throw new RuntimeException("no se puede reservar turnos en el pasado");
            }

            Turno turno = new Turno();

            turno.setCliente(clienteDb);
            turno.setEmpleado(empleadoDb);
            turno.setServicio(servicioDb);
            turno.setLocal(localDb);
            turno.setFechaHoraInicio(turnoRequest.getFechaHoraInicio());
            turno.setFechaHoraFin(fechaHoraFin);
            turno.setEstado(EstadoTurno.PENDIENTE);
            turno.setAdelantado(false);

            Turno turnoNuevo = turnoRepository.save(turno);

            return convertirDto(turnoNuevo);

    }

    
    @Override
    @Transactional(readOnly = true)
    public List<SlotDisponibleDTO> obtenerSlotsDisponibles(Long localId, Long empleadoId,
                                                                    Long servicioId,LocalDate fecha) {
        Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(empleadoId);
        ServicioLocal servicioDb = servicioLocalService.obtenerServicioEntity(servicioId);
        Local localDb = localService.obtenerLocalPorId(localId);

        if (!empleadoDb.getLocal().getId().equals(localDb.getId())) {
        throw new RuntimeException("El empleado no pertenece a este local");
        }
        if (!servicioDb.getLocal().getId().equals(localDb.getId())) {
            throw new RuntimeException("El servicio no pertenece a este local");
        }

        Locale espaniol =  Locale.of("es", "ES");
        String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, espaniol);
        String diaSemanaCapitalized = Character.toUpperCase(diaSemana.charAt(0)) + diaSemana.substring(1).toLowerCase();
        
        List<Horario> rangos = empleadoDb.getHorarios().stream()
        .filter(h -> h.getDiaSemana().equals(diaSemanaCapitalized) && h.isActivo())
        .sorted(Comparator.comparing(Horario::getHorarioApertura))
        .collect(Collectors.toList());
        
        if(rangos.isEmpty()){
            return new ArrayList<>();
        }
            for(Horario rango : rangos){
                System.out.println("/////////////// RANGOSSSSSSS //////////////");
                System.out.println(rango.getDiaSemana());
                System.out.println(rango.getHorarioApertura());
                System.out.println(rango.getEmpleado());
                System.out.println(rango.getHorarioCierre());

            }

        int duracionMin = servicioDb.getTiempo();
        int intervaloMin =15;

        List<SlotDisponibleDTO> slotDisponibles = new ArrayList<>();

        for(Horario rango : rangos){
            LocalTime apertura = rango.getHorarioApertura();
            LocalTime cierre = rango.getHorarioCierre();
            
            while (!apertura.plusMinutes(duracionMin).isAfter(cierre)) {
                LocalDateTime slotInicial = LocalDateTime.of(fecha,apertura);
                LocalDateTime slotFin = slotInicial.plusMinutes(duracionMin);

                if(slotInicial.isBefore(LocalDateTime.now())){
                    apertura = apertura.plusMinutes(intervaloMin);
                    continue;
                }

                boolean ocupado = turnoRepository.existsByEmpleadoAndHorarioSolapado(empleadoId, slotInicial, slotFin);
                if(!ocupado){
                    slotDisponibles.add(new SlotDisponibleDTO(slotInicial,slotFin));
                }

                apertura = apertura.plusMinutes(intervaloMin);
            }
        }
        return slotDisponibles;
    }
    
    public TurnoResponseDTO convertirDto(Turno turno){
        return new TurnoResponseDTO(
            turno.getId(),
            turno.getEmpleado().getNombre() +" "+turno.getEmpleado().getApellido(),
            turno.getServicio().getNombre(),
            turno.getLocal().getNombre(),
            turno.getFechaHoraInicio(),
            turno.getFechaHoraFin(),
            turno.getEstado().name(),
            turno.isAdelantado()
            );
    }
}
