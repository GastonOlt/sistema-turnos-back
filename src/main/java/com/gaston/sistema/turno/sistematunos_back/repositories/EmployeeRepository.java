package com.gaston.sistema.turno.sistematunos_back.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gaston.sistema.turno.sistematunos_back.entities.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("Select u From User u where u.email = ?1")
    Optional<Employee> findByEmail(String email);

    List<Employee> findByShopId(Long shopId);
}
