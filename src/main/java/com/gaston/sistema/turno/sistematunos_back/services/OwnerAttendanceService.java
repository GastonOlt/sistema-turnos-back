package com.gaston.sistema.turno.sistematunos_back.services;

import com.gaston.sistema.turno.sistematunos_back.dto.EmployeeDTO;

public interface OwnerAttendanceService {

    /**
     * Toggles the owner's availability to attend appointments.
     *
     * If {@code available} is {@code true}:
     * - Creates a ghost Employee profile linked to the owner (only on the first activation).
     * - On subsequent activations, reactivates the existing ghost profile.
     *
     * If {@code available} is {@code false}:
     * - Deactivates the ghost Employee profile (soft-disable, never deleted).
     *
     * @param ownerId   the ID of the owner
     * @param available the desired attendance availability
     * @return EmployeeDTO representing the ghost profile
     */
    EmployeeDTO toggleOwnerAttendance(Long ownerId, boolean available);
}
