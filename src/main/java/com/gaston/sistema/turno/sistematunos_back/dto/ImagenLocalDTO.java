package com.gaston.sistema.turno.sistematunos_back.dto;

public class ImagenLocalDTO {
    private Long id;
    private String nombre;
    private String urlDescarga;
   
    public ImagenLocalDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.urlDescarga = "http://localhost:8080/publico/locales/imagenes/" + id;
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
    public String getUrlDescarga() {
        return urlDescarga;
    }
    public void setUrlDescarga(String urlDescarga) {
        this.urlDescarga = urlDescarga;
    }

        
}
