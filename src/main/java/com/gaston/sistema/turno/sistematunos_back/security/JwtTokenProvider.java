package com.gaston.sistema.turno.sistematunos_back.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecretString;

    private final long jwtExpirationInMs = 360000000;

    private SecretKey secretKey;

    @PostConstruct
    public void init(){
        secretKey = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
    }

    public String generateToken(Authentication authentication){

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime()+jwtExpirationInMs);

        return Jwts.builder()
                    .setSubject(Long.toString(userPrincipal.getId()))
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey)
                    .compact();
    }

    public Long getIdFromToken(String token){
        Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException  e) {
           System.err.println("token JWT invalido "+token);
        }
        return false;
    }


}
