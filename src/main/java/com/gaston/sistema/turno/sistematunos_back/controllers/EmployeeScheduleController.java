package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ScheduleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ScheduleRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ScheduleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/employee/schedules")
public class EmployeeScheduleController {

    private final ScheduleService scheduleService;

    public EmployeeScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<ScheduleDTO> createEmployeeSchedule(@Valid @RequestBody ScheduleRequestDTO request,
                                                              @AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        ScheduleDTO newSchedule = scheduleService.createEmployeeSchedule(request, employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSchedule);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getEmployeeSchedule(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        ScheduleDTO schedule = scheduleService.getEmployeeSchedule(id, employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(schedule);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> getEmployeeSchedules(@AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        List<ScheduleDTO> schedules = scheduleService.getEmployeeSchedules(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> editEmployeeSchedule(@PathVariable Long id,
                                                            @Valid @RequestBody ScheduleRequestDTO request,
                                                            @AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        ScheduleDTO updatedSchedule = scheduleService.editEmployeeSchedule(request, id, employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSchedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployeeSchedule(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        scheduleService.deleteEmployeeSchedule(id, employeeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
