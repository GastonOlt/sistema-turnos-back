package com.gaston.sistema.turno.sistematunos_back.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "local")
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "ingrese un valor ")
    private String nombre;

    @NotBlank(message = "ingrese un valor ")
    private String descripcion;
    
    @NotBlank(message = "ingrese un valor ")
    private String direccion;
    @NotBlank(message = "ingrese un valor ")
    private String provincia;
    @NotBlank(message = "ingrese un valor ")
    private String telefono;
    @NotBlank(message = "ingrese un valor ")
    private String tipoLocal;

    private Double latitud;
    private Double longitud;

    @OneToOne(mappedBy = "local")
    private Dueno dueno;

    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenLocal> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horario> horarios = new ArrayList<>();

    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Empleado> empleados = new ArrayList<>();

    @OneToMany(mappedBy = "local",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicioLocal> servicios = new ArrayList<>();
    
    @OneToMany(mappedBy = "local",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Turno> turnos = new ArrayList<>();

    public void actualizarDatosLocal(Local local){
        if(local.nombre != null) this.nombre = local.getNombre();
        if(local.descripcion != null) this.descripcion = local.getDescripcion();
        if(local.direccion != null) this.direccion = local.getDireccion();
        if(local.provincia != null) this.provincia = local.getProvincia();
        if(local.telefono != null) this.telefono = local.getTelefono();
        if(local.tipoLocal != null) this.tipoLocal = local.getTipoLocal();
        if(local.latitud != null) this.latitud = local.getLatitud();
        if(local.longitud != null) this.longitud = local.getLongitud();
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

    public Dueno getDueno() {
        return dueno;
    }

    public void setDueno(Dueno dueno) {
        this.dueno = dueno;
    }

    public List<ImagenLocal> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenLocal> imagenes) {
        this.imagenes = imagenes;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    public String getTipoLocal() {
        return tipoLocal;
    }

    public void setTipoLocal(String tipoLocal) {
        this.tipoLocal = tipoLocal;
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

    public List<ServicioLocal> getServicios() {
        return servicios;
    }

    public void setServicios(List<ServicioLocal> servicios) {
        this.servicios = servicios;
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<Turno> turnos) {
        this.turnos = turnos;
    }

    
    
}
