package com.gaston.sistema.turno.sistematunos_back.dto;

public class AuthTokenDTO {
    private String accessToken;
    private String refreshToken;
    private long refreshTokenDuration;

    public AuthTokenDTO() {
    }

    public AuthTokenDTO(String accessToken, String refreshToken, long refreshTokenDuration) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshTokenDuration = refreshTokenDuration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getRefreshTokenDuration() {
        return refreshTokenDuration;
    }

    public void setRefreshTokenDuration(long refreshTokenDuration) {
        this.refreshTokenDuration = refreshTokenDuration;
    }
}
