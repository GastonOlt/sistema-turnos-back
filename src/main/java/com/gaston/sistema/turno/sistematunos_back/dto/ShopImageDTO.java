package com.gaston.sistema.turno.sistematunos_back.dto;

public class ShopImageDTO {
    private Long id;
    private String name;
    private String downloadUrl;

    public ShopImageDTO(Long id, String name) {
        this.id = id;
        this.name = name;
        this.downloadUrl = "http://localhost:8080/publico/locales/imagenes/" + id;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDownloadUrl() {
        return downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
