package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.EmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.Owner;
import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.repositories.EmployeeRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.OwnerRepository;

@Service
public class OwnerAttendanceServiceImpl implements OwnerAttendanceService {

    private static final Logger log = LoggerFactory.getLogger(OwnerAttendanceServiceImpl.class);
    private static final String OWNER_PROVIDER_ROLE = "OWNER_PROVIDER";

    private final OwnerRepository ownerRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeServiceImpl employeeServiceImpl;
    private final ShopService shopService;
    private final PasswordEncoder passwordEncoder;

    public OwnerAttendanceServiceImpl(OwnerRepository ownerRepository,
                                      EmployeeRepository employeeRepository,
                                      EmployeeServiceImpl employeeServiceImpl,
                                      ShopService shopService,
                                      PasswordEncoder passwordEncoder) {
        this.ownerRepository = ownerRepository;
        this.employeeRepository = employeeRepository;
        this.employeeServiceImpl = employeeServiceImpl;
        this.shopService = shopService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public EmployeeDTO toggleOwnerAttendance(Long ownerId, boolean available) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));

        Employee ghostProfile = owner.getEmployeeProfile();

        if (available) {
            if (ghostProfile == null) {
                // First activation: create the ghost Employee profile
                ghostProfile = createGhostProfile(owner);
                owner.setEmployeeProfile(ghostProfile);
                log.info("Ghost profile created for owner id={}, employee id={}", ownerId, ghostProfile.getId());
            } else {
                // Re-activation: simply re-enable the existing ghost profile
                ghostProfile.setActive(true);
                employeeRepository.save(ghostProfile);
                log.info("Ghost profile reactivated for owner id={}, employee id={}", ownerId, ghostProfile.getId());
            }
            owner.setAvailableToAttend(true);
        } else {
            if (ghostProfile != null) {
                // Deactivation: soft-disable, never delete (preserves appointment history)
                ghostProfile.setActive(false);
                employeeRepository.save(ghostProfile);
                log.info("Ghost profile deactivated for owner id={}, employee id={}", ownerId, ghostProfile.getId());
            }
            owner.setAvailableToAttend(false);
        }

        ownerRepository.save(owner);
        return employeeServiceImpl.toEmployeeDTO(owner.getEmployeeProfile());
    }

    /**
     * Creates an Employee ghost profile for the Owner.
     * The ghost Employee receives:
     * - A synthetic email ensuring no collision with real users.
     * - The OWNER_PROVIDER role to distinguish it from regular employees.
     * - A copy of all active schedules from the shop (same inheritance logic as regular employees).
     */
    private Employee createGhostProfile(Owner owner) {
        Shop shop = shopService.getByOwner(owner.getId());

        Employee ghost = new Employee();
        ghost.setName(owner.getName());
        ghost.setLastName(owner.getLastName());
        // Synthetic email: guaranteed unique and identifiable, never used for auth
        ghost.setEmail("ghost_owner_" + owner.getId() + "@sistema.internal");
        // Placeholder password — this account is never used to log in
        ghost.setPassword(passwordEncoder.encode("GHOST_ACCOUNT_NO_LOGIN_" + owner.getId()));
        ghost.setRole(OWNER_PROVIDER_ROLE);
        ghost.setActive(true);
        ghost.setShop(shop);

        Employee savedGhost = employeeRepository.save(ghost);

        // Inherit all active shop schedules (same logic as createEmployee)
        List<Schedule> shopSchedules = shop.getSchedules();
        for (Schedule shopSchedule : shopSchedules) {
            if (shopSchedule.isActive()) {
                Schedule ghostSchedule = new Schedule();
                ghostSchedule.setDayOfWeek(shopSchedule.getDayOfWeek());
                ghostSchedule.setOpeningTime(shopSchedule.getOpeningTime());
                ghostSchedule.setClosingTime(shopSchedule.getClosingTime());
                ghostSchedule.setActive(true);
                ghostSchedule.setEmployee(savedGhost);
                savedGhost.getSchedules().add(ghostSchedule);
            }
        }

        return employeeRepository.save(savedGhost);
    }
}
