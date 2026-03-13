package com.gaston.sistema.turno.sistematunos_back.controllers;

import com.gaston.sistema.turno.sistematunos_back.dto.MetricasDashboardDTO;
import com.gaston.sistema.turno.sistematunos_back.security.JwtTokenProvider;
import com.gaston.sistema.turno.sistematunos_back.services.DashboardDuenoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/dueno/dashboard")
public class DashboardDuenoController {

    @Autowired
    private DashboardDuenoService dashboardDuenoService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/metricas")
    public ResponseEntity<MetricasDashboardDTO> obtenerMetricas(
            HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        String token = obtenerTokenDeRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }

        Long duenoId = jwtTokenProvider.getIdFromToken(token);

        MetricasDashboardDTO metricas = dashboardDuenoService.obtenerMetricasDashboard(duenoId, fechaInicio, fechaFin);

        return ResponseEntity.ok(metricas);
    }

    private String obtenerTokenDeRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
