package com.gaston.sistema.turno.sistematunos_back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gaston.sistema.turno.sistematunos_back.dto.ScheduleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ScheduleRequestDTO;
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
    public ResponseEntity<ScheduleDTO> createShopSchedule(@Valid @RequestBody ScheduleRequestDTO request,
                                                          @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        ScheduleDTO newSchedule = scheduleService.createShopSchedule(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSchedule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> editShopSchedule(@PathVariable Long id,
                                                        @Valid @RequestBody ScheduleRequestDTO request,
                                                        @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        ScheduleDTO updatedSchedule = scheduleService.editShopSchedule(request, id, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSchedule);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> getShopSchedules(@AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        List<ScheduleDTO> schedules = scheduleService.getSchedules(ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getShopSchedule(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        ScheduleDTO schedule = scheduleService.getSchedule(id, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(schedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShopSchedule(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserPrincipal user) {
        Long ownerId = user.getId();
        scheduleService.deleteShopSchedule(id, ownerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
