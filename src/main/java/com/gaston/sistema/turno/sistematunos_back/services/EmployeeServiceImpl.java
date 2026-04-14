package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.Base64;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.EmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopImage;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.repositories.EmployeeRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopImageRepository;
import com.gaston.sistema.turno.sistematunos_back.validation.EmailAlreadyExistsException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ShopService shopService;
    private final ShopImageRepository shopImageRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ShopService shopService,
            ShopImageRepository shopImageRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.shopService = shopService;
        this.shopImageRepository = shopImageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public EmployeeDTO createEmployee(Employee employee, Long ownerId, MultipartFile file) {
           if(employeeRepository.findByEmail(employee.getEmail()).isPresent()){
               throw new EmailAlreadyExistsException("Email ya registrado");
           }

           Shop shopDb = shopService.getByOwner(ownerId);
           if (employeeRepository.countByShopId(shopDb.getId()) >= 5) {
              throw new IllegalArgumentException("No puedes tener mas de 5 empleados");
           }

           try{
                if (file != null && !file.isEmpty()) {
                    ShopImage employeeImage = new ShopImage();
                    employeeImage.setFileName(file.getOriginalFilename());
                    employeeImage.setFileType(file.getContentType());
                    employeeImage.setImageData(file.getBytes());
                    employee.setEmployeeImage(employeeImage);
                }
           }catch(Exception e){
              throw new RuntimeException("Error al guardar la imagen del empleado: " + e);
           }

           shopDb.getEmployees().add(employee);

           employee.setPassword(passwordEncoder.encode(employee.getPassword()));
           employee.setShop(shopDb);
           employee.setRole("EMPLEADO");

           Employee newEmployee = employeeRepository.save(employee);

           return toEmployeeDTO(newEmployee);
    }

    @Override
    public EmployeeDTO editEmployee(Employee employee, MultipartFile file, Long employeeId, Long ownerId) {
            Employee employeeDb = employeeRepository.findById(employeeId).orElseThrow(() ->
                                     new IllegalArgumentException("No se encontro el empleado con ese id " + employeeId));

            if (!employeeDb.getShop().getOwner().getId().equals(ownerId)) {
              throw new AccessDeniedException("No tienes permisos para editar este empleado");
            }

            employeeDb.setLastName(employee.getLastName());
            employeeDb.setSpecialty(employee.getSpecialty());
            employeeDb.setName(employee.getName());
            employeeDb.setEmail(employee.getEmail());

            try{
                if(file != null && !file.isEmpty()){
                    ShopImage img = employeeDb.getEmployeeImage();
                    if (img != null) {
                        shopImageRepository.delete(img);
                    }
                    ShopImage employeeImage = new ShopImage();
                    employeeImage.setFileName(file.getOriginalFilename());
                    employeeImage.setFileType(file.getContentType());
                    employeeImage.setImageData(file.getBytes());
                    employeeDb.setEmployeeImage(employeeImage);
               }
            } catch (Exception e) {
                throw new RuntimeException("Error al actualizar la imagen del empleado: " + e);
            }

            Employee editedEmployee = employeeRepository.save(employeeDb);
            return toEmployeeDTO(editedEmployee);
    }

    @Override
    public void deleteEmployee(Long employeeId, Long ownerId) {
        Employee employeeDb = employeeRepository.findById(employeeId).orElseThrow(()->
                         new IllegalArgumentException("no se encontro el empleado con ese id" + employeeId));

         if (!employeeDb.getShop().getOwner().getId().equals(ownerId)) {
              throw new AccessDeniedException("No tienes permisos para eliminar este empleado");
        }
        employeeRepository.deleteById(employeeId);
     }

     @Override
     public List<EmployeeDTO> getEmployees(Long ownerId) {
        Shop shopDb = shopService.getByOwner(ownerId);
         return employeeRepository.findByShopId(shopDb.getId()).stream()
                                     .map(emp -> toEmployeeDTO(emp))
                                     .toList();
      }

    @Override
    public EmployeeDTO getEmployee(Long employeeId, Long ownerId) {
        Employee employeeDb = employeeRepository.findById(employeeId).orElseThrow(()->
                            new IllegalArgumentException("no se encontro el empleado con ese id" + employeeId));

        if (!employeeDb.getShop().getOwner().getId().equals(ownerId)) {
              throw new AccessDeniedException("No tienes permisos para ver este empleado");
        }
          return toEmployeeDTO(employeeDb);
    }

    @Override
    public Employee getEmployeeEntity(Long employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(()->
                                new IllegalArgumentException("error al encontrar al empleado"));
    }

    public EmployeeDTO toEmployeeDTO(Employee employee){
            EmployeeDTO dto = new EmployeeDTO();
            dto.setId(employee.getId());
            dto.setLastName(employee.getLastName());
            dto.setName(employee.getName());
            dto.setEmail(employee.getEmail());
            dto.setRole(employee.getRole());
            dto.setSpecialty(employee.getSpecialty());

            if(employee.getEmployeeImage() != null){
                dto.setImageData(Base64.getEncoder().encodeToString(employee.getEmployeeImage().getImageData()));
                dto.setContentType(employee.getEmployeeImage().getFileType());
            }
            return dto;
    }
}
