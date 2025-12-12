package com.gaston.sistema.turno.sistematunos_back.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Autowired
    private EmailService emailService;

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
    public List<SlotDisponibleDTO> obtenerSlotsDisponibles(Long empleadoId, Long servicioId, LocalDate fecha) {
         Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(empleadoId);
         Long localId = empleadoDb.getLocal().getId();
         return this.obtenerSlotsDisponibles( localId, empleadoId, servicioId, fecha);
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

    //para actualizar los turnos que estan confirmados antes de esta fecha
    @Scheduled(fixedRate = 600000)
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
    @Scheduled(cron = "0 0 8 * * *") 
    @Transactional(readOnly = true)
    public void enviarRecordatorioTurno(){
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(23,59);

        List<Turno> turnosDelDia = turnoRepository.findByEstadoAndFechaHoraInicioBetween(EstadoTurno.CONFIRMADO,inicioDia,finDia);

        if(!turnosDelDia.isEmpty()){
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
    }
    
    //metodo para que el empleado pueda crear el registro de turno a un cliente sin reserva previa , para que quede registro
    @Override
    @Transactional
    public TurnoResponseDTO crearTurnoEmpleado(Long empleadoId, TurnoRequestDTO turnoRequest){
        Empleado empleadoDb = empleadoService.obtenerEmpleadoEntity(empleadoId);
        Long localId = empleadoDb.getLocal().getId();
        Local localDb = localService.obtenerLocalPorId(localId);
        ServicioLocal servicioDb = servicioLocalService.obtenerServicioEntity(turnoRequest.getServicioId());
        Cliente clienteAnonimo = clienteService.findByEmail("anonimo@sistema.com")
                                            .orElseThrow(() -> new RuntimeException("Cliente anónimo no configurado"));

        LocalDateTime horarioFin = turnoRequest.getFechaHoraInicio().plusMinutes(servicioDb.getTiempo());
        boolean ocupado = turnoRepository.existsByEmpleadoAndHorarioSolapado(empleadoId,turnoRequest.getFechaHoraInicio(),horarioFin);
        if(ocupado){
            throw new IllegalArgumentException("Horario no disponible para hacer una reserva");
        }

        Turno turno = new Turno();
        turno.setCliente(clienteAnonimo);
        turno.setEmpleado(empleadoDb);
        turno.setLocal(localDb);
        turno.setServicio(servicioDb);
        turno.setFechaHoraInicio(turnoRequest.getFechaHoraInicio());
        turno.setFechaHoraFin(horarioFin);
        turno.setEstado(EstadoTurno.CONFIRMADO);

        Turno nuevoTurno = turnoRepository.save(turno);
        return convertirDto(nuevoTurno);
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
