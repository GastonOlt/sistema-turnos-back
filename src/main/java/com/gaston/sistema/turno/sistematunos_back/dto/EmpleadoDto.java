package com.gaston.sistema.turno.sistematunos_back.dto;




public class EmpleadoDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private String especialidad;
    private String datosImagen; 
    private String tipoContenido;
   

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
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }
    public String getDatosImagen() {
        return datosImagen;
    }
    public void setDatosImagen(String datosImagen) {
        this.datosImagen = datosImagen;
    }
    public String getTipoContenido() {
        return tipoContenido;
    }
    public void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }
    public String getEspecialidad() {
        return especialidad;
    }
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    
}
