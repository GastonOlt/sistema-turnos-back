package com.gaston.sistema.turno.sistematunos_back.dto;

import java.util.Base64;

/**
 * DTO plano para imágenes. Sin referencia a entidades JPA.
 */
public class ImagenDto {
    private Long id;
    private String datosImagen; // base64
    private String tipoArchivo;
    private String nombreArchivo;

    public ImagenDto() {
    }

    public ImagenDto(Long id, byte[] datos, String tipoArchivo, String nombreArchivo) {
        this.id = id;
        this.tipoArchivo = tipoArchivo;
        this.nombreArchivo = nombreArchivo;
        if (datos != null) {
            this.datosImagen = Base64.getEncoder().encodeToString(datos);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatosImagen() {
        return datosImagen;
    }

    public void setDatosImagen(String datosImagen) {
        this.datosImagen = datosImagen;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
}
