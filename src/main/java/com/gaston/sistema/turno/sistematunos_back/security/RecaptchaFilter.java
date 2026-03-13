package com.gaston.sistema.turno.sistematunos_back.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaston.sistema.turno.sistematunos_back.services.RecaptchaValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RecaptchaFilter extends OncePerRequestFilter {

    @Autowired
    private RecaptchaValidationService recaptchaValidationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equals("/auth/login") && request.getMethod().equalsIgnoreCase("POST")) {

            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

            try {
                JsonNode jsonNode = objectMapper.readTree(cachedRequest.getInputStream());
                String captchaResponse = jsonNode.has("g-recaptcha-response")
                        ? jsonNode.get("g-recaptcha-response").asText()
                        : null;

                if (captchaResponse == null || captchaResponse.trim().isEmpty()
                        || !recaptchaValidationService.validateCaptcha(captchaResponse)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"error\": \"Validación de reCAPTCHA fallida o token ausente\"}");
                    return;
                }

                filterChain.doFilter(cachedRequest, response);
                return;
            } catch (Exception e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"Error procesando la solicitud\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
