package com.gaston.sistema.turno.sistematunos_back.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
       
        try{
            String token = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("accessToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                    }
                }
            }

            if(StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)){
                Long id = jwtTokenProvider.getIdFromToken(token);

                UserPrincipal user = (UserPrincipal) customUserDetailsService.loadUserById(id);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null ,  user.getAuthorities());
                
                SecurityContextHolder.getContext().setAuthentication(auth);

            }

        } catch(Exception ex){
                logger.error("No se pudo setear el usuario Autenticado en el contexto de seguridad", ex);
            }
            filterChain.doFilter(request, response);
    }

}
