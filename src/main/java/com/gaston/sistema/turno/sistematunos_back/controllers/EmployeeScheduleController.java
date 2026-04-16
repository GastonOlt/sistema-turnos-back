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

import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;
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
    public ResponseEntity<Schedule> createEmployeeSchedule(@Valid @RequestBody Schedule schedule, @AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        Schedule newSchedule = scheduleService.createEmployeeSchedule(schedule, employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSchedule);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getEmployeeSchedule(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        Schedule schedule = scheduleService.getEmployeeSchedule(id, employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(schedule);
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> getEmployeeSchedules(@AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        List<Schedule> schedules = scheduleService.getEmployeeSchedules(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> editEmployeeSchedule(@PathVariable Long id, @RequestBody Schedule schedule,
                                                @AuthenticationPrincipal UserPrincipal user) {
        Long employeeId = user.getId();
        Schedule updatedSchedule = scheduleService.editEmployeeSchedule(schedule, id, employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSchedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployeeSchedule(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user){
         Long employeeId = user.getId();
         scheduleService.deleteEmployeeSchedule(id, employeeId);
          return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
