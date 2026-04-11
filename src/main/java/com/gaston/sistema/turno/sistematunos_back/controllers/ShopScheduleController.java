package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;
import com.gaston.sistema.turno.sistematunos_back.security.UserPrincipal;
import com.gaston.sistema.turno.sistematunos_back.services.ScheduleService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/owner/schedules")
public class ShopScheduleController {

    private final ScheduleService scheduleService;

    public ShopScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<Schedule> createShopSchedule(@Valid @RequestBody Schedule schedule, @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        Schedule newSchedule = scheduleService.createShopSchedule(schedule, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSchedule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> editShopSchedule(@PathVariable Long id, @RequestBody Schedule schedule,
                                                @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        Schedule updatedSchedule = scheduleService.editShopSchedule(schedule, id, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSchedule);
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> getShopSchedules(@AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        List<Schedule> schedules = scheduleService.getSchedules(ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getShopSchedule(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        Schedule schedule = scheduleService.getSchedule(id, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(schedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShopSchedule(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user){
         Long ownerId = user.getId();
         scheduleService.deleteShopSchedule(id, ownerId);
          return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
