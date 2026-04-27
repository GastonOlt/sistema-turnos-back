package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.ScheduleDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ScheduleRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.repositories.ScheduleRepository;
import com.gaston.sistema.turno.sistematunos_back.validation.ResourceNotFoundException;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    private final ScheduleRepository scheduleRepository;
    private final ShopService shopService;
    private final EmployeeService employeeService;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, ShopService shopService,
            EmployeeService employeeService) {
        this.scheduleRepository = scheduleRepository;
        this.shopService = shopService;
        this.employeeService = employeeService;
    }

    // ===================== SHOP SCHEDULES =====================

    @Override
    @Transactional
    public ScheduleDTO createShopSchedule(ScheduleRequestDTO request, Long ownerId) {
        Shop shopDb = shopService.getByOwner(ownerId);
        Schedule schedule = toEntity(request);
        shopDb.getSchedules().add(schedule);
        schedule.setShop(shopDb);
        log.info("Shop schedule created for shop id={} day={}", shopDb.getId(), schedule.getDayOfWeek());
        return toDTO(scheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public ScheduleDTO editShopSchedule(ScheduleRequestDTO request, Long scheduleId, Long ownerId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ResourceNotFoundException("Schedule", scheduleId));
        if (!scheduleDb.getShop().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have permission to edit this schedule");
        }
        applyRequestToEntity(request, scheduleDb);
        log.info("Shop schedule id={} updated", scheduleId);
        return toDTO(scheduleRepository.save(scheduleDb));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getSchedules(Long ownerId) {
        Shop shopDb = shopService.getByOwner(ownerId);
        return scheduleRepository.findByShopId(shopDb.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleDTO getSchedule(Long scheduleId, Long ownerId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ResourceNotFoundException("Schedule", scheduleId));
        if (!scheduleDb.getShop().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have permission to view this schedule");
        }
        return toDTO(scheduleDb);
    }

    @Override
    @Transactional
    public void deleteShopSchedule(Long scheduleId, Long ownerId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ResourceNotFoundException("Schedule", scheduleId));
        if (!scheduleDb.getShop().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have permission to delete this schedule");
        }
        scheduleRepository.delete(scheduleDb);
        log.info("Shop schedule id={} deleted", scheduleId);
    }

    // ===================== EMPLOYEE SCHEDULES =====================

    @Override
    @Transactional
    public ScheduleDTO createEmployeeSchedule(ScheduleRequestDTO request, Long employeeId) {
        Employee employeeDb = employeeService.getEmployeeEntity(employeeId);
        Schedule schedule = toEntity(request);
        validateScheduleWithinShopRange(schedule, employeeDb.getShop());
        employeeDb.getSchedules().add(schedule);
        schedule.setEmployee(employeeDb);
        log.info("Employee schedule created for employee id={} day={}", employeeId, schedule.getDayOfWeek());
        return toDTO(scheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public ScheduleDTO editEmployeeSchedule(ScheduleRequestDTO request, Long scheduleId, Long employeeId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ResourceNotFoundException("Schedule", scheduleId));
        if (!scheduleDb.getEmployee().getId().equals(employeeId)) {
            throw new AccessDeniedException("You do not have permission to edit this schedule");
        }
        Schedule proposed = toEntity(request);
        validateScheduleWithinShopRange(proposed, scheduleDb.getEmployee().getShop());
        applyRequestToEntity(request, scheduleDb);
        log.info("Employee schedule id={} updated", scheduleId);
        return toDTO(scheduleRepository.save(scheduleDb));
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleDTO getEmployeeSchedule(Long scheduleId, Long employeeId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ResourceNotFoundException("Schedule", scheduleId));
        if (!scheduleDb.getEmployee().getId().equals(employeeId)) {
            throw new AccessDeniedException("You do not have permission to view this schedule");
        }
        return toDTO(scheduleDb);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getEmployeeSchedules(Long employeeId) {
        return scheduleRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteEmployeeSchedule(Long scheduleId, Long employeeId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new ResourceNotFoundException("Schedule", scheduleId));
        if (!scheduleDb.getEmployee().getId().equals(employeeId)) {
            throw new AccessDeniedException("You do not have permission to delete this schedule");
        }
        scheduleRepository.delete(scheduleDb);
        log.info("Employee schedule id={} deleted", scheduleId);
    }

    // ===================== PRIVATE HELPERS =====================

    private Schedule toEntity(ScheduleRequestDTO request) {
        Schedule schedule = new Schedule();
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setOpeningTime(request.getOpeningTime());
        schedule.setClosingTime(request.getClosingTime());
        schedule.setActive(request.isActive());
        return schedule;
    }

    private void applyRequestToEntity(ScheduleRequestDTO request, Schedule entity) {
        entity.setDayOfWeek(request.getDayOfWeek());
        entity.setOpeningTime(request.getOpeningTime());
        entity.setClosingTime(request.getClosingTime());
        entity.setActive(request.isActive());
    }

    private ScheduleDTO toDTO(Schedule entity) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(entity.getId());
        dto.setDayOfWeek(entity.getDayOfWeek());
        dto.setOpeningTime(entity.getOpeningTime());
        dto.setClosingTime(entity.getClosingTime());
        dto.setActive(entity.isActive());
        return dto;
    }

    /**
     * Validates that an employee's schedule falls within the shop's master schedule for the same day.
     * Rules:
     * - The day must exist and be active in the shop's schedule.
     * - openingTime cannot be earlier than the shop's openingTime for that day.
     * - closingTime cannot be later than the shop's closingTime for that day.
     */
    private void validateScheduleWithinShopRange(Schedule employeeSchedule, Shop shop) {
        String day = employeeSchedule.getDayOfWeek();

        Optional<Schedule> shopScheduleOpt = shop.getSchedules().stream()
                .filter(s -> s.getDayOfWeek().equalsIgnoreCase(day) && s.isActive())
                .findFirst();

        if (shopScheduleOpt.isEmpty()) {
            throw new IllegalArgumentException(
                    "The shop does not operate on '" + day + "'. Employee cannot have a schedule for this day.");
        }

        Schedule shopSchedule = shopScheduleOpt.get();

        if (employeeSchedule.getOpeningTime().isBefore(shopSchedule.getOpeningTime())) {
            throw new IllegalArgumentException(
                    "Employee opening time (" + employeeSchedule.getOpeningTime() +
                    ") cannot be earlier than the shop opening time (" + shopSchedule.getOpeningTime() + ") for " + day + ".");
        }

        if (employeeSchedule.getClosingTime().isAfter(shopSchedule.getClosingTime())) {
            throw new IllegalArgumentException(
                    "Employee closing time (" + employeeSchedule.getClosingTime() +
                    ") cannot be later than the shop closing time (" + shopSchedule.getClosingTime() + ") for " + day + ".");
        }
    }
}
