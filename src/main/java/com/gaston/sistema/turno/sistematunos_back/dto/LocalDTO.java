package com.gaston.sistema.turno.sistematunos_back.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.Horario;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.entities.ServicioLocal;



public class LocalDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String provincia;
    private String telefono;
    private Double latitud;
    private Double longitud;
    private String  nombreDueno;
    private Double promedioCalificacion;

    private List<ImagenLocalDTO> imagenes = new ArrayList<>();
    private List<ServicioLocalDTO> servicios = new ArrayList<>();
    private List<HorarioDTO> horarios = new ArrayList<>();
    private List<EmpleadoDto> empleados = new ArrayList<>();

    
    public LocalDTO(Local local) {
        this.id = local.getId();
        this.nombre = local.getNombre();
        this.descripcion = local.getDescripcion();
        this.provincia = local.getProvincia();
        this.telefono = local.getTelefono();
        this.latitud = local.getLatitud();
        this.longitud = local.getLongitud();
        this.nombreDueno = local.getDueno().getNombre();
        this.promedioCalificacion = local.getPromedioCalificacion();
        
        if (local.getImagenes() != null) {
            this.imagenes = local.getImagenes().stream()
                .map(img -> new ImagenLocalDTO(img.getId(), img.getNombreArchivo()))
                .collect(Collectors.toList());
        }

        if (local.getServicios() != null) {
            this.servicios = local.getServicios().stream()
                .map(this::mapServicio)
                .collect(Collectors.toList());
        }

        if (local.getHorarios() != null) {
            this.horarios = local.getHorarios().stream()
                .map(this::mapHorario)
                .collect(Collectors.toList());
        }

        if (local.getEmpleados() != null) {
            this.empleados = local.getEmpleados().stream()
                .map(this::mapEmpleado)
                .collect(Collectors.toList());
        }
    }

    private ServicioLocalDTO mapServicio(ServicioLocal s) {
        ServicioLocalDTO dto = new ServicioLocalDTO();
        dto.setId(s.getId());
        dto.setNombre(s.getNombre());
        dto.setDescripcion(s.getDescripcion());
        dto.setPrecio(s.getPrecio());
        dto.setTiempo(s.getTiempo());
        return dto;
    }

    private HorarioDTO mapHorario(Horario h) {
        HorarioDTO dto = new HorarioDTO();
        dto.setId(h.getId());
        dto.setDiaSemana(h.getDiaSemana());
        dto.setHorarioApertura(h.getHorarioApertura());
        dto.setHorarioCierre(h.getHorarioCierre());
        dto.setActivo(h.isActivo());
        return dto;
    }

    private EmpleadoDto mapEmpleado(Empleado e) {
        EmpleadoDto dto = new EmpleadoDto();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setApellido(e.getApellido());
        dto.setEmail(e.getEmail());
        dto.setEspecialidad(e.getEspecialidad());
        dto.setRol(e.getRol());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getNombreDueno() {
        return nombreDueno;
    }

    public void setNombreDueno(String nombreDueno) {
        this.nombreDueno = nombreDueno;
    }

    public Double getPromedioCalificacion() {
        return promedioCalificacion;
    }

    public void setPromedioCalificacion(Double promedioCalificacion) {
        this.promedioCalificacion = promedioCalificacion;
    }

    public List<ImagenLocalDTO> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenLocalDTO> imagenes) {
        this.imagenes = imagenes;
    }

    public List<ServicioLocalDTO> getServicios() {
        return servicios;
    }

    public void setServicios(List<ServicioLocalDTO> servicios) {
        this.servicios = servicios;
    }

    public List<HorarioDTO> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioDTO> horarios) {
        this.horarios = horarios;
    }

    public List<EmpleadoDto> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<EmpleadoDto> empleados) {
        this.empleados = empleados;
    }
  
    

}
