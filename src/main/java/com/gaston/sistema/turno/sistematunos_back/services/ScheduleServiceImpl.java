package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.repositories.ScheduleRepository;

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
    public Schedule createShopSchedule(Schedule schedule, Long ownerId) {
        Shop shopDb = shopService.getByOwner(ownerId);
        shopDb.getSchedules().add(schedule);
        schedule.setShop(shopDb);
        log.info("Shop schedule created for shop id={} day={}", shopDb.getId(), schedule.getDayOfWeek());
        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public Schedule editShopSchedule(Schedule schedule, Long scheduleId, Long ownerId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new IllegalArgumentException("Schedule not found with id: " + scheduleId));
        if (!scheduleDb.getShop().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have permission to edit this schedule");
        }
        scheduleDb.setActive(schedule.isActive());
        scheduleDb.setDayOfWeek(schedule.getDayOfWeek());
        scheduleDb.setOpeningTime(schedule.getOpeningTime());
        scheduleDb.setClosingTime(schedule.getClosingTime());
        log.info("Shop schedule id={} updated", scheduleId);
        return scheduleRepository.save(scheduleDb);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getSchedules(Long ownerId) {
        Shop shopDb = shopService.getByOwner(ownerId);
        return scheduleRepository.findByShopId(shopDb.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Schedule getSchedule(Long scheduleId, Long ownerId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new IllegalArgumentException("Schedule not found with id: " + scheduleId));
        if (!scheduleDb.getShop().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have permission to view this schedule");
        }
        return scheduleDb;
    }

    @Override
    @Transactional
    public void deleteShopSchedule(Long scheduleId, Long ownerId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new IllegalArgumentException("Schedule not found with id: " + scheduleId));
        if (!scheduleDb.getShop().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have permission to delete this schedule");
        }
        scheduleRepository.delete(scheduleDb);
        log.info("Shop schedule id={} deleted", scheduleId);
    }

    // ===================== EMPLOYEE SCHEDULES =====================

    @Override
    @Transactional
    public Schedule createEmployeeSchedule(Schedule schedule, Long employeeId) {
        Employee employeeDb = employeeService.getEmployeeEntity(employeeId);
        validateScheduleWithinShopRange(schedule, employeeDb.getShop());
        employeeDb.getSchedules().add(schedule);
        schedule.setEmployee(employeeDb);
        log.info("Employee schedule created for employee id={} day={}", employeeId, schedule.getDayOfWeek());
        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public Schedule editEmployeeSchedule(Schedule schedule, Long scheduleId, Long employeeId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new IllegalArgumentException("Schedule not found with id: " + scheduleId));
        if (!scheduleDb.getEmployee().getId().equals(employeeId)) {
            throw new AccessDeniedException("You do not have permission to edit this schedule");
        }
        validateScheduleWithinShopRange(schedule, scheduleDb.getEmployee().getShop());
        scheduleDb.setActive(schedule.isActive());
        scheduleDb.setDayOfWeek(schedule.getDayOfWeek());
        scheduleDb.setOpeningTime(schedule.getOpeningTime());
        scheduleDb.setClosingTime(schedule.getClosingTime());
        log.info("Employee schedule id={} updated", scheduleId);
        return scheduleRepository.save(scheduleDb);
    }

    @Override
    @Transactional(readOnly = true)
    public Schedule getEmployeeSchedule(Long scheduleId, Long employeeId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new IllegalArgumentException("Schedule not found with id: " + scheduleId));
        if (!scheduleDb.getEmployee().getId().equals(employeeId)) {
            throw new AccessDeniedException("You do not have permission to view this schedule");
        }
        return scheduleDb;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getEmployeeSchedules(Long employeeId) {
        return scheduleRepository.findByEmployeeId(employeeId);
    }

    @Override
    @Transactional
    public void deleteEmployeeSchedule(Long scheduleId, Long employeeId) {
        Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new IllegalArgumentException("Schedule not found with id: " + scheduleId));
        if (!scheduleDb.getEmployee().getId().equals(employeeId)) {
            throw new AccessDeniedException("You do not have permission to delete this schedule");
        }
        scheduleRepository.delete(scheduleDb);
        log.info("Employee schedule id={} deleted", scheduleId);
    }

    // ===================== PRIVATE VALIDATION =====================

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
