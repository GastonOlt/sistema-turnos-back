package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.SlotDisponibleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Cliente;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Turno;
import com.gaston.sistema.turno.sistematunos_back.repositories.TurnoRepository;

@Service
public class TurnoServiceImp implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final EmpleadoService empleadoService;
    private final ServicioLocalService servicioLocalService;
    private final LocalService localService;
    private final ClienteService clienteService;
    private final EmailService emailService;
    private final CalculadoraDisponibilidadService calculadoraDisponibilidadService;

    public TurnoServiceImp(TurnoRepository turnoRepository, EmpleadoService empleadoService,
            ServicioLocalService servicioLocalService, LocalService localService, ClienteService clienteService,
            EmailService emailService, CalculadoraDisponibilidadService calculadoraDisponibilidadService) {
        this.turnoRepository = turnoRepository;
        this.empleadoService = empleadoService;
        this.servicioLocalService = servicioLocalService;
        this.localService = localService;
        this.clienteService = clienteService;
        this.emailService = emailService;
        this.calculadoraDisponibilidadService = calculadoraDisponibilidadService;
    }


    @Override
    @Transactional
    public TurnoResponseDTO reservarTurno(Long clienteId, TurnoRequestDTO turnoRequest) {
        
        List<EstadoTurno> estados = Arrays.asList(EstadoTurno.CONFIRMADO,EstadoTurno.PENDIENTE);

        if (turnoRepository.existsByClienteIdAndEstadoIn(clienteId, estados)) {
            throw new RuntimeException("Ya tienes un turno . No puedes reservar más de uno.");
        }
        Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(turnoRequest.getEmpleadoId());
        ServicioLocal servicioDb = servicioLocalService.obtenerServicioEntity(turnoRequest.getServicioId());
        Local localDb = localService.obtenerLocalPorId(turnoRequest.getLocalId());
        Cliente clienteDb = clienteService.obtenerPorId(clienteId);

        validarConsistenciaLocal(empleadoDb, servicioDb, localDb);

        LocalDateTime fechaHoraInicio = turnoRequest.getFechaHoraInicio();
        
        if (fechaHoraInicio.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar turnos en el pasado");
        }

        LocalDateTime fechaHoraFin =fechaHoraInicio.plusMinutes(servicioDb.getTiempo());
       
        boolean ocupado = turnoRepository.existsByEmpleadoAndHorarioSolapado(empleadoDb.getId(), fechaHoraInicio, fechaHoraFin);
        if(ocupado){
                throw new RuntimeException("El empleado no está disponible en ese horario");
        }

        Turno turno = crearEntidadTurno(clienteDb, empleadoDb, servicioDb, localDb, fechaHoraInicio, fechaHoraFin, EstadoTurno.PENDIENTE);
        Turno turnoNuevo = turnoRepository.save(turno);

        return convertirDto(turnoNuevo);

    }

    //para que el cliente pueda ver los slots disponibles para el empleado y servicio seleccionado
    @Override
    @Transactional(readOnly = true)
    public List<SlotDisponibleDTO> obtenerSlotsDisponibles(Long localId, Long empleadoId, Long servicioId, LocalDate fecha) {
        Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(empleadoId);
        ServicioLocal servicioDb = servicioLocalService.obtenerServicioEntity(servicioId);
        Local localDb = localService.obtenerLocalPorId(localId);

        validarConsistenciaLocal(empleadoDb, servicioDb, localDb);

        return calculadoraDisponibilidadService.calcularSlots(empleadoDb, servicioDb, fecha);
    }

    //para que el empleado pueda ver los slots disponibles para el servicio que brinda
    @Override
    @Transactional(readOnly = true)
    public List<SlotDisponibleDTO> obtenerSlotsDisponibles(Long empleadoId, Long servicioId, LocalDate fecha) {
         Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(empleadoId);
         Long localId = empleadoDb.getLocal().getId();
         return this.obtenerSlotsDisponibles(localId, empleadoId, servicioId, fecha);
    }

   
    //metodo para que el empleado pueda crear el registro de turno a un cliente sin reserva previa , para que quede registro
    @Override
    @Transactional
    public TurnoResponseDTO crearTurnoEmpleado(Long empleadoId, TurnoRequestDTO turnoRequest){
        Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(empleadoId);
        Local localDb = empleadoDb.getLocal();
        ServicioLocal servicioDb = servicioLocalService.obtenerServicioEntity(turnoRequest.getServicioId());

        Cliente clienteAnonimo = clienteService.findByEmail("anonimo@sistema.com")
                                            .orElseThrow(() -> new RuntimeException("Cliente anónimo no configurado"));

        LocalDateTime fechaInicio = turnoRequest.getFechaHoraInicio();
        LocalDateTime fechaFin = fechaInicio.plusMinutes(servicioDb.getTiempo());

        boolean ocupado = turnoRepository.existsByEmpleadoAndHorarioSolapado(empleadoId,fechaInicio,fechaFin);
        if(ocupado){
            throw new IllegalArgumentException("Horario no disponible para hacer una reserva");
        }

        Turno turno = crearEntidadTurno(clienteAnonimo, empleadoDb, servicioDb, localDb, fechaInicio, fechaFin, EstadoTurno.CONFIRMADO);
        Turno nuevoTurno = turnoRepository.save(turno);
        
        return convertirDto(nuevoTurno);
    }


     //para actualizar los turnos que estan confirmados antes de esta fecha
    @Override
    @Transactional
    public void actualizarTurnosFinalizados(){
        LocalDateTime ahora = LocalDateTime.now();

        List<Turno> turnosVencidos = turnoRepository.findAllByEstadoAndFechaHoraFinBefore(EstadoTurno.CONFIRMADO,ahora);

        if(!turnosVencidos.isEmpty()){
            for(Turno turno : turnosVencidos){
                turno.setEstado(EstadoTurno.FINALIZADO);
            }
            turnoRepository.saveAll(turnosVencidos);
        }
    }

    //para enviar email a los turnos que estan confirmados , de el dia actual 
    @Override 
    @Transactional(readOnly = true)
    public void enviarRecordatorioTurno(){
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(23,59);

        List<Turno> turnosDelDia = turnoRepository.findByEstadoAndFechaHoraInicioBetween(EstadoTurno.CONFIRMADO,inicioDia,finDia);

            for (Turno turno : turnosDelDia) {
                try {
                    if (turno.getCliente() != null) {
                           emailService.enviarRecordatorioTurno(turno.getCliente().getEmail(),
                                        turno.getCliente().getNombre(),
                                        turno.getFechaHoraInicio(),
                                        turno.getServicio().getNombre(),
                                        turno.getLocal().getNombre(),
                                        turno.getLocal().getDireccion());
                    }
                } catch (Exception e) {
                    System.err.println("Error enviando recordatorio al turno " + turno.getId());
                }
            }
    }
    

    private void validarConsistenciaLocal(Empleado e, ServicioLocal s, Local l) {
        if (!e.getLocal().getId().equals(l.getId())) {
            throw new RuntimeException("El empleado no pertenece a este local");
        }
        if (!s.getLocal().getId().equals(l.getId())) {
            throw new RuntimeException("El servicio no pertenece a este local");
        }
    }

    private Turno crearEntidadTurno(Cliente c, Empleado e, ServicioLocal s, Local l, LocalDateTime inicio, LocalDateTime fin, EstadoTurno estado) {
        Turno turno = new Turno();
        turno.setCliente(c);
        turno.setEmpleado(e);
        turno.setServicio(s);
        turno.setLocal(l);
        turno.setFechaHoraInicio(inicio);
        turno.setFechaHoraFin(fin);
        turno.setEstado(estado);
        turno.setAdelantado(false);
        return turno;
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
