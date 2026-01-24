package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.EmpleadoDto;
import com.gaston.sistema.turno.sistematunos_back.dto.EmpleadoRequestDTO;

import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;

public interface EmpleadoService {
    EmpleadoDto crearEmpleado(EmpleadoRequestDTO empleadoDto, Long duenoId, MultipartFile archivo);

    EmpleadoDto editarEmpleado(EmpleadoRequestDTO empleadoDto, MultipartFile archivo, Long empleadoId, Long duenoId);

    void eliminarEmpleado(Long empleadoId, Long duenoId);

    EmpleadoDto obtenerEmpleado(Long empleadoId, Long duenoId);

    Empleado obtenerEmpleadoEntity(Long empleadoId);

    List<EmpleadoDto> obtenerEmpleados(Long duenoId);
}
