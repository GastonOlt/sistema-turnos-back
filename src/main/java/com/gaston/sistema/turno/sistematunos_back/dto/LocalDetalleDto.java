package com.gaston.sistema.turno.sistematunos_back.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO completo para la vista de detalle de un local.
 * Jackson serializa SOLO esta clase — nunca entidades @Entity.
 */
public class LocalDetalleDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String direccion;
    private String provincia;
    private String telefono;
    private String tipoLocal;
    private Double latitud;
    private Double longitud;

    // Dueño como objeto plano
    private DuenoInfo dueno;

    // Colecciones de DTOs planos
    private List<ImagenDto> imagenes = new ArrayList<>();
    private List<HorarioDto> horarios = new ArrayList<>();
    private List<ServicioDto> servicios = new ArrayList<>();
    private List<EmpleadoResumenDto> empleados = new ArrayList<>();

    public LocalDetalleDto() {
    }

    // --- Dueño info interna ---
    public static class DuenoInfo {
        private String nombre;
        private String apellido;

        public DuenoInfo() {
        }

        public DuenoInfo(String nombre, String apellido) {
            this.nombre = nombre;
            this.apellido = apellido;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }
    }

    // --- Getters / Setters ---
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getTipoLocal() {
        return tipoLocal;
    }

    public void setTipoLocal(String tipoLocal) {
        this.tipoLocal = tipoLocal;
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

    public DuenoInfo getDueno() {
        return dueno;
    }

    public void setDueno(DuenoInfo dueno) {
        this.dueno = dueno;
    }

    public List<ImagenDto> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenDto> imagenes) {
        this.imagenes = imagenes;
    }

    public List<HorarioDto> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioDto> horarios) {
        this.horarios = horarios;
    }

    public List<ServicioDto> getServicios() {
        return servicios;
    }

    public void setServicios(List<ServicioDto> servicios) {
        this.servicios = servicios;
    }

    public List<EmpleadoResumenDto> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<EmpleadoResumenDto> empleados) {
        this.empleados = empleados;
    }
}
