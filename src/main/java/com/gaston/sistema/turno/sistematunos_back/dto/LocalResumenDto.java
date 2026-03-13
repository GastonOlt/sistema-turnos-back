package com.gaston.sistema.turno.sistematunos_back.dto;

/**
 * DTO ligero para la búsqueda/listado de locales.
 * Solo contiene los campos necesarios para la tarjeta visual.
 */
public class LocalResumenDto {
    private Long id;
    private String nombre;
    private String direccion;
    private String provincia;
    private String tipoLocal;
    private String telefono;
    private String nombreDueno;
    private Double promedioCalificacion;
    private String imagenPrincipal; // base64 de la primera imagen
    private String tipoContenidoImagen;

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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    public String getImagenPrincipal() {
        return imagenPrincipal;
    }

    public void setImagenPrincipal(String imagenPrincipal) {
        this.imagenPrincipal = imagenPrincipal;
    }

    public String getTipoContenidoImagen() {
        return tipoContenidoImagen;
    }

    public void setTipoContenidoImagen(String tipoContenidoImagen) {
        this.tipoContenidoImagen = tipoContenidoImagen;
    }
}
