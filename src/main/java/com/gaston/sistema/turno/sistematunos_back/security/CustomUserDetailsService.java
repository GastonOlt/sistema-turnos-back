package com.gaston.sistema.turno.sistematunos_back.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gaston.sistema.turno.sistematunos_back.entities.Usuario;
import com.gaston.sistema.turno.sistematunos_back.repositories.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuarioDb = usuarioRepository.findByEmail(username).orElseThrow(() ->
                 new UsernameNotFoundException("usuario con ese email no encontrado :" + username));
        return UserPrincipal.crear(usuarioDb);
    }
    
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Usuario usuarioDb = usuarioRepository.findById(id).orElseThrow(() ->
                 new UsernameNotFoundException("usuario con ese id no encontrado :" + id));
        return UserPrincipal.crear(usuarioDb);
    }
}
