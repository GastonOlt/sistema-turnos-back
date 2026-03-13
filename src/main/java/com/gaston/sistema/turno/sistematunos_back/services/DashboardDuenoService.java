package com.gaston.sistema.turno.sistematunos_back.services;

import com.gaston.sistema.turno.sistematunos_back.dto.MetricasDashboardDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.RendimientoEmpleadoDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.TurnoDuenoDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Dueno;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.entities.Turno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardDuenoService {

    @Autowired
    private DuenoService duenoService;

    @Transactional(readOnly = true)
    public MetricasDashboardDTO obtenerMetricasDashboard(Long duenoId, LocalDate fechaInicio, LocalDate fechaFin) {
        Dueno dueno = duenoService.findById(duenoId)
                .orElseThrow(() -> new IllegalArgumentException("Dueño no encontrado"));

        Local local = dueno.getLocal();
        if (local == null) {
            throw new IllegalArgumentException("El dueño no tiene un local asignado");
        }

        LocalDateTime inicio = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null) ? fechaFin.atTime(23, 59, 59) : null;

        double gananciaTotal = 0;
        int cantidadTurnosTotal = 0;
        List<RendimientoEmpleadoDTO> rendimientos = new ArrayList<>();
        List<TurnoDuenoDTO> ultimosTurnos = new ArrayList<>();

        List<Empleado> empleadosActivos = local.getEmpleados();

        List<Turno> todosLosTurnosDelLocal = local.getTurnos();

        // Filtrar todos los turnos por fecha si se proporcionaron
        List<Turno> turnosFiltrados = todosLosTurnosDelLocal.stream()
                .filter(t -> {
                    if (inicio != null && t.getFechaHoraInicio().isBefore(inicio))
                        return false;
                    if (fin != null && t.getFechaHoraInicio().isAfter(fin))
                        return false;
                    return true;
                }).collect(Collectors.toList());

        for (Empleado empleado : empleadosActivos) {
            RendimientoEmpleadoDTO rendimiento = new RendimientoEmpleadoDTO();
            rendimiento.setEmpleadoId(empleado.getId());
            rendimiento.setNombre(empleado.getNombre() + " " + empleado.getApellido());

            // Filtrar los turnos del empleado dentro del periodo
            List<Turno> turnosEmpleado = turnosFiltrados.stream()
                    .filter(t -> t.getEmpleado().getId().equals(empleado.getId()))
                    .collect(Collectors.toList());

            int turnosAtendidos = 0;
            double gananciaEmpleado = 0;
            List<TurnoDuenoDTO> turnosDetalle = new ArrayList<>();

            for (Turno turno : turnosEmpleado) {
                if (turno.getEstado() == EstadoTurno.FINALIZADO && turno.getServicio() != null) {
                    turnosAtendidos++;
                    gananciaEmpleado += turno.getServicio().getPrecio();

                    TurnoDuenoDTO tDto = new TurnoDuenoDTO();
                    tDto.setId(turno.getId());
                    tDto.setServicioNombre(turno.getServicio().getNombre());
                    tDto.setPrecio(turno.getServicio().getPrecio());
                    tDto.setFechaHoraInicio(turno.getFechaHoraInicio());
                    tDto.setEstado(turno.getEstado().name());
                    tDto.setClienteNombre(turno.getCliente() != null
                            ? turno.getCliente().getNombre() + " " + turno.getCliente().getApellido()
                            : "N/A");
                    tDto.setEmpleadoNombre(empleado.getNombre() + " " + empleado.getApellido());
                    turnosDetalle.add(tDto);
                }
            }

            rendimiento.setCantidadTurnos(turnosAtendidos);
            rendimiento.setGananciaGenerada(gananciaEmpleado);
            rendimiento.setTurnos(turnosDetalle);
            rendimientos.add(rendimiento);

            gananciaTotal += gananciaEmpleado;
        }

        cantidadTurnosTotal = turnosFiltrados.size(); // O turnos finalizados? Dejemos total de turnos movidos.

        // Mapear los ultimos turnos filtrados
        ultimosTurnos = turnosFiltrados.stream()
                .sorted(Comparator.comparing(Turno::getFechaHoraInicio).reversed())
                .limit(20) // Limitamos a los ultimos 20 turnos
                .map(t -> {
                    TurnoDuenoDTO tDto = new TurnoDuenoDTO();
                    tDto.setId(t.getId());
                    tDto.setEmpleadoNombre(t.getEmpleado().getNombre() + " " + t.getEmpleado().getApellido());
                    tDto.setClienteNombre(
                            t.getCliente() != null ? t.getCliente().getNombre() + " " + t.getCliente().getApellido()
                                    : "N/A");
                    tDto.setServicioNombre(t.getServicio() != null ? t.getServicio().getNombre() : "N/A");
                    tDto.setFechaHoraInicio(t.getFechaHoraInicio());
                    tDto.setEstado(t.getEstado().name());
                    tDto.setPrecio(t.getServicio() != null ? t.getServicio().getPrecio() : 0.0);
                    return tDto;
                })
                .collect(Collectors.toList());

        MetricasDashboardDTO metricas = new MetricasDashboardDTO();
        metricas.setGananciaTotal(gananciaTotal);
        metricas.setCantidadTurnosTotal(cantidadTurnosTotal);
        metricas.setEmpleadosActivos(empleadosActivos.size());
        metricas.setRendimientoEmpleados(rendimientos);
        metricas.setUltimosTurnos(ultimosTurnos);

        return metricas;
    }
}
