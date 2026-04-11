package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.repositories.ScheduleRepository;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ShopService shopService;
    private final EmployeeService employeeService;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, ShopService shopService,
            EmployeeService employeeService) {
        this.scheduleRepository = scheduleRepository;
        this.shopService = shopService;
        this.employeeService = employeeService;
    }

    @Override
    @Transactional
    public Schedule createShopSchedule(Schedule schedule, Long ownerId) {
            Shop shopDb = shopService.getByOwner(ownerId);
            shopDb.getSchedules().add(schedule);
            schedule.setShop(shopDb);
             return scheduleRepository.save(schedule);
        }

    @Override
    @Transactional
    public Schedule editShopSchedule(Schedule schedule, Long scheduleId, Long ownerId) {
            Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(()->
                                                new IllegalArgumentException("error al encontrar el horario"));
            if (!scheduleDb.getShop().getOwner().getId().equals(ownerId)) {
                 throw new AccessDeniedException("No tienes permisos para editar este horario");
            }
            scheduleDb.setActive(schedule.isActive());
            scheduleDb.setDayOfWeek(schedule.getDayOfWeek());
            scheduleDb.setOpeningTime(schedule.getOpeningTime());
            scheduleDb.setClosingTime(schedule.getClosingTime());

            return scheduleRepository.save(scheduleDb);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getSchedules(Long ownerId) {
        Shop shopDb = shopService.getByOwner(ownerId);
        return shopDb.getSchedules();
    }

    @Override
    @Transactional(readOnly = true)
    public Schedule getSchedule(Long scheduleId, Long ownerId) {
             Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(()->
                                                             new IllegalArgumentException("Horario no encontrado"));
             if(!scheduleDb.getShop().getOwner().getId().equals(ownerId)){
                throw new AccessDeniedException("No tienes permisos ver este horario");
             }
             return scheduleDb;
    }

    @Override
    @Transactional
    public void deleteShopSchedule(Long scheduleId, Long ownerId) {
            Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(()->
                                                            new IllegalArgumentException("Horario no encontrado"));
            if(!scheduleDb.getShop().getOwner().getId().equals(ownerId)){
                throw new AccessDeniedException("No tienes permisos eliminar este horario");
             }
            scheduleRepository.delete(scheduleDb);
    }

    //////// EMPLOYEE ////////

    @Override
    @Transactional
    public Schedule createBarberSchedule(Schedule schedule, Long employeeId) {
            Employee employeeDb = employeeService.getEmployeeEntity(employeeId);
            employeeDb.getSchedules().add(schedule);
            schedule.setEmployee(employeeDb);
            return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public Schedule editBarberSchedule(Schedule schedule, Long scheduleId, Long employeeId) {
            Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(()->
                                                             new IllegalArgumentException("error al obtener el horario"));
            if(!scheduleDb.getEmployee().getId().equals(employeeId)){
                throw new AccessDeniedException("no tienes permisos para editar este horario");
            }
            scheduleDb.setActive(schedule.isActive());
            scheduleDb.setDayOfWeek(schedule.getDayOfWeek());
            scheduleDb.setOpeningTime(schedule.getOpeningTime());
            scheduleDb.setClosingTime(schedule.getClosingTime());

            return scheduleRepository.save(scheduleDb);
    }

    @Override
    @Transactional(readOnly = true)
    public Schedule getBarberSchedule(Long scheduleId, Long employeeId) {
         Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(()->
                                                              new IllegalArgumentException("error al obtener el horario"));
            if(!scheduleDb.getEmployee().getId().equals(employeeId)){
                throw new AccessDeniedException("no tienes permisos para ver este horario");
             }
           return scheduleDb;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getBarberSchedules(Long employeeId) {
            Employee employeeDb = employeeService.getEmployeeEntity(employeeId);
            return employeeDb.getSchedules();
    }

    @Override
    @Transactional
    public void deleteBarberSchedule(Long scheduleId, Long employeeId) {
           Schedule scheduleDb = scheduleRepository.findById(scheduleId).orElseThrow(()->
                                                              new IllegalArgumentException("error al obtener el horario"));
            if(!scheduleDb.getEmployee().getId().equals(employeeId)){
                throw new AccessDeniedException("no tienes permisos para eliminar este horario");
             }
            scheduleRepository.delete(scheduleDb);
    }
}
