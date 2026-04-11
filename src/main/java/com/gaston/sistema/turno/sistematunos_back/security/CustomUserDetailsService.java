package com.gaston.sistema.turno.sistematunos_back.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gaston.sistema.turno.sistematunos_back.entities.User;
import com.gaston.sistema.turno.sistematunos_back.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userDb = userRepository.findByEmail(username).orElseThrow(() ->
                 new UsernameNotFoundException("usuario con ese email no encontrado :" + username));
        return UserPrincipal.create(userDb);
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User userDb = userRepository.findById(id).orElseThrow(() ->
                 new UsernameNotFoundException("usuario con ese id no encontrado :" + id));
        return UserPrincipal.create(userDb);
    }
}
