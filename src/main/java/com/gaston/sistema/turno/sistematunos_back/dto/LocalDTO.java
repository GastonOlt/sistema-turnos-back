package com.gaston.sistema.turno.sistematunos_back.dto;

import java.util.ArrayList;
import java.util.List;

import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.Horario;
import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
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
    private List<ImagenLocal> imagenes = new ArrayList<>();
    private List<ServicioLocal> servicios = new ArrayList<>();
    private List<Horario> horarios = new ArrayList<>();
    private List<Empleado> empleados = new ArrayList<>();

    
    public LocalDTO(Local local) {
        this.id = local.getId();
        this.nombre = local.getNombre();
        this.descripcion = local.getDescripcion();
        this.provincia = local.getProvincia();
        this.telefono = local.getTelefono();
        this.latitud = local.getLatitud();
        this.longitud = local.getLongitud();
        this.nombreDueno = local.getDueno().getNombre();
        this.imagenes = local.getImagenes();
        this.horarios = local.getHorarios();
        this.empleados = local.getEmpleados();
        this.servicios= local.getServicios();
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


    public List<ImagenLocal> getImagenes() {
        return imagenes;
    }


    public void setImagenes(List<ImagenLocal> imagenes) {
        this.imagenes = imagenes;
    }


    public List<Horario> getHorarios() {
        return horarios;
    }


    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }


    public List<Empleado> getEmpleados() {
        return empleados;
    }


    public void setEmpleados(List<Empleado> empleados) {
        this.empleados = empleados;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getProvincia() {
        return provincia;
    }


    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }


    public List<ServicioLocal> getServicios() {
        return servicios;
    }


    public void setServicios(List<ServicioLocal> servicios) {
        this.servicios = servicios;
    }


    public String getTelefono() {
        return telefono;
    }


    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    

}
