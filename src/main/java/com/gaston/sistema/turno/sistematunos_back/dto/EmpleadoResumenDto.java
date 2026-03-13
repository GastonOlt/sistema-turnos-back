package com.gaston.sistema.turno.sistematunos_back.dto;

/**
 * DTO plano para empleados en vista de detalle.
 * PROHIBIDA referencia bidireccional a Local o ImagenLocal entity.
 */
public class EmpleadoResumenDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String especialidad;
    private boolean isDueno;
    private ImagenDto imagenEmpleado; // DTO plano, no entidad

    public EmpleadoResumenDto() {
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public boolean isDueno() {
        return isDueno;
    }

    public void setDueno(boolean dueno) {
        isDueno = dueno;
    }

    public ImagenDto getImagenEmpleado() {
        return imagenEmpleado;
    }

    public void setImagenEmpleado(ImagenDto imagenEmpleado) {
        this.imagenEmpleado = imagenEmpleado;
    }
}
