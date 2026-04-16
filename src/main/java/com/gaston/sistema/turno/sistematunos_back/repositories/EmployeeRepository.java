package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE e.email = ?1")
    Optional<Employee> findByEmail(String email);

    List<Employee> findByShopId(Long shopId);

    /** Returns only active employees (excludes deactivated owner ghost profiles). */
    List<Employee> findByShopIdAndActiveTrue(Long shopId);

    long countByShopId(Long shopId);

    /** Counts employees excluding a given role (used to exclude OWNER_PROVIDER ghost profiles from the 5-employee limit). */
    long countByShopIdAndRoleNot(Long shopId, String role);
}
