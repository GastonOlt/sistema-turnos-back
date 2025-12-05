package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.EmpleadoDto;
import com.gaston.sistema.turno.sistematunos_back.entities.Empleado;
import com.gaston.sistema.turno.sistematunos_back.entities.ImagenLocal;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.repositories.EmpleadoRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ImagenLocalRepository;
import com.gaston.sistema.turno.sistematunos_back.validation.EmailExistenteException;

@Service
public class EmpleadoServiceImp implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private LocalService localService;

    @Autowired
    private ImagenLocalRepository imagenLocalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public EmpleadoDto crearEmpleado(Empleado empleado, Long duenoId , MultipartFile archivo) {
        try {
           if(empleadoRepository.findByEmail(empleado.getEmail()).isPresent()){
               throw new EmailExistenteException("Email ya registrado");
           }

           ImagenLocal imgEmpleado = new ImagenLocal();
           imgEmpleado.setNombreArchivo(archivo.getOriginalFilename());
           imgEmpleado.setTipoArchivo(archivo.getContentType());
           imgEmpleado.setDatosImagen(archivo.getBytes());

           empleado.setImagenEmpleado(imgEmpleado);

           Local localDb = localService.obtenerPorDueno(duenoId);
           if(localDb.getEmpleados().size() >=5 ){
              throw new IllegalArgumentException("No puedes tener mas de 5 empleados");
           }
           localDb.getEmpleados().add(empleado);

           empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
           empleado.setLocal(localDb);
           empleado.setRol("EMPLEADO");

           Empleado nuevoEmpleado = empleadoRepository.save(empleado);
        
           return  empleadoDto(nuevoEmpleado);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    
    @Override
    public EmpleadoDto editarEmpleado(Empleado empleado, MultipartFile archivo,Long empleadoId,Long duenoId) {
        try{
            Empleado empleadoDb = empleadoRepository.findById(empleadoId).orElseThrow(() ->
                                     new IllegalArgumentException("No se encontro el empleado con ese id "+empleado.getId()));

            if (!empleadoDb.getLocal().getDueno().getId().equals(duenoId)) {
              throw new AccessDeniedException("No tienes permisos para editar este empleado");
            }

            empleadoDb.setApellido(empleado.getApellido());
            empleadoDb.setEspecialidad(empleado.getEspecialidad());
            empleadoDb.setNombre(empleado.getNombre());
            empleadoDb.setEmail(empleado.getEmail());
            // empleadoDb.setPassword(empleado.getPassword());

            if(archivo != null && !archivo.isEmpty()){
                ImagenLocal img = empleadoDb.getImagenEmpleado();
                if (img != null) {
                    imagenLocalRepository.delete(img);
                }
                ImagenLocal imgEmpleado = new ImagenLocal();
                imgEmpleado.setNombreArchivo(archivo.getOriginalFilename());
                imgEmpleado.setTipoArchivo(archivo.getContentType());
                imgEmpleado.setDatosImagen(archivo.getBytes());

                empleadoDb.setImagenEmpleado(imgEmpleado);
            }

            Empleado empleadoEditado = empleadoRepository.save(empleadoDb);
            return empleadoDto(empleadoEditado);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
         }
    }
    

    @Override
    public void eliminarEmpleado(Long empleadoId,Long duenoId) {
        Empleado empleadoDb = empleadoRepository.findById(empleadoId).orElseThrow(()-> 
                         new IllegalArgumentException("no se encontro el empleado con ese id" + empleadoId ));

         if (!empleadoDb.getLocal().getDueno().getId().equals(duenoId)) {
              throw new AccessDeniedException("No tienes permisos para eliminar este empleado");
        }
        empleadoRepository.deleteById(empleadoId);
     }

     
     @Override
     public List<EmpleadoDto> obtenerEmpleados(Long duenoId) {
        Local localDb = localService.obtenerPorDueno(duenoId);
         return empleadoRepository.findByLocalId(localDb.getId()).stream().map(emple -> empleadoDto(emple)).toList();
      }
        
    @Override
    public EmpleadoDto obtenerEmpleado(Long empleadoId,Long duenoId) {
        Empleado empleadoDb = empleadoRepository.findById(empleadoId).orElseThrow(()-> 
                            new IllegalArgumentException("no se encontro el empleado con ese id" + empleadoId ));

        if (!empleadoDb.getLocal().getDueno().getId().equals(duenoId)) {
              throw new AccessDeniedException("No tienes permisos para ver este empleado");
        }
          return empleadoDto(empleadoDb);
    }

    @Override
    public Empleado obtenerEmpleadoEntity(Long empleadoId) {
        Empleado empl = empleadoRepository.findById(empleadoId).orElseThrow(()-> 
                                new IllegalArgumentException("error al encontrar al empleado"));
        return empl;
    }


    public EmpleadoDto empleadoDto(Empleado empleado){
            EmpleadoDto respDto = new EmpleadoDto();
            respDto.setId(empleado.getId());
            respDto.setApellido(empleado.getApellido());
            respDto.setNombre(empleado.getNombre());
            respDto.setEmail(empleado.getEmail());
            respDto.setRol(empleado.getRol());
            respDto.setEspecialidad(empleado.getEspecialidad());

            if(empleado.getImagenEmpleado() != null){
            respDto.setDatosImagen(Base64.getEncoder().encodeToString(empleado.getImagenEmpleado().getDatosImagen()));
            respDto.setTipoContenido(empleado.getImagenEmpleado().getTipoArchivo());
            }
            return respDto;
    }


}
