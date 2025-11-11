package com.gaston.sistema.turno.sistematunos_back.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gaston.sistema.turno.sistematunos_back.entities.Usuario;

public class UserPrincipal implements UserDetails{

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    
    public UserPrincipal(Long id, String nombre, String apellido, String email, String password,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal crear(Usuario usuario){
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+usuario.getRol());
        return new UserPrincipal(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getEmail(),
            usuario.getPassword(),
           Collections.singletonList(authority));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return authorities;
    }

    @Override
    public String getPassword() {
       return password;
    }

    @Override
    public String getUsername() {
        return email;
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

    public void setPassword(String password) {
        this.password = password;
    }


    

}
